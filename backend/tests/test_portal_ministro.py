import os
import unittest
from datetime import date, timedelta
from unittest.mock import patch

from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker

from app.auth_profiles import MINISTRO
from app.database import Base
from app.models import Escala, EscalaMinistro, Evento, Indisponibilidade, Ministro, SincronizacaoCalendario
from app.schemas import FeedbackMinistroIn, IndisponibilidadeIn, UsuarioAdminIn
from app.services import auth_service, escala_service, google_calendar_service, portal_ministro_service, usuario_service


class PortalMinistroTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.engine = create_engine("sqlite:///:memory:")
        cls.Session = sessionmaker(bind=cls.engine)

    def setUp(self):
        Base.metadata.drop_all(bind=self.engine)
        Base.metadata.create_all(bind=self.engine)
        self.db = self.Session()
        environment = {
            "AUTH_ADMIN_NAME": "Administrador Principal",
            "AUTH_ADMIN_EMAIL": "admin@example.com",
            "AUTH_ADMIN_PASSWORD": "senha-principal-segura",
            "GOOGLE_CALENDAR_ENABLED": "false",
        }
        with patch.dict(os.environ, environment, clear=False):
            auth_service.bootstrap_admin(self.db)
            auth_service.ensure_access_profiles(self.db)
        self.admin = auth_service.authenticate(
            self.db,
            "admin@example.com",
            "senha-principal-segura",
            "127.0.0.1",
        )
        self.minister = Ministro(
            nome="Ministro Portal",
            email="ministro@example.com",
            ativo=True,
            funcao="LEITURA",
        )
        self.db.add(self.minister)
        self.db.commit()
        self.db.refresh(self.minister)
        created = usuario_service.criar(
            self.db,
            UsuarioAdminIn(
                nome=self.minister.nome,
                email=self.minister.email,
                senha="senha-ministro-segura",
                perfil=MINISTRO,
                ministro_id=self.minister.id,
            ),
            self.admin,
        )
        self.assertEqual(created.ministro_id, self.minister.id)
        self.user = auth_service.authenticate(
            self.db,
            self.minister.email,
            "senha-ministro-segura",
            "127.0.0.2",
        )

    def tearDown(self):
        self.db.close()

    def _assigned_event(self, event_date: date) -> tuple[Evento, EscalaMinistro]:
        event = Evento(
            nome="Missa de teste",
            data=event_date,
            horario="19:00",
            tipo_evento="MISSA_PAROQUIAL",
            local="Igreja Matriz",
            cancelado=False,
        )
        self.db.add(event)
        self.db.flush()
        scale = Escala(evento_id=event.id, status="APROVADA")
        self.db.add(scale)
        self.db.flush()
        assignment = EscalaMinistro(
            escala_id=scale.id,
            ministro_id=self.minister.id,
            substituido=False,
        )
        self.db.add(assignment)
        self.db.commit()
        self.db.refresh(event)
        self.db.refresh(assignment)
        return event, assignment

    def test_minister_calendar_and_feedback_are_scoped(self):
        event, _ = self._assigned_event(date.today() - timedelta(days=1))
        output = portal_ministro_service.calendar(
            self.db,
            self.minister,
            date.today() - timedelta(days=7),
            date.today(),
        )
        self.assertEqual(len(output), 1)
        self.assertTrue(output[0].escalado)
        self.assertTrue(output[0].feedback_disponivel)

        feedback = portal_ministro_service.create_feedback(
            self.db,
            self.minister,
            self.user,
            FeedbackMinistroIn(evento_id=event.id, nota=9, comentario="Celebração organizada"),
        )
        self.assertEqual(feedback.ministro_id, self.minister.id)
        self.assertEqual(feedback.evento_nome, event.nome)
        updated_output = portal_ministro_service.calendar(
            self.db,
            self.minister,
            date.today() - timedelta(days=7),
            date.today(),
        )
        self.assertTrue(updated_output[0].feedback_enviado)
        self.assertFalse(updated_output[0].feedback_disponivel)
        with self.assertRaisesRegex(ValueError, "já enviou"):
            portal_ministro_service.create_feedback(
                self.db,
                self.minister,
                self.user,
                FeedbackMinistroIn(evento_id=event.id, nota=8),
            )

    def test_minister_can_only_change_own_unavailability(self):
        created = portal_ministro_service.create_unavailability(
            self.db,
            self.minister,
            self.user,
            IndisponibilidadeIn(data=date.today() + timedelta(days=5), motivo="Viagem"),
        )
        other = Ministro(nome="Outro Ministro", email="outro@example.com", ativo=True, funcao="LEITURA")
        self.db.add(other)
        self.db.flush()
        foreign = Indisponibilidade(ministro_id=other.id, data=date.today() + timedelta(days=6))
        self.db.add(foreign)
        self.db.commit()

        with self.assertRaisesRegex(ValueError, "não encontrada"):
            portal_ministro_service.delete_unavailability(
                self.db,
                self.minister,
                self.user,
                foreign.id,
            )
        portal_ministro_service.delete_unavailability(
            self.db,
            self.minister,
            self.user,
            created.id,
        )

    def test_google_calendar_payload_has_expected_schedule(self):
        _, assignment = self._assigned_event(date.today() + timedelta(days=10))
        with patch.dict(
            os.environ,
            {
                "GOOGLE_CALENDAR_TIMEZONE": "America/Sao_Paulo",
                "GOOGLE_CALENDAR_EVENT_DURATION_MINUTES": "120",
            },
            clear=False,
        ):
            body = google_calendar_service.build_event_body(assignment)
        self.assertEqual(body["attendees"][0]["email"], self.minister.email)
        self.assertEqual(
            [reminder["minutes"] for reminder in body["reminders"]["overrides"]],
            [10080, 4320, 60],
        )

    def test_scale_with_remote_event_is_not_deleted_while_calendar_is_disabled(self):
        _, assignment = self._assigned_event(date.today() + timedelta(days=10))
        self.db.add(
            SincronizacaoCalendario(
                escala_ministro_id=assignment.id,
                google_event_id="evento-google-existente",
                status="SINCRONIZADO",
            )
        )
        self.db.commit()

        with patch.dict(os.environ, {"GOOGLE_CALENDAR_ENABLED": "false"}, clear=False):
            with self.assertRaisesRegex(ValueError, "Não foi possível remover"):
                escala_service.deletar(self.db, assignment.escala_id)

        self.assertIsNotNone(self.db.get(Escala, assignment.escala_id))


if __name__ == "__main__":
    unittest.main()
