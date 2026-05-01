import random
from datetime import date, datetime as dt
from sqlalchemy.orm import Session
from app.models import Escala, EscalaMinistro, Evento, Ministro, Indisponibilidade
from app.schemas import (
    EscalaIn, EscalaOut, EscalaMinistroOut, EventoOut,
    PreviewEscalaOut, MinistroEmpatadoOut, MinistroExcluidoOut,
)
from app.services import auditoria_service


def _to_out(e: Escala) -> EscalaOut:
    ems = [
        EscalaMinistroOut(
            id=em.id,
            escala_id=e.id,
            ministro_id=em.ministro_id,
            ministro_nome=em.ministro.nome if em.ministro else None,
            ministro_funcao=em.ministro.funcao if em.ministro else None,
            confirmacao_ministro=em.confirmacao_ministro,
            data_confirmacao=em.data_confirmacao,
            substituido=em.substituido,
        )
        for em in e.escala_ministros
        if not em.substituido
    ]
    return EscalaOut(
        id=e.id,
        data_atribuicao=e.data_atribuicao,
        observacao=e.observacao,
        status=e.status,
        evento_id=e.evento_id,
        evento=EventoOut.model_validate(e.evento) if e.evento else None,
        escala_ministros=ems,
    )


def _escala_detalhes(escala: Escala) -> str:
    evento = escala.evento
    if not evento:
        return f"Escala {escala.id} sem evento associado"
    horario = evento.horario or "horário não informado"
    return f"{evento.nome} — {evento.data} {horario} @ {evento.local or 'local não informado'}"


def _filtrar_disponiveis(
    db: Session,
    candidatos: list[Ministro],
    evento_data: date,
    evento_horario: str | None,
    evento_id: int | None = None,
    excluir_ids: set[int] | None = None,
) -> list[Ministro]:
    """Remove candidatos com indisponibilidade registrada e quem já está escalado no mesmo dia.

    Regras:
    - Indisponibilidade sem horário = dia inteiro bloqueado.
    - Indisponibilidade com horário = bloqueado se horário do evento cair dentro da janela.
    - Ministro já escalado em outro evento no mesmo dia é excluído.
    """
    if not candidatos:
        return []

    candidatos = _filtrar_escalados_mesmo_dia(db, candidatos, evento_data, evento_id)
    if not candidatos:
        return []

    ids = [m.id for m in candidatos]
    indisps = (
        db.query(Indisponibilidade)
        .filter(Indisponibilidade.ministro_id.in_(ids), Indisponibilidade.data == evento_data)
        .all()
    )
    if not indisps:
        return candidatos

    por_ministro: dict[int, list[Indisponibilidade]] = {}
    for ind in indisps:
        por_ministro.setdefault(ind.ministro_id, []).append(ind)

    def _disponivel(m: Ministro) -> bool:
        for ind in por_ministro.get(m.id, []):
            if not ind.horario_inicio:
                return False  # dia inteiro indisponível
            if not evento_horario:
                return False  # sem horário no evento, considera bloqueado
            try:
                ev = dt.strptime(evento_horario, "%H:%M").time()
                ini = dt.strptime(ind.horario_inicio, "%H:%M").time()
                fim = dt.strptime(ind.horario_fim, "%H:%M").time() if ind.horario_fim else ini
                if ini <= ev <= fim:
                    return False
            except ValueError:
                return False
        return True

    disponiveis = [m for m in candidatos if _disponivel(m)]
    if excluir_ids:
        disponiveis = [m for m in disponiveis if m.id not in excluir_ids]
    return disponiveis


def _filtrar_escalados_mesmo_dia(
    db: Session,
    candidatos: list[Ministro],
    evento_data: date,
    evento_id: int | None = None,
) -> list[Ministro]:
    if not candidatos:
        return []

    ids = [m.id for m in candidatos]
    query = (
        db.query(EscalaMinistro.ministro_id)
        .join(Escala)
        .join(Evento)
        .filter(EscalaMinistro.ministro_id.in_(ids), Evento.data == evento_data)
    )
    if evento_id is not None:
        query = query.filter(Escala.evento_id != evento_id)

    escalados_ids = {row[0] for row in query.all()}
    return [m for m in candidatos if m.id not in escalados_ids]


