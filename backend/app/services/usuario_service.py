from sqlalchemy.orm import Session

from app.auth_profiles import ADMINISTRADOR, MINISTRO, PROFILES, normalize_profile
from app.models import AcessoUsuario, Ministro, SessaoAutenticacao, Usuario, VinculoUsuarioMinistro
from app.schemas import PerfilOut, UsuarioAdminIn, UsuarioAdminOut, UsuarioAdminUpdate
from app.services import auditoria_service, auth_service


def _to_out(user: Usuario) -> UsuarioAdminOut:
    access = user.acesso
    return UsuarioAdminOut(
        id=user.id,
        nome=user.nome,
        email=user.email,
        perfil=access.perfil if access else "SEM_PERFIL",
        ativo=user.ativo,
        protegido=bool(access and access.protegido),
        ministro_id=user.vinculo_ministro.ministro_id if user.vinculo_ministro else None,
        criado_em=user.criado_em,
        atualizado_em=user.atualizado_em,
    )


def _get_user(db: Session, user_id: int) -> Usuario:
    user = db.get(Usuario, user_id)
    if not user:
        raise ValueError("Usuário não encontrado.")
    if not user.acesso:
        raise ValueError("Usuário sem perfil de acesso.")
    return user


def _active_admin_count(db: Session) -> int:
    return (
        db.query(Usuario)
        .join(AcessoUsuario)
        .filter(Usuario.ativo.is_(True), AcessoUsuario.perfil == ADMINISTRADOR)
        .count()
    )


def _ensure_email_available(db: Session, email: str, ignore_user_id: int | None = None) -> str:
    normalized = auth_service.validate_email(email)
    query = db.query(Usuario).filter(Usuario.email == normalized)
    if ignore_user_id is not None:
        query = query.filter(Usuario.id != ignore_user_id)
    if query.first():
        raise ValueError("Já existe um usuário com este e-mail.")
    return normalized


def _resolve_minister_link(
    db: Session,
    profile: str,
    email: str,
    minister_id: int | None,
    ignore_user_id: int | None = None,
) -> Ministro | None:
    if profile != MINISTRO:
        if minister_id is not None:
            raise ValueError("O vínculo com ministro só pode ser usado no perfil MINISTRO.")
        return None
    if minister_id is None:
        raise ValueError("Informe ministroId para um usuário MINISTRO.")

    minister = db.get(Ministro, minister_id)
    if not minister:
        raise ValueError("Ministro não encontrado.")
    if not minister.ativo:
        raise ValueError("O ministro precisa estar ativo para receber acesso.")
    if auth_service.normalize_email(minister.email) != email:
        raise ValueError("O e-mail do usuário deve ser igual ao e-mail do ministro.")

    query = db.query(VinculoUsuarioMinistro).filter(VinculoUsuarioMinistro.ministro_id == minister.id)
    if ignore_user_id is not None:
        query = query.filter(VinculoUsuarioMinistro.usuario_id != ignore_user_id)
    if query.first():
        raise ValueError("Este ministro já possui um usuário vinculado.")
    return minister


def listar_perfis() -> list[PerfilOut]:
    return [
        PerfilOut(
            nome=definition.nome,
            descricao=definition.descricao,
            permissoes=sorted(definition.permissoes),
        )
        for definition in PROFILES.values()
    ]


def listar(db: Session) -> list[UsuarioAdminOut]:
    return [_to_out(user) for user in db.query(Usuario).order_by(Usuario.id.asc()).all()]


def obter(db: Session, user_id: int) -> UsuarioAdminOut:
    return _to_out(_get_user(db, user_id))


