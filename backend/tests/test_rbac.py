import os
import unittest
from unittest.mock import patch

from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker

from app.auth_profiles import ADMINISTRADOR, CONSULTA, COORDENADOR, MINISTRO, has_permission
from app.database import Base
from app.models import Ministro
from app.schemas import UsuarioAdminIn, UsuarioAdminUpdate
from app.services import auth_service, usuario_service


class RbacTest(unittest.TestCase):
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

    def tearDown(self):
        self.db.close()

    def test_profile_permission_matrix(self):
        self.assertTrue(has_permission(ADMINISTRADOR, "usuarios:gerir"))
        self.assertTrue(has_permission(COORDENADOR, "eventos:gerir"))
        self.assertTrue(has_permission(COORDENADOR, "auditoria:ler"))
        self.assertFalse(has_permission(COORDENADOR, "usuarios:gerir"))
        self.assertTrue(has_permission(CONSULTA, "eventos:ler"))
        self.assertFalse(has_permission(CONSULTA, "eventos:gerir"))
        self.assertFalse(has_permission(CONSULTA, "auditoria:ler"))
        self.assertTrue(has_permission(MINISTRO, "portal_ministro:usar"))
        self.assertFalse(has_permission(MINISTRO, "eventos:ler"))

    def test_first_admin_is_protected(self):
        output = usuario_service.obter(self.db, self.admin.id)
        self.assertEqual(output.perfil, ADMINISTRADOR)
        self.assertTrue(output.protegido)
        self.assertTrue(output.ativo)

        with self.assertRaisesRegex(ValueError, "protegido"):
            usuario_service.deletar(self.db, self.admin.id, self.admin)
        with self.assertRaisesRegex(ValueError, "própria conta"):
            usuario_service.redefinir_senha(
                self.db,
                self.admin.id,
                "outra-senha-bastante-segura",
                self.admin,
            )

    def test_admin_can_manage_other_users(self):
        created = usuario_service.criar(
            self.db,
            UsuarioAdminIn(
                nome="Pessoa Coordenadora",
                email="coordenador@example.com",
                senha="senha-coordenador-segura",
                perfil=COORDENADOR,
                ativo=True,
            ),
            self.admin,
        )
        self.assertEqual(created.perfil, COORDENADOR)
        self.assertFalse(created.protegido)

        updated = usuario_service.atualizar(
            self.db,
            created.id,
            UsuarioAdminUpdate(
                nome="Pessoa Consulta",
                email="consulta@example.com",
                perfil=CONSULTA,
                ativo=True,
            ),
            self.admin,
        )
        self.assertEqual(updated.perfil, CONSULTA)

        usuario_service.redefinir_senha(
            self.db,
            created.id,
            "nova-senha-de-consulta",
            self.admin,
        )
        usuario_service.deletar(self.db, created.id, self.admin)
        with self.assertRaisesRegex(ValueError, "não encontrado"):
            usuario_service.obter(self.db, created.id)

    def test_minister_profile_requires_unique_matching_link(self):
        minister = Ministro(
            nome="Pessoa Ministra",
            email="ministra@example.com",
            ativo=True,
            funcao="LEITURA",
        )
        self.db.add(minister)
        self.db.commit()
        self.db.refresh(minister)

        with self.assertRaisesRegex(ValueError, "igual ao e-mail"):
            usuario_service.criar(
                self.db,
                UsuarioAdminIn(
                    nome=minister.nome,
                    email="outro@example.com",
                    senha="senha-ministro-segura",
                    perfil=MINISTRO,
                    ministro_id=minister.id,
                ),
                self.admin,
            )

        created = usuario_service.criar(
            self.db,
            UsuarioAdminIn(
                nome=minister.nome,
                email=minister.email,
                senha="senha-ministro-segura",
                perfil=MINISTRO,
                ministro_id=minister.id,
            ),
            self.admin,
        )
        self.assertEqual(created.ministro_id, minister.id)

        with self.assertRaisesRegex(ValueError, "Já existe um usuário"):
            usuario_service.criar(
                self.db,
                UsuarioAdminIn(
                    nome="Segundo Acesso",
                    email=minister.email,
                    senha="segunda-senha-ministro-segura",
                    perfil=MINISTRO,
                    ministro_id=minister.id,
                ),
                self.admin,
            )


if __name__ == "__main__":
    unittest.main()
