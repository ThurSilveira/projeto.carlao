import random
from datetime import date, timedelta
from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session
from app.database import get_db
from app.models import Ministro, Evento

router = APIRouter()

_NOMES = ["João", "Maria", "Pedro", "Ana", "Carlos", "Fernanda", "Roberto", "Luciana",
          "Marcos", "Silvia", "Thiago", "Patricia", "Rafael", "Beatriz", "Felipe",
          "Camila", "Bruno", "Juliana", "Diego", "Larissa", "André", "Vanessa"]
_SOBRENOMES = ["Silva", "Santos", "Oliveira", "Souza", "Lima", "Ferreira", "Costa",
               "Alves", "Pereira", "Carvalho", "Mendes", "Ramos", "Gomes", "Vieira"]
_FUNCOES = ["EUCARISTIA", "LEITURA", "ACOLHIMENTO", "MUSICA", "CATEQUESE", "ADORACAO", "OUTRO"]
_TIPOS = ["MISSA_PAROQUIAL", "MISSA_ESPECIAL", "RETIRO", "BATIZADO", "CASAMENTO", "ADORACAO", "OUTRO"]
_LOCAIS = ["Igreja Matriz", "Salão Paroquial", "Praça Central", "Casa de Retiros São José"]
_EVENTOS_NOMES = ["Missa Dominical", "Missa Solene", "Retiro de Advento", "Batizado Comunitário",
                  "Adoração Noturna", "Encontro de Ministros", "Celebração Especial"]


@router.post("/seed")
def seed(quantidade: int = 10, db: Session = Depends(get_db)):
    rnd = random.Random()
    for _ in range(quantidade):
        nome = f"{rnd.choice(_NOMES)} {rnd.choice(_SOBRENOMES)} {rnd.choice(_SOBRENOMES)}"
        email = f"{nome.lower().replace(' ', '.')[:20]}.{rnd.randint(1000, 99999)}@paroquia.com"
        m = Ministro(
            nome=nome,
            email=email,
            telefone=f"({rnd.randint(11,99)}) 9{rnd.randint(1000,9999)}-{rnd.randint(1000,9999)}",
            data_nascimento=date(rnd.randint(1960, 2000), rnd.randint(1, 12), rnd.randint(1, 28)),
            ativo=rnd.random() > 0.15,
            visitas_ao_infermo=rnd.choice([True, False]),
            status_curso=rnd.choice([True, False]),
            escalas_mes=rnd.randint(0, 4),
            funcao=rnd.choice(_FUNCOES),
        )
        db.add(m)

    for _ in range(quantidade):
        e = Evento(
            nome=rnd.choice(_EVENTOS_NOMES),
            data=date.today() + timedelta(days=rnd.randint(1, 180)),
            horario=f"{rnd.choice([7,9,11,15,18,19])}:{'00' if rnd.random() > 0.5 else '30'}",
            tipo_evento=rnd.choice(_TIPOS),
            max_ministros=rnd.randint(2, 10),
            local=rnd.choice(_LOCAIS),
        )
        db.add(e)

    db.commit()
    total_m = db.query(Ministro).count()
    total_e = db.query(Evento).count()
    return {"ministros": total_m, "eventos": total_e, "mensagem": f"+{quantidade} ministros e +{quantidade} eventos adicionados!"}
