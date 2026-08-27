import os
import unittest
from unittest.mock import patch

from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker

from app.database import Base
from app.models import Usuario
from app.services import auth_service


class AuthServiceTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.engine = create_engine("sqlite:///:memory:")
        cls.Session = sessionmaker(bind=cls.engine)

    def setUp(self):
        Base.metadata.drop_all(bind=self.engine)
        Base.metadata.create_all(bind=self.engine)
        self.db = self.Session()

    def tearDown(self):
        self.db.close()

    def test_missing_bootstrap_credentials_leave_service_closed_without_crashing(self):
        with patch.dict(
            os.environ,
            {"AUTH_ADMIN_EMAIL": "", "AUTH_ADMIN_PASSWORD": ""},
            clear=False,
        ):
            self.assertFalse(auth_service.bootstrap_admin(self.db))
        self.assertEqual(self.db.query(Usuario).count(), 0)

    def test_bootstrap_login_session_and_logout(self):
        environment = {
            "AUTH_ADMIN_NAME": "Admin Teste",
            "AUTH_ADMIN_EMAIL": "ADMIN@EXAMPLE.COM",
            "AUTH_ADMIN_PASSWORD": "uma-frase-senha-segura",
            "AUTH_SESSION_HOURS": "8",
        }
        with patch.dict(os.environ, environment, clear=False):
            self.assertTrue(auth_service.bootstrap_admin(self.db))
            self.assertFalse(auth_service.bootstrap_admin(self.db))
            user = self.db.query(Usuario).one()

            self.assertEqual(user.email, "admin@example.com")
            self.assertNotEqual(user.senha_hash, environment["AUTH_ADMIN_PASSWORD"])
            self.assertIsNone(auth_service.authenticate(self.db, user.email, "senha-incorreta", "127.0.0.1"))
            authenticated = auth_service.authenticate(
                self.db, user.email, environment["AUTH_ADMIN_PASSWORD"], "127.0.0.1"
            )
            self.assertEqual(authenticated.id, user.id)

            session, token = auth_service.create_session(self.db, user)
            self.assertNotEqual(session.token_hash, token)
            self.assertEqual(auth_service.get_session(self.db, token).id, session.id)
            auth_service.revoke_session(self.db, token)
            self.assertIsNone(auth_service.get_session(self.db, token))

    def test_password_rotation_revokes_old_session(self):
        user = Usuario(
            nome="Administrador",
            email="admin@example.com",
            senha_hash=auth_service.hash_password("senha-atual-bastante-longa"),
        )
        self.db.add(user)
        self.db.commit()
        _, old_token = auth_service.create_session(self.db, user)

        with self.assertRaisesRegex(ValueError, "senha atual está incorreta"):
            auth_service.change_password(self.db, user, "senha-errada", "nova-senha-bastante-longa")

        _, new_token = auth_service.change_password(
            self.db, user, "senha-atual-bastante-longa", "nova-senha-bastante-longa"
        )
        self.assertNotEqual(new_token, old_token)
        self.assertIsNone(auth_service.get_session(self.db, old_token))
        self.assertIsNotNone(auth_service.get_session(self.db, new_token))


if __name__ == "__main__":
    unittest.main()