def _selecionar_ministros(candidatos: list[Ministro], vagas: int) -> list[Ministro]:
    """Seleciona ministros priorizando quem tem menos escalas no mês.

    Invariante mantida: max(escalas_mes) - min(escalas_mes) ≤ 1 após cada rodada.
    Empates são resolvidos por ordem aleatória.
    """
    if not candidatos:
        return []
    pool = candidatos[:]
    random.shuffle(pool)
    pool.sort(key=lambda m: m.escalas_mes or 0)
    return pool[: min(vagas, len(pool))]


def listar(db: Session) -> list[EscalaOut]:
    escalas = db.query(Escala).all()
    return [_to_out(e) for e in escalas]


def obter(db: Session, escala_id: int) -> EscalaOut | None:
    e = db.get(Escala, escala_id)
    return _to_out(e) if e else None


def criar(db: Session, data: EscalaIn) -> EscalaOut:
    if not db.get(Evento, data.evento_id):
        raise ValueError(f"Evento não encontrado: {data.evento_id}")
    escala = Escala(evento_id=data.evento_id, observacao=data.observacao, status="PROPOSTA")
    db.add(escala)
    db.flush()
    auditoria_service.registrar(db, "Escala", "CRIADO", None, f"CRIADO — {_escala_detalhes(escala)}")
    db.commit()
    db.refresh(escala)
    return _to_out(escala)


def substituir(db: Session, escala_id: int, ministro_id: int, substituto_id: int | None = None) -> EscalaOut:
    escala = db.get(Escala, escala_id)
    if not escala:
        raise ValueError(f"Escala não encontrada: {escala_id}")

    escala_ministro = (
        db.query(EscalaMinistro)
        .filter(EscalaMinistro.escala_id == escala_id, EscalaMinistro.ministro_id == ministro_id, EscalaMinistro.substituido == False)
        .first()
    )
    if not escala_ministro:
        raise ValueError(f"Ministro não encontrado nesta escala ou já substituído: {ministro_id}")

    evento = escala.evento
    if not evento:
        raise ValueError("Evento associado à escala não encontrado")

    candidatos = db.query(Ministro).filter(Ministro.ativo == True).all()
    if not candidatos:
        raise ValueError("Nenhum ministro ativo disponível")

    ids_excluir = {em.ministro_id for em in escala.escala_ministros if not em.substituido}

    candidatos = _filtrar_disponiveis(
        db,
        candidatos,
        evento.data,
        evento.horario,
        evento_id=evento.id,
        excluir_ids=ids_excluir,
    )
    if not candidatos:
        raise ValueError("Nenhum ministro disponível para substituição nesta data")

    if substituto_id is not None:
        substituto = next((m for m in candidatos if m.id == substituto_id), None)
        if not substituto:
            raise ValueError(f"Ministro escolhido não está disponível para substituição: {substituto_id}")
    else:
        substituto = _selecionar_ministros(candidatos, 1)[0]

    escala_ministro.substituido = True
    antigo = db.get(Ministro, ministro_id)
    if antigo:
        antigo.escalas_mes = max(0, (antigo.escalas_mes or 0) - 1)

    novo = substituto
    novo.escalas_mes = (novo.escalas_mes or 0) + 1
    novo_em = EscalaMinistro(escala_id=escala_id, ministro_id=novo.id)
    db.add(novo_em)

    auditoria_service.registrar(
        db,
        "Escala",
        "SUBSTITUIDO",
        str(ministro_id),
        f"SUBSTITUÍDO {antigo.nome if antigo else ministro_id} → {novo.nome} — {_escala_detalhes(escala)}",
    )
    db.commit()
    db.refresh(escala)
    return _to_out(escala)


def _motivo_exclusao(ind: Indisponibilidade, evento_horario: str | None) -> str:
    if not ind.horario_inicio:
        motivo = "Indisponível o dia todo"
    elif not evento_horario:
        motivo = "Indisponível (evento sem horário definido)"
    else:
        fim_str = f"–{ind.horario_fim}" if ind.horario_fim else ""
        motivo = f"Indisponível das {ind.horario_inicio}{fim_str}"
    if ind.motivo:
        motivo += f" ({ind.motivo})"
    return motivo


