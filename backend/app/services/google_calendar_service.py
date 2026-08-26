import os
from datetime import UTC, date, datetime, time, timedelta
from zoneinfo import ZoneInfo, ZoneInfoNotFoundError

from sqlalchemy.orm import Session

from app.models import Escala, EscalaMinistro, SincronizacaoCalendario, utcnow_naive
from app.schemas import SincronizacaoCalendarioOut


ACTIVE_SCALE_STATUSES = {"APROVADA", "CONFIRMADA"}
REMINDER_MINUTES = [7 * 24 * 60, 3 * 24 * 60, 60]


def configured() -> bool:
    required = (
        "GOOGLE_CALENDAR_CLIENT_ID",
        "GOOGLE_CALENDAR_CLIENT_SECRET",
        "GOOGLE_CALENDAR_REFRESH_TOKEN",
        "GOOGLE_CALENDAR_ID",
    )
    return (
        os.getenv("GOOGLE_CALENDAR_ENABLED", "false").strip().lower() == "true"
        and all(os.getenv(key, "").strip() for key in required)
    )


def _timezone() -> ZoneInfo:
    timezone_name = os.getenv("GOOGLE_CALENDAR_TIMEZONE", "America/Sao_Paulo").strip()
    try:
        return ZoneInfo(timezone_name)
    except ZoneInfoNotFoundError as exc:
        raise RuntimeError("GOOGLE_CALENDAR_TIMEZONE é inválido.") from exc


def _event_duration() -> timedelta:
    raw_minutes = os.getenv("GOOGLE_CALENDAR_EVENT_DURATION_MINUTES", "120")
    try:
        minutes = int(raw_minutes)
    except ValueError as exc:
        raise RuntimeError("GOOGLE_CALENDAR_EVENT_DURATION_MINUTES deve ser inteiro.") from exc
    if not 15 <= minutes <= 1440:
        raise RuntimeError("GOOGLE_CALENDAR_EVENT_DURATION_MINUTES deve ficar entre 15 e 1440.")
    return timedelta(minutes=minutes)


def _google_service():
    from google.oauth2.credentials import Credentials
    from googleapiclient.discovery import build

    credentials = Credentials(
        token=None,
        refresh_token=os.environ["GOOGLE_CALENDAR_REFRESH_TOKEN"],
        token_uri="https://oauth2.googleapis.com/token",
        client_id=os.environ["GOOGLE_CALENDAR_CLIENT_ID"],
        client_secret=os.environ["GOOGLE_CALENDAR_CLIENT_SECRET"],
        scopes=["https://www.googleapis.com/auth/calendar.events"],
    )
    return build("calendar", "v3", credentials=credentials, cache_discovery=False)


def build_event_body(assignment: EscalaMinistro) -> dict:
    scale = assignment.escala
    event = scale.evento
    minister = assignment.ministro
    try:
        event_time = time.fromisoformat(event.horario)
    except (TypeError, ValueError) as exc:
        raise RuntimeError("O evento precisa ter um horário válido para sincronizar com o Google Calendar.") from exc

    timezone = _timezone()
    start = datetime.combine(event.data, event_time, timezone)
    end = start + _event_duration()
    function = minister.funcao_especificada if minister.funcao == "OUTRO" else minister.funcao
    description = (
        "Escala Ministerial\n"
        f"Ministro: {minister.nome}\n"
        f"Função: {function or 'não informada'}\n"
        "Consulte o portal para visualizar os detalhes atualizados da escala."
    )
    return {
        "summary": f"Escala Ministerial — {event.nome}",
        "description": description,
        "location": event.local or "",
        "start": {"dateTime": start.isoformat(), "timeZone": str(timezone)},
        "end": {"dateTime": end.isoformat(), "timeZone": str(timezone)},
        "attendees": [{"email": minister.email, "displayName": minister.nome}],
        "reminders": {
            "useDefault": False,
            "overrides": [
                {"method": "email", "minutes": reminder_minutes}
                for reminder_minutes in REMINDER_MINUTES
            ],
        },
        "extendedProperties": {
            "shared": {
                "escalaMinisterialId": str(scale.id),
                "escalaMinisterialAssignmentId": str(assignment.id),
            }
        },
    }