def criar(db: Session, data: UsuarioAdminIn, actor: Usuario) -> UsuarioAdminOut:
    profile = normalize_profile(data.perfil)
    email = _ensure_email_available(db, data.email)
    minister = _resolve_minister_link(db, profile, email, data.ministro_id)
    name = data.nome.strip()
    if len(name) < 2:
        raise ValueError("O nome deve conter pelo menos 2 caracteres.")

    user = Usuario(nome=name, email=email, senha_hash=auth_service.hash_password(data.senha), ativo=data.ativo)
    db.add(user)
    db.flush()
    user.acesso = AcessoUsuario(perfil=profile, protegido=False)
    if minister:
        user.vinculo_ministro = VinculoUsuarioMinistro(ministro_id=minister.id)
    auditoria_service.registrar(db, "Usuário", "CRIADO", None, f"{user.email} — {profile}", str(actor.id))
    db.commit()
    db.refresh(user)
    return _to_out(user)


def atualizar(db: Session, user_id: int, data: UsuarioAdminUpdate, actor: Usuario) -> UsuarioAdminOut:
    user = _get_user(db, user_id)
    access = user.acesso
    profile = normalize_profile(data.perfil)
    if access.protegido:
        raise ValueError("O administrador principal é protegido e não pode ser alterado por esta rota.")
    if user.id == actor.id and (not data.ativo or profile != access.perfil):
        raise ValueError("Você não pode desativar ou alterar o perfil da própria conta.")
    if access.perfil == ADMINISTRADOR and (not data.ativo or profile != ADMINISTRADOR):
        if _active_admin_count(db) <= 1:
            raise ValueError("O sistema deve manter pelo menos um administrador ativo.")

    name = data.nome.strip()
    if len(name) < 2:
        raise ValueError("O nome deve conter pelo menos 2 caracteres.")
    email = _ensure_email_available(db, data.email, user.id)
    current_minister_id = user.vinculo_ministro.ministro_id if user.vinculo_ministro else None
    requested_minister_id = (
        data.ministro_id if data.ministro_id is not None else current_minister_id
    ) if profile == MINISTRO else data.ministro_id
    minister = _resolve_minister_link(db, profile, email, requested_minister_id, user.id)
    previous = f"{user.email} — {access.perfil} — {'ATIVO' if user.ativo else 'INATIVO'}"
    user.nome = name
    user.email = email
    user.ativo = data.ativo
    access.perfil = profile
    if minister:
        if user.vinculo_ministro:
            user.vinculo_ministro.ministro_id = minister.id
        else:
            user.vinculo_ministro = VinculoUsuarioMinistro(ministro_id=minister.id)
    elif user.vinculo_ministro:
        db.delete(user.vinculo_ministro)
    current = f"{user.email} — {profile} — {'ATIVO' if user.ativo else 'INATIVO'}"
    auditoria_service.registrar(db, "Usuário", "ATUALIZADO", previous, current, str(actor.id))
    db.commit()
    db.refresh(user)
    return _to_out(user)


def redefinir_senha(db: Session, user_id: int, new_password: str, actor: Usuario) -> None:
    user = _get_user(db, user_id)
    if user.acesso.protegido:
        raise ValueError("A senha do administrador principal só pode ser alterada pela própria conta.")
    if user.id == actor.id:
        raise ValueError("Use a opção de alteração de senha da própria conta.")
    user.senha_hash = auth_service.hash_password(new_password)
    db.query(SessaoAutenticacao).filter(SessaoAutenticacao.usuario_id == user.id).delete(synchronize_session=False)
    auditoria_service.registrar(db, "Usuário", "SENHA_REDEFINIDA", None, user.email, str(actor.id))
    db.commit()


def deletar(db: Session, user_id: int, actor: Usuario) -> None:
    user = _get_user(db, user_id)
    if user.acesso.protegido:
        raise ValueError("O administrador principal é protegido e não pode ser excluído.")
    if user.id == actor.id:
        raise ValueError("Você não pode excluir a própria conta.")
    if user.ativo and user.acesso.perfil == ADMINISTRADOR and _active_admin_count(db) <= 1:
        raise ValueError("O sistema deve manter pelo menos um administrador ativo.")
    description = f"{user.email} — {user.acesso.perfil}"
    auditoria_service.registrar(db, "Usuário", "DELETADO", description, None, str(actor.id))
    db.delete(user)
    db.commit()