def _build_preview(
    db: Session,
    evento: Evento,
    candidatos: list[Ministro],
    excluir_ids: set[int] | None = None,
    validar_cancelado: bool = True,
    vagas_override: int | None = None,
) -> PreviewEscalaOut:
    if validar_cancelado and evento.cancelado:
        raise ValueError("Evento cancelado")

    vagas = vagas_override if vagas_override is not None else evento.max_ministros or 6
    ids = [m.id for m in candidatos]
    indisps = (
        db.query(Indisponibilidade)
        .filter(Indisponibilidade.ministro_id.in_(ids), Indisponibilidade.data == evento.data)
        .all()
    ) if ids else []

    escalados_mesmo_dia = set(
        row[0]
        for row in (
            db.query(EscalaMinistro.ministro_id)
            .join(Escala)
            .join(Evento)
            .filter(
                EscalaMinistro.ministro_id.in_(ids),
                Evento.data == evento.data,
                Escala.evento_id != evento.id,
            )
            .all()
        )
    ) if ids else set()

    por_ministro: dict[int, list[Indisponibilidade]] = {}
    for ind in indisps:
        por_ministro.setdefault(ind.ministro_id, []).append(ind)

    excluidos: list[MinistroExcluidoOut] = []
    disponiveis: list[Ministro] = []

    for m in candidatos:
        if excluir_ids and m.id in excluir_ids:
            excluidos.append(MinistroExcluidoOut(
                id=m.id,
                nome=m.nome,
                funcao=m.funcao,
                escalas_mes=m.escalas_mes or 0,
                motivo_exclusao="Não pode ser selecionado para este sorteio",
            ))
            continue

        if m.id in escalados_mesmo_dia:
            excluidos.append(MinistroExcluidoOut(
                id=m.id,
                nome=m.nome,
                funcao=m.funcao,
                escalas_mes=m.escalas_mes or 0,
                motivo_exclusao="Já escalado em outro evento neste dia",
            ))
            continue

        bloqueado = False
        motivo = ""
        for ind in por_ministro.get(m.id, []):
            if not ind.horario_inicio:
                bloqueado = True
                motivo = _motivo_exclusao(ind, evento.horario)
                break
            if not evento.horario:
                bloqueado = True
                motivo = _motivo_exclusao(ind, None)
                break
            try:
                ev = dt.strptime(evento.horario, "%H:%M").time()
                ini = dt.strptime(ind.horario_inicio, "%H:%M").time()
                fim = dt.strptime(ind.horario_fim, "%H:%M").time() if ind.horario_fim else ini
                if ini <= ev <= fim:
                    bloqueado = True
                    motivo = _motivo_exclusao(ind, evento.horario)
                    break
            except ValueError:
                bloqueado = True
                motivo = _motivo_exclusao(ind, evento.horario)
                break

        if bloqueado:
            excluidos.append(MinistroExcluidoOut(
                id=m.id,
                nome=m.nome,
                funcao=m.funcao,
                escalas_mes=m.escalas_mes or 0,
                motivo_exclusao=motivo,
            ))
        else:
            disponiveis.append(m)

    disponiveis.sort(key=lambda m: m.escalas_mes or 0)

    def _to_emp(m: Ministro) -> MinistroEmpatadoOut:
        return MinistroEmpatadoOut(id=m.id, nome=m.nome, funcao=m.funcao, escalas_mes=m.escalas_mes or 0)

    if len(disponiveis) <= vagas:
        return PreviewEscalaOut(
            evento_id=evento.id,
            vagas=vagas,
            definitivos=[_to_emp(m) for m in disponiveis],
            empatados=[],
            vagas_no_empate=0,
            selecionados_auto=[m.id for m in disponiveis],
            excluidos=excluidos,
            tem_empate=False,
        )

    cutoff_value = disponiveis[vagas - 1].escalas_mes or 0
    definitivos_m = [m for m in disponiveis if (m.escalas_mes or 0) < cutoff_value]
    empatados_m = [m for m in disponiveis if (m.escalas_mes or 0) == cutoff_value]
    vagas_no_empate = vagas - len(definitivos_m)

    shuffled = empatados_m[:]
    random.shuffle(shuffled)
    selecionados_auto = [m.id for m in shuffled[:vagas_no_empate]]

    return PreviewEscalaOut(
        evento_id=evento.id,
        vagas=vagas,
        definitivos=[_to_emp(m) for m in definitivos_m],
        empatados=[_to_emp(m) for m in empatados_m],
        vagas_no_empate=vagas_no_empate,
        selecionados_auto=selecionados_auto,
        excluidos=excluidos,
        tem_empate=len(empatados_m) > vagas_no_empate,
    )


