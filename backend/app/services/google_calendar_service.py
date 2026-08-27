import hashlib
import os
from datetime import datetime, timedelta
from zoneinfo import ZoneInfo, ZoneInfoNotFoundError

from google.oauth2.credentials import Credentials
from googleapiclient.discovery import build
from googleapiclient.errors import HttpError
from sqlalchemy.orm import Session

from app.models import Escala, SincronizacaoCalendario, utcnow_naive
from app.schemas import SincronizacaoCalendarioOut
from app.services import auth_service


ACTIVE_SCALE_STATUSES = {"APROVADA", "CONFIRMADA"}
CALENDAR_SCOPE = "https://www.googleapis.com/auth/calendar.events"


def enabled() -> bool:
    return os.getenv("GOOGLE_CALENDAR_ENABLED", "false").strip().lower() == "true"


def _setting(name: str) -> str:
    value = os.getenv(name, "").strip()
    if not value:
        raise RuntimeError(f"{name} é obrigatório quando o Google Calendar está ativo.")
    return value


def _calendar_service():
    credentials = Credentials(
        token=None,
        refresh_token=_setting("GOOGLE_CALENDAR_REFRESH_TOKEN"),
        token_uri="https://oauth2.googleapis.com/token",
        client_id=_setting("GOOGLE_CALENDAR_CLIENT_ID"),
        client_secret=_setting("GOOGLE_CALENDAR_CLIENT_SECRET"),
        scopes=[CALENDAR_SCOPE],
    )
    return build("calendar", "v3", credentials=credentials, cache_discovery=False)


def _timezone() -> ZoneInfo:
    try:
        return ZoneInfo(os.getenv("EVENT_TIMEZONE", "America/Sao_Paulo"))
    except ZoneInfoNotFoundError as exc:
        raise ValueError("O fuso horário configurado para os eventos é inválido.") from exc


def _duration_minutes() -> int:
    try:
        duration = int(os.getenv("EVENT_DURATION_MINUTES", "120"))
    except ValueError as exc:
        raise ValueError("EVENT_DURATION_MINUTES deve ser um número inteiro.") from exc
    if not 1 <= duration <= 1440:
        raise ValueError("EVENT_DURATION_MINUTES deve estar entre 1 e 1440.")
    return duration


def _same_day_reminder_minutes() -> int:
    try:
        minutes = int(os.getenv("GOOGLE_CALENDAR_SAME_DAY_REMINDER_MINUTES", "60"))
    except ValueError as exc:
        raise ValueError("GOOGLE_CALENDAR_SAME_DAY_REMINDER_MINUTES deve ser inteiro.") from exc
    if not 0 <= minutes <= 1440:
        raise ValueError("O lembrete do dia deve estar entre 0 e 1440 minutos antes do evento.")
    return minutes


def _event_id(scale_id: int) -> str:
    namespace = os.getenv(
        "GOOGLE_CALENDAR_EVENT_NAMESPACE", "escala-ministerial"
    ).strip() or "escala-ministerial"
    return hashlib.sha256(f"{namespace}:{scale_id}".encode("utf-8")).hexdigest()[:40]


def _recipient_emails(scale: Escala) -> list[str]:
    emails: list[str] = []
    for assignment in scale.escala_ministros:
        minister = assignment.ministro
        if assignment.substituido or not minister or not minister.ativo:
            continue
        try:
            email = auth_service.validate_email(minister.email)
        except ValueError:
            continue
        if email not in emails:
            emails.append(email)
    return emails


def build_event_body(scale: Escala, *, include_id: bool = False) -> dict:
    event = scale.evento
    if not event:
        raise ValueError("A escala não possui evento vinculado.")
    try:
        timezone = _timezone()
        event_time = datetime.strptime(event.horario, "%H:%M").time()
    except ValueError as exc:
        raise ValueError("O evento possui horário inválido.") from exc

    start = datetime.combine(event.data, event_time, timezone)
    end = start + timedelta(minutes=_duration_minutes())
    recipients = _recipient_emails(scale)
    if not recipients:
        raise ValueError("A escala não possui ministros ativos com e-mail válido.")

    reminder_minutes = [10080, 4320, _same_day_reminder_minutes()]
    body = {
        "summary": f"Escala ministerial — {event.nome}",
        "description": (
            "Você foi escalado(a) para este evento. "
            "Em caso de impedimento, registre sua indisponibilidade no sistema."
        ),
        "location": event.local or "",
        "start": {"dateTime": start.isoformat(), "timeZone": str(timezone)},
        "end": {"dateTime": end.isoformat(), "timeZone": str(timezone)},
        "attendees": [{"email": email} for email in recipients],
        "guestsCanInviteOthers": False,
        "guestsCanModify": False,
        "guestsCanSeeOtherGuests": False,
        "reminders": {
            "useDefault": False,
            "overrides": [
                {"method": "email", "minutes": minutes}
                for minutes in reminder_minutes
            ],
        },
        "extendedProperties": {
            "private": {
                "escalaId": str(scale.id),
                "origem": "escala-ministerial",
            }
        },
    }
    if include_id:
        body["id"] = _event_id(scale.id)
    return body


