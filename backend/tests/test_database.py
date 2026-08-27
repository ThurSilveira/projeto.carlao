import os
import unittest
from unittest.mock import patch

from sqlalchemy.engine import make_url

from app.database import _build_url


class DatabaseUrlTest(unittest.TestCase):
    def test_prisma_public_schema_is_removed_for_psycopg(self):
        with patch.dict(
            os.environ,
            {"DATABASE_URL": "postgresql://user:password@db.example.test/app?schema=public"},
            clear=False,
        ):
            parsed = make_url(_build_url())

        self.assertEqual(parsed.drivername, "postgresql+psycopg2")
        self.assertNotIn("schema", parsed.query)

    def test_custom_schema_becomes_search_path(self):
        with patch.dict(
            os.environ,
            {"DATABASE_URL": "postgresql://user:password@db.example.test/app?schema=paroquia"},
            clear=False,
        ):
            parsed = make_url(_build_url())

        self.assertEqual(parsed.query["options"], "-csearch_path=paroquia")

    def test_split_credentials_are_safely_encoded(self):
        environment = {
            "DATABASE_URL": "",
            "DB_HOST": "127.0.0.1",
            "DB_PORT": "5432",
            "DB_NAME": "escala_ministerial",
            "DB_USERNAME": "escala@local",
            "DB_PASSWORD": "senha:/?#[]@!$&'()*+,;=",
        }
        with patch.dict(os.environ, environment, clear=False):
            parsed = make_url(_build_url())

        self.assertEqual(parsed.username, environment["DB_USERNAME"])
        self.assertEqual(parsed.password, environment["DB_PASSWORD"])

    def test_rejects_invalid_schema(self):
        with patch.dict(
            os.environ,
            {"DATABASE_URL": "postgresql://user:password@db.example.test/app?schema=public%3Bdrop"},
            clear=False,
        ):
            with self.assertRaisesRegex(RuntimeError, "schema"):
                _build_url()


if __name__ == "__main__":
    unittest.main()