def preview(db: Session, evento_id: int) -> PreviewEscalaOut:
    evento = db.get(Evento, evento_id)
    if not evento:
        raise ValueError(f"Evento não encontrado: {evento_id}")

    todos = db.query(Ministro).filter(Ministro.ativo == True).all()
    return _build_preview(db, evento, todos)


def preview_substituicao(db: Session, escala_id: int, ministro_id: int) -> PreviewEscalaOut:
    escala = db.get(Escala, escala_id)
    if not escala:
        raise ValueError(f"Escala não encontrada: {escala_id}")
    evento = escala.evento
    if not evento:
        raise ValueError("Evento associado à escala não encontrado")

    escala_ministro = (
        db.query(EscalaMinistro)
        .filter(EscalaMinistro.escala_id == escala_id, EscalaMinistro.ministro_id == ministro_id, EscalaMinistro.substituido == False)
        .first()
    )
    if not escala_ministro:
        raise ValueError(f"Ministro não encontrado nesta escala ou já substituído: {ministro_id}")

    todos = db.query(Ministro).filter(Ministro.ativo == True).all()
    excluir_ids = {em.ministro_id for em in escala.escala_ministros if not em.substituido and em.ministro_id != ministro_id}
    excluir_ids.add(ministro_id)

    return _build_preview(db, evento, todos, excluir_ids=excluir_ids, validar_cancelado=False, vagas_override=1)


def gerar(db: Session, evento_id: int, ministro_ids_manuais: list[int] | None = None) -> EscalaOut:
    evento = db.get(Evento, evento_id)
    if not evento:
        raise ValueError(f"Evento não encontrado: {evento_id}")
    if evento.cancelado:
        raise ValueError("Não é possível gerar escala para evento cancelado")

    candidatos = db.query(Ministro).filter(Ministro.ativo == True).all()
    if not candidatos:
        raise ValueError("Nenhum ministro ativo disponível")

    candidatos = _filtrar_disponiveis(db, candidatos, evento.data, evento.horario, evento_id=evento_id)
    if not candidatos:
        raise ValueError("Nenhum ministro disponível para a data/hora deste evento")

    vagas = evento.max_ministros or 6

    if ministro_ids_manuais is not None:
        ids_disponiveis = {m.id for m in candidatos}
        invalidos = [mid for mid in ministro_ids_manuais if mid not in ids_disponiveis]
        if invalidos:
            raise ValueError(f"Ministros indisponíveis ou inválidos: {invalidos}")
        selecionados = [m for m in candidatos if m.id in set(ministro_ids_manuais)]
    else:
        selecionados = _selecionar_ministros(candidatos, vagas)

    escala = Escala(
        evento_id=evento_id,
        status="PROPOSTA",
        observacao=f"Gerado por sorteio em {date.today()} — {len(selecionados)} ministros sorteados",
    )
    db.add(escala)
    db.flush()

    for m in selecionados:
        em = EscalaMinistro(escala_id=escala.id, ministro_id=m.id)
        db.add(em)
        m.escalas_mes = (m.escalas_mes or 0) + 1

    auditoria_service.registrar(db, "Escala", "CRIADO", None, "PROPOSTA")
    db.commit()
    db.refresh(escala)
    return _to_out(escala)


def aprovar(db: Session, escala_id: int) -> EscalaOut | None:
    escala = db.get(Escala, escala_id)
    if not escala:
        return None
    prev = escala.status
    escala.status = "APROVADA"
    if escala.evento:
        escala.evento.cancelado = True
    auditoria_service.registrar(db, "Escala", "APROVADO", prev, f"APROVADA — {_escala_detalhes(escala)}")
    db.commit()
    db.refresh(escala)
    return _to_out(escala)


def cancelar(db: Session, escala_id: int) -> EscalaOut | None:
    escala = db.get(Escala, escala_id)
    if not escala:
        return None
    prev = escala.status
    escala.status = "CANCELADA"
    auditoria_service.registrar(db, "Escala", "CANCELADO", prev, f"CANCELADA — {_escala_detalhes(escala)}")
    db.commit()
    db.refresh(escala)
    return _to_out(escala)


def deletar(db: Session, escala_id: int) -> None:
    escala = db.get(Escala, escala_id)
    if escala:
        auditoria_service.registrar(db, "Escala", "DELETADO", escala.status, f"DELETADA — {_escala_detalhes(escala)}")
        db.delete(escala)
        db.commit()
