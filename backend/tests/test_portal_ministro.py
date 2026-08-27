import os
import unittest
from datetime import date, timedelta
from unittest.mock import patch

from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker

from app.auth_profiles import MINISTRO
from app.database import Base
from app.models import Escala, EscalaMinistro, Evento, Indisponibilidade, Ministro
from app.schemas import FeedbackMinistroIn, IndisponibilidadeIn, UsuarioAdminIn
from app.services import auth_service, indisponibilidade_service, portal_ministro_service, usuario_service


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
            "EVENT_TIMEZONE": "America/Sao_Paulo",
            "EVENT_DURATION_MINUTES": "120",
        }
        with patch.dict(os.environ, environment, clear=False):
            auth_service.bootstrap_admin(self.db)
        self.admin = auth_service.authenticate(
            self.db, "admin@example.com", "senha-principal-segura", "127.0.0.1"
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
        usuario_service.criar(
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
        self.user = auth_service.authenticate(
            self.db, self.minister.email, "senha-ministro-segura", "127.0.0.2"
        )

    def tearDown(self):
        self.db.close()

    def _assigned_event(self, event_date: date) -> Evento:
        event = Evento(
            nome="Missa de teste",
            data=event_date,
            horario="08:00",
            tipo_evento="MISSA_PAROQUIAL",
            local="Igreja Matriz",
            cancelado=False,
        )
        self.db.add(event)
        self.db.flush()
        scale = Escala(evento_id=event.id, status="APROVADA")
        self.db.add(scale)
        self.db.flush()
        self.db.add(EscalaMinistro(
            escala_id=scale.id,
            ministro_id=self.minister.id,
            substituido=False,
        ))
        self.db.commit()
        self.db.refresh(event)
        return event

    def test_calendar_and_feedback_are_scoped_to_logged_minister(self):
        event = self._assigned_event(date.today() - timedelta(days=1))
        unrelated = Evento(
            nome="Evento público",
            data=date.today(),
            horario="19:00",
            tipo_evento="OUTRO",
            cancelado=False,
        )
        self.db.add(unrelated)
        self.db.commit()

        output = portal_ministro_service.calendar(
            self.db, self.minister, date.today() - timedelta(days=7), date.today()
        )
        self.assertEqual(len(output), 2)
        assigned = next(item for item in output if item.evento_id == event.id)
        self.assertTrue(assigned.escalado)
        self.assertTrue(assigned.feedback_disponivel)
        public = next(item for item in output if item.evento_id == unrelated.id)
        self.assertFalse(public.escalado)

        feedback = portal_ministro_service.create_feedback(
            self.db,
            self.minister,
            self.user,
            FeedbackMinistroIn(evento_id=event.id, nota=9, comentario="Celebração organizada"),
        )
        self.assertEqual(feedback.ministro_id, self.minister.id)
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
        other = Ministro(
            nome="Outro Ministro", email="outro@example.com", ativo=True, funcao="LEITURA"
        )
        self.db.add(other)
        self.db.flush()
        foreign = Indisponibilidade(
            ministro_id=other.id,
            data=date.today() + timedelta(days=6),
        )
        self.db.add(foreign)
        self.db.commit()

        with self.assertRaisesRegex(ValueError, "não encontrada"):
            portal_ministro_service.delete_unavailability(
                self.db, self.minister, self.user, foreign.id
            )
        portal_ministro_service.delete_unavailability(
            self.db, self.minister, self.user, created.id
        )

    def test_administrative_route_service_checks_parent_minister_id(self):
        other = Ministro(
            nome="Outro Ministro", email="outro@example.com", ativo=True, funcao="LEITURA"
        )
        self.db.add(other)
        self.db.flush()
        foreign = Indisponibilidade(
            ministro_id=other.id,
            data=date.today() + timedelta(days=2),
        )
        self.db.add(foreign)
        self.db.commit()

        with self.assertRaisesRegex(ValueError, "não encontrada"):
            indisponibilidade_service.deletar(self.db, self.minister.id, foreign.id)


if __name__ == "__main__":
    unittest.main()