def _record(db: Session, scale: Escala) -> SincronizacaoCalendario:
    record = scale.sincronizacao_calendario
    if record:
        return record
    record = SincronizacaoCalendario(escala_id=scale.id, status="PENDENTE")
    db.add(record)
    db.flush()
    return record


def _result(
    scale_id: int,
    status: str,
    recipients: int,
    *,
    configured: bool,
) -> SincronizacaoCalendarioOut:
    return SincronizacaoCalendarioOut(
        escala_id=scale_id,
        configurado=configured,
        status=status,
        destinatarios=recipients,
    )


def _save_error(db: Session, record: SincronizacaoCalendario, exc: Exception) -> None:
    record.status = "ERRO"
    record.erro = str(exc)[:2000]
    record.ultima_tentativa_em = utcnow_naive()
    db.commit()


def _update_event(events, calendar_id: str, event_id: str, body: dict) -> dict:
    current = events.get(calendarId=calendar_id, eventId=event_id).execute()
    response_by_email = {
        attendee.get("email", "").strip().lower(): attendee.get("responseStatus")
        for attendee in current.get("attendees", [])
        if attendee.get("email") and attendee.get("responseStatus")
    }
    attendees = []
    for attendee in body.get("attendees", []):
        merged = dict(attendee)
        response_status = response_by_email.get(attendee["email"].strip().lower())
        if response_status:
            merged["responseStatus"] = response_status
        attendees.append(merged)

    patch_body = {**body, "attendees": attendees}
    return events.patch(
        calendarId=calendar_id,
        eventId=event_id,
        body=patch_body,
        sendUpdates="all",
    ).execute()


def remove_scale_event(db: Session, scale: Escala) -> SincronizacaoCalendarioOut:
    record = scale.sincronizacao_calendario
    recipients = len(_recipient_emails(scale))
    if not record or not record.google_event_id:
        return _result(scale.id, "SEM_EVENTO", recipients, configured=enabled())
    if not enabled():
        record.status = "PENDENTE_REMOCAO"
        record.ultima_tentativa_em = utcnow_naive()
        db.commit()
        return _result(scale.id, record.status, recipients, configured=False)

    try:
        _calendar_service().events().delete(
            calendarId=os.getenv("GOOGLE_CALENDAR_ID", "primary"),
            eventId=record.google_event_id,
            sendUpdates="all",
        ).execute()
    except HttpError as exc:
        if getattr(exc.resp, "status", None) not in {404, 410}:
            _save_error(db, record, exc)
            raise RuntimeError("Não foi possível remover o evento do Google Calendar.") from exc
    except Exception as exc:
        _save_error(db, record, exc)
        raise RuntimeError("Não foi possível remover o evento do Google Calendar.") from exc

    record.google_event_id = None
    record.status = "REMOVIDO"
    record.erro = None
    record.destinatarios = 0
    record.ultima_tentativa_em = utcnow_naive()
    db.commit()
    return _result(scale.id, record.status, recipients, configured=True)


def sync_scale(db: Session, scale_id: int) -> SincronizacaoCalendarioOut:
    scale = db.get(Escala, scale_id)
    if not scale:
        raise ValueError("Escala não encontrada.")

    recipients = _recipient_emails(scale)
    if scale.status not in ACTIVE_SCALE_STATUSES or scale.evento.cancelado:
        return remove_scale_event(db, scale)
    if not recipients:
        result = remove_scale_event(db, scale)
        if result.status == "SEM_EVENTO":
            return _result(scale.id, "SEM_DESTINATARIOS", 0, configured=enabled())
        return result
    if not enabled():
        return _result(scale.id, "DESATIVADO", len(recipients), configured=False)

    body = build_event_body(scale)
    record = _record(db, scale)
    record.status = "SINCRONIZANDO"
    record.destinatarios = len(recipients)
    record.ultima_tentativa_em = utcnow_naive()
    db.commit()

    calendar_id = os.getenv("GOOGLE_CALENDAR_ID", "primary")
    try:
        service_events = _calendar_service().events()
        if record.google_event_id:
            result = _update_event(
                service_events,
                calendar_id,
                record.google_event_id,
                body,
            )
        else:
            create_body = {**body, "id": _event_id(scale.id)}
            try:
                result = service_events.insert(
                    calendarId=calendar_id,
                    body=create_body,
                    sendUpdates="all",
                ).execute()
            except HttpError as exc:
                if getattr(exc.resp, "status", None) != 409:
                    raise
                result = _update_event(
                    service_events,
                    calendar_id,
                    create_body["id"],
                    body,
                )
    except Exception as exc:
        _save_error(db, record, exc)
        raise RuntimeError("Não foi possível sincronizar a escala com o Google Calendar.") from exc

    record.google_event_id = result.get("id") or _event_id(scale.id)
    record.status = "SINCRONIZADO"
    record.erro = None
    record.destinatarios = len(recipients)
    record.ultima_tentativa_em = utcnow_naive()
    db.commit()
    return _result(scale.id, record.status, len(recipients), configured=True)
