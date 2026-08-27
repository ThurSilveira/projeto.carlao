import os
import unittest
from copy import deepcopy
from datetime import date, timedelta
from unittest.mock import patch

from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker

from app.database import Base
from app.models import Escala, EscalaMinistro, Evento, Ministro, SincronizacaoCalendario
from app.schemas import MinistroIn
from app.services import evento_service, google_calendar_service, ministro_service


class _Request:
    def __init__(self, result=None):
        self.result = result or {}

    def execute(self):
        return self.result


class _EventsResource:
    def __init__(self):
        self.inserts = []
        self.patches = []
        self.deletes = []
        self.events_by_id = {}

    def insert(self, **kwargs):
        self.inserts.append(kwargs)
        event = deepcopy(kwargs["body"])
        self.events_by_id[event["id"]] = event
        return _Request(event)

    def get(self, **kwargs):
        return _Request(deepcopy(self.events_by_id[kwargs["eventId"]]))

    def patch(self, **kwargs):
        self.patches.append(kwargs)
        event = self.events_by_id[kwargs["eventId"]]
        event.update(deepcopy(kwargs["body"]))
        return _Request(deepcopy(event))

    def delete(self, **kwargs):
        self.deletes.append(kwargs)
        self.events_by_id.pop(kwargs["eventId"], None)
        return _Request()


class _CalendarService:
    def __init__(self):
        self.resource = _EventsResource()

    def events(self):
        return self.resource


class GoogleCalendarServiceTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.engine = create_engine("sqlite:///:memory:")
        cls.Session = sessionmaker(bind=cls.engine)

    def setUp(self):
        Base.metadata.drop_all(bind=self.engine)
        Base.metadata.create_all(bind=self.engine)
        self.db = self.Session()
        minister = Ministro(
            nome="Ministro Notificado",
            email="ministro@example.com",
            ativo=True,
            funcao="LEITURA",
        )
        event = Evento(
            nome="Celebração",
            data=date.today() + timedelta(days=10),
            horario="19:00",
            tipo_evento="MISSA_PAROQUIAL",
            local="Igreja Matriz",
            cancelado=False,
        )
        self.db.add_all([minister, event])
        self.db.flush()
        self.scale = Escala(evento_id=event.id, status="APROVADA")
        self.db.add(self.scale)
        self.db.flush()
        self.db.add(EscalaMinistro(escala_id=self.scale.id, ministro_id=minister.id))
        self.db.commit()
        self.db.refresh(self.scale)

    def tearDown(self):
        self.db.close()

    def test_event_payload_has_private_attendees_and_three_email_reminders(self):
        environment = {
            "EVENT_TIMEZONE": "America/Sao_Paulo",
            "EVENT_DURATION_MINUTES": "120",
            "GOOGLE_CALENDAR_SAME_DAY_REMINDER_MINUTES": "60",
        }
        with patch.dict(os.environ, environment, clear=False):
            body = google_calendar_service.build_event_body(self.scale)

        self.assertEqual(body["attendees"], [{"email": "ministro@example.com"}])
        self.assertFalse(body["guestsCanSeeOtherGuests"])
        self.assertEqual(
            [reminder["minutes"] for reminder in body["reminders"]["overrides"]],
            [10080, 4320, 60],
        )

    def test_disabled_integration_does_not_create_remote_or_local_sync(self):
        with patch.dict(os.environ, {"GOOGLE_CALENDAR_ENABLED": "false"}, clear=False):
            result = google_calendar_service.sync_scale(self.db, self.scale.id)

        self.assertFalse(result.configurado)
        self.assertEqual(result.status, "DESATIVADO")
        self.assertEqual(self.db.query(SincronizacaoCalendario).count(), 0)

    def test_sync_is_idempotent_and_cancel_removes_remote_event(self):
        fake = _CalendarService()
        environment = {
            "GOOGLE_CALENDAR_ENABLED": "true",
            "GOOGLE_CALENDAR_ID": "primary",
            "GOOGLE_CALENDAR_EVENT_NAMESPACE": "tests",
            "EVENT_TIMEZONE": "America/Sao_Paulo",
            "EVENT_DURATION_MINUTES": "120",
            "GOOGLE_CALENDAR_SAME_DAY_REMINDER_MINUTES": "60",
        }
        with (
            patch.dict(os.environ, environment, clear=False),
            patch.object(google_calendar_service, "_calendar_service", return_value=fake),
        ):
            first = google_calendar_service.sync_scale(self.db, self.scale.id)
            second = google_calendar_service.sync_scale(self.db, self.scale.id)
            self.scale.status = "CANCELADA"
            self.db.commit()
            removed = google_calendar_service.sync_scale(self.db, self.scale.id)

        self.assertEqual(first.status, "SINCRONIZADO")
        self.assertEqual(second.status, "SINCRONIZADO")
        self.assertEqual(len(fake.resource.inserts), 1)
        self.assertEqual(len(fake.resource.patches), 1)
        self.assertEqual(fake.resource.inserts[0]["sendUpdates"], "all")
        self.assertEqual(len(fake.resource.deletes), 1)
        self.assertEqual(removed.status, "REMOVIDO")

    def test_resync_preserves_attendee_response_status(self):
        fake = _CalendarService()
        environment = {
            "GOOGLE_CALENDAR_ENABLED": "true",
            "GOOGLE_CALENDAR_ID": "primary",
            "GOOGLE_CALENDAR_EVENT_NAMESPACE": "tests",
            "EVENT_TIMEZONE": "America/Sao_Paulo",
            "EVENT_DURATION_MINUTES": "120",
            "GOOGLE_CALENDAR_SAME_DAY_REMINDER_MINUTES": "60",
        }
        with (
            patch.dict(os.environ, environment, clear=False),
            patch.object(google_calendar_service, "_calendar_service", return_value=fake),
        ):
            google_calendar_service.sync_scale(self.db, self.scale.id)
            event = next(iter(fake.resource.events_by_id.values()))
            event["attendees"][0]["responseStatus"] = "accepted"
            google_calendar_service.sync_scale(self.db, self.scale.id)

        attendee = fake.resource.patches[0]["body"]["attendees"][0]
        self.assertEqual(attendee["responseStatus"], "accepted")

    def test_scale_without_active_recipients_removes_stale_remote_event(self):
        fake = _CalendarService()
        environment = {
            "GOOGLE_CALENDAR_ENABLED": "true",
            "GOOGLE_CALENDAR_ID": "primary",
            "GOOGLE_CALENDAR_EVENT_NAMESPACE": "tests",
            "EVENT_TIMEZONE": "America/Sao_Paulo",
            "EVENT_DURATION_MINUTES": "120",
            "GOOGLE_CALENDAR_SAME_DAY_REMINDER_MINUTES": "60",
        }
        with (
            patch.dict(os.environ, environment, clear=False),
            patch.object(google_calendar_service, "_calendar_service", return_value=fake),
        ):
            google_calendar_service.sync_scale(self.db, self.scale.id)
            self.scale.escala_ministros[0].ministro.ativo = False
            self.db.commit()
            removed = google_calendar_service.sync_scale(self.db, self.scale.id)

        self.assertEqual(removed.status, "REMOVIDO")
        self.assertEqual(len(fake.resource.deletes), 1)

    def test_scale_without_recipients_and_without_remote_event_is_a_noop(self):
        self.scale.escala_ministros[0].ministro.ativo = False
        self.db.commit()

        with patch.dict(os.environ, {"GOOGLE_CALENDAR_ENABLED": "true"}, clear=False):
            result = google_calendar_service.sync_scale(self.db, self.scale.id)

        self.assertTrue(result.configurado)
        self.assertEqual(result.status, "SEM_DESTINATARIOS")
        self.assertEqual(self.db.query(SincronizacaoCalendario).count(), 0)

    def test_deleting_event_removes_remote_event_and_its_scale(self):
        fake = _CalendarService()
        event_id = self.scale.evento_id
        scale_id = self.scale.id
        environment = {
            "GOOGLE_CALENDAR_ENABLED": "true",
            "GOOGLE_CALENDAR_ID": "primary",
            "GOOGLE_CALENDAR_EVENT_NAMESPACE": "tests",
            "EVENT_TIMEZONE": "America/Sao_Paulo",
            "EVENT_DURATION_MINUTES": "120",
            "GOOGLE_CALENDAR_SAME_DAY_REMINDER_MINUTES": "60",
        }
        with (
            patch.dict(os.environ, environment, clear=False),
            patch.object(google_calendar_service, "_calendar_service", return_value=fake),
        ):
            google_calendar_service.sync_scale(self.db, scale_id)
            evento_service.deletar(self.db, event_id)

        self.assertEqual(len(fake.resource.deletes), 1)
        self.assertIsNone(self.db.get(Evento, event_id))
        self.assertIsNone(self.db.get(Escala, scale_id))

    def test_enabled_integration_without_credentials_records_error(self):
        environment = {
            "GOOGLE_CALENDAR_ENABLED": "true",
            "GOOGLE_CALENDAR_CLIENT_ID": "",
            "GOOGLE_CALENDAR_CLIENT_SECRET": "",
            "GOOGLE_CALENDAR_REFRESH_TOKEN": "",
        }
        with patch.dict(os.environ, environment, clear=False):
            with self.assertRaisesRegex(RuntimeError, "sincronizar"):
                google_calendar_service.sync_scale(self.db, self.scale.id)

        record = self.db.query(SincronizacaoCalendario).one()
        self.assertEqual(record.status, "ERRO")
        self.assertIn("GOOGLE_CALENDAR_REFRESH_TOKEN", record.erro)

    def test_updating_assigned_minister_resynchronizes_calendar(self):
        minister = self.scale.escala_ministros[0].ministro
        data = MinistroIn(
            nome=minister.nome,
            email="novo-email@example.com",
            ativo=True,
            funcao=minister.funcao,
        )

        with patch.object(google_calendar_service, "sync_scale") as sync_scale:
            ministro_service.atualizar(self.db, minister.id, data)

        sync_scale.assert_called_once_with(self.db, self.scale.id)


if __name__ == "__main__":
    unittest.main()