def _tracking(db: Session, assignment: EscalaMinistro) -> SincronizacaoCalendario:
    tracking = (
        db.query(SincronizacaoCalendario)
        .filter(SincronizacaoCalendario.escala_ministro_id == assignment.id)
        .first()
    )
    if tracking:
        return tracking
    tracking = SincronizacaoCalendario(escala_ministro_id=assignment.id)
    db.add(tracking)
    db.flush()
    return tracking


def _mark_error(db: Session, tracking: SincronizacaoCalendario, error: Exception | str) -> None:
    tracking.status = "FALHA"
    tracking.erro = str(error)[:2000]
    tracking.ultima_tentativa_em = utcnow_naive()
    db.commit()


def _remove_google_event(service, tracking: SincronizacaoCalendario) -> bool:
    if not tracking.google_event_id:
        return False
    from googleapiclient.errors import HttpError

    try:
        service.events().delete(
            calendarId=os.environ["GOOGLE_CALENDAR_ID"],
            eventId=tracking.google_event_id,
            sendUpdates="all",
        ).execute()
    except HttpError as exc:
        if getattr(exc.resp, "status", None) not in {404, 410}:
            raise
    return True


def sync_scale(db: Session, scale_id: int) -> SincronizacaoCalendarioOut:
    scale = db.get(Escala, scale_id)
    if not scale:
        raise ValueError("Escala não encontrada.")

    result = SincronizacaoCalendarioOut(escala_id=scale.id, configurado=configured())
    assignments = list(scale.escala_ministros)
    if not configured():
        should_publish = (
            scale.status in ACTIVE_SCALE_STATUSES
            and scale.evento is not None
            and not scale.evento.cancelado
            and scale.evento.data >= date.today()
        )
        for assignment in assignments:
            tracking = _tracking(db, assignment)
            if should_publish and not assignment.substituido:
                tracking.status = "AGUARDANDO_CONFIGURACAO"
                tracking.erro = "Google Calendar ainda não configurado."
                result.pendentes += 1
            elif tracking.google_event_id:
                tracking.status = "REMOCAO_PENDENTE"
                tracking.erro = "Google Calendar não configurado para remover o evento existente."
                result.pendentes += 1
            else:
                tracking.status = "IGNORADO"
                tracking.erro = None
            tracking.ultima_tentativa_em = utcnow_naive()
        db.commit()
        return result

    service = _google_service()
    should_publish = (
        scale.status in ACTIVE_SCALE_STATUSES
        and scale.evento is not None
        and not scale.evento.cancelado
        and scale.evento.data >= date.today()
    )

    for assignment in assignments:
        tracking = _tracking(db, assignment)
        active_assignment = should_publish and not assignment.substituido
        try:
            if not active_assignment:
                removed = _remove_google_event(service, tracking)
                tracking.google_event_id = None
                tracking.status = "REMOVIDO" if removed else "IGNORADO"
                tracking.erro = None
                tracking.ultima_tentativa_em = utcnow_naive()
                result.removidos += int(removed)
                db.commit()
                continue

            body = build_event_body(assignment)
            if tracking.google_event_id:
                response = service.events().update(
                    calendarId=os.environ["GOOGLE_CALENDAR_ID"],
                    eventId=tracking.google_event_id,
                    body=body,
                    sendUpdates="all",
                ).execute()
            else:
                response = service.events().insert(
                    calendarId=os.environ["GOOGLE_CALENDAR_ID"],
                    body=body,
                    sendUpdates="all",
                ).execute()
            tracking.google_event_id = response["id"]
            tracking.status = "SINCRONIZADO"
            tracking.erro = None
            tracking.ultima_tentativa_em = utcnow_naive()
            result.sincronizados += 1
            db.commit()
        except Exception as exc:
            db.rollback()
            tracking = _tracking(db, assignment)
            _mark_error(db, tracking, exc)
            result.falhas += 1
    return result


def cancel_scale_events(db: Session, scale_id: int) -> SincronizacaoCalendarioOut:
    scale = db.get(Escala, scale_id)
    if not scale:
        raise ValueError("Escala não encontrada.")
    previous_status = scale.status
    scale.status = "CANCELADA"
    db.flush()
    result = sync_scale(db, scale_id)
    scale.status = previous_status
    db.commit()
    return result
