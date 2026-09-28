"""Cria um cenário determinístico de demonstração em um PostgreSQL local.

O script recusa conexões que não apontem explicitamente para loopback. Ele não
carrega ``backend/.env`` de propósito, para evitar que uma configuração de
deploy seja usada por engano.
"""

from __future__ import annotations

import os
import random
from calendar import monthrange
from datetime import date

from sqlalchemy import create_engine, func, select
from sqlalchemy.engine import make_url
from sqlalchemy.orm import Session

from app.models import Base, Evento, Indisponibilidade, Ministro


SCENARIO_TAG = "SEED_LOCAL_50_MINISTROS_30_EVENTOS"
LOCAL_HOSTS = {"127.0.0.1", "localhost", "::1"}

FIRST_NAMES = [
    "Ana",
    "André",
    "Beatriz",
    "Bruno",
    "Camila",
    "Carlos",
    "Daniel",
    "Diego",
    "Eduarda",
    "Felipe",
    "Fernanda",
    "Gabriel",
    "Helena",
    "Isabela",
    "João",
    "Juliana",
    "Larissa",
    "Lucas",
    "Luciana",
    "Marcos",
    "Maria",
    "Mateus",
    "Patrícia",
    "Paulo",
    "Rafael",
]

LAST_NAMES = [
    "Almeida",
    "Alves",
    "Barbosa",
    "Carvalho",
    "Costa",
    "Ferreira",
    "Gomes",
    "Lima",
    "Martins",
    "Mendes",
    "Oliveira",
    "Pereira",
    "Ramos",
    "Rocha",
    "Santos",
    "Silva",
    "Souza",
    "Teixeira",
    "Vieira",
]

FUNCTIONS = [
    "EUCARISTIA",
    "LEITURA",
    "ACOLHIMENTO",
    "MUSICA",
    "CATEQUESE",
    "ADORACAO",
]

EVENT_TEMPLATES = [
    ("Missa Dominical", "MISSA_PAROQUIAL", "Igreja Matriz"),
    ("Celebração da Comunidade", "MISSA_ESPECIAL", "Capela São José"),
    ("Batizado Comunitário", "BATIZADO", "Igreja Matriz"),
    ("Adoração ao Santíssimo", "ADORACAO", "Capela do Santíssimo"),
    ("Encontro de Formação", "OUTRO", "Salão Paroquial"),
    ("Retiro de Ministros", "RETIRO", "Casa de Retiros São José"),
]

TIME_WINDOWS = [
    (None, None, "Compromisso durante todo o dia"),
    ("07:00", "12:00", "Compromisso familiar pela manhã"),
    ("12:00", "18:00", "Trabalho no período da tarde"),
    ("18:00", "22:30", "Viagem no período da noite"),
]

EVENT_TIMES = ["07:00", "09:00", "11:00", "15:00", "18:00", "19:30"]


def require_local_database(raw_url: str):
    if not raw_url.strip():
        raise RuntimeError("Defina DATABASE_URL explicitamente para o banco local.")

    url = make_url(raw_url.strip())
    if url.host not in LOCAL_HOSTS:
        raise RuntimeError(
            f"Seed recusado: o host {url.host!r} não é um endereço local permitido."
        )
    return url


def next_months(reference: date, quantity: int) -> list[tuple[int, int]]:
    months: list[tuple[int, int]] = []
    year = reference.year
    month = reference.month
    for _ in range(quantity):
        month += 1
        if month == 13:
            month = 1
            year += 1
        months.append((year, month))
    return months


def weekend_dates(year: int, month: int) -> list[date]:
    return [
        date(year, month, day)
        for day in range(1, monthrange(year, month)[1] + 1)
        if date(year, month, day).weekday() in {5, 6}
    ]


def build_events(reference: date) -> list[Evento]:
    events: list[Evento] = []
    for month_index, (year, month) in enumerate(next_months(reference, 3)):
        available_dates = weekend_dates(year, month)
        for event_index in range(10):
            template_index = (month_index * 10 + event_index) % len(EVENT_TEMPLATES)
            name, event_type, location = EVENT_TEMPLATES[template_index]
            event_date = available_dates[event_index % len(available_dates)]
            events.append(
                Evento(
                    nome=f"[LOCAL] {name} {event_index + 1:02d}",
                    data=event_date,
                    horario=EVENT_TIMES[(event_index + month_index) % len(EVENT_TIMES)],
                    tipo_evento=event_type,
                    max_ministros=4 + ((event_index + month_index) % 7),
                    local=location,
                )
            )
    return events


def build_ministers(rnd: random.Random) -> list[Ministro]:
    ministers: list[Ministro] = []
    for index in range(50):
        first_name = FIRST_NAMES[index % len(FIRST_NAMES)]
        last_name = LAST_NAMES[(index * 7) % len(LAST_NAMES)]
        second_last_name = LAST_NAMES[(index * 11 + 3) % len(LAST_NAMES)]
        ministers.append(
            Ministro(
                nome=f"{first_name} {last_name} {second_last_name}",
                email=f"ministro.local.{index + 1:02d}@example.test",
                telefone=f"(11) 9{rnd.randint(1000, 9999)}-{rnd.randint(1000, 9999)}",
                data_nascimento=date(
                    rnd.randint(1962, 2002), rnd.randint(1, 12), rnd.randint(1, 28)
                ),
                observacoes=f"{SCENARIO_TAG}: registro sintético local",
                ativo=True,
                visitas_aos_enfermos=index % 3 == 0,
                status_curso=index % 4 != 0,
                escalas_mes=index % 5,
                funcao=FUNCTIONS[index % len(FUNCTIONS)],
            )
        )
    return ministers


def add_unavailability(
    db: Session,
    ministers: list[Ministro],
    event_dates: list[date],
) -> int:
    total = 0
    unique_dates = sorted(set(event_dates))
    for minister_index, minister in enumerate(ministers):
        unavailable_count = 1 + (minister_index % 4)
        for unavailable_index in range(unavailable_count):
            start, end, reason = TIME_WINDOWS[
                (minister_index + unavailable_index) % len(TIME_WINDOWS)
            ]
            unavailable_date = unique_dates[
                (minister_index * 3 + unavailable_index * 7) % len(unique_dates)
            ]
            db.add(
                Indisponibilidade(
                    ministro_id=minister.id,
                    data=unavailable_date,
                    horario_inicio=start,
                    horario_fim=end,
                    motivo=f"[LOCAL] {reason}",
                )
            )
            total += 1
    return total


def seed() -> dict[str, object]:
    url = require_local_database(os.getenv("DATABASE_URL", ""))
    engine = create_engine(url, pool_pre_ping=True)
    Base.metadata.create_all(bind=engine)

    with Session(engine) as db, db.begin():
        existing_ministers = db.scalar(
            select(func.count(Ministro.id)).where(
                Ministro.observacoes.like(f"{SCENARIO_TAG}:%")
            )
        )
        existing_events = db.scalar(
            select(func.count(Evento.id)).where(Evento.nome.like("[LOCAL] %"))
        )
        if existing_ministers or existing_events:
            raise RuntimeError(
                "O cenário local já existe neste banco; nenhuma duplicação foi feita."
            )

        events = build_events(date.today())
        ministers = build_ministers(random.Random(20260829))
        db.add_all(events)
        db.add_all(ministers)
        db.flush()
        unavailability_count = add_unavailability(
            db, ministers, [event.data for event in events]
        )

        event_months = sorted({event.data.strftime("%Y-%m") for event in events})
        result = {
            "database": url.database,
            "ministros": len(ministers),
            "indisponibilidades": unavailability_count,
            "eventos": len(events),
            "meses": event_months,
        }

    engine.dispose()
    return result


if __name__ == "__main__":
    summary = seed()
    print("Cenário local criado com sucesso:")
    for key, value in summary.items():
        print(f"- {key}: {value}")
