import os
import unittest
from unittest.mock import patch
from urllib.parse import parse_qs, urlparse

from app.database import _build_url


class DatabaseUrlTest(unittest.TestCase):
    def test_prisma_public_schema_is_removed_for_psycopg(self):
        with patch.dict(
            os.environ,
            {"DATABASE_URL": "postgresql://user:password@db.example.test/app?schema=public"},
            clear=False,
        ):
            parsed = urlparse(_build_url())
        self.assertEqual(parsed.scheme, "postgresql+psycopg2")
        self.assertNotIn("schema", parse_qs(parsed.query))

    def test_custom_schema_becomes_search_path(self):
        with patch.dict(
            os.environ,
            {"DATABASE_URL": "postgresql://user:password@db.example.test/app?schema=paroquia"},
            clear=False,
        ):
            parsed = urlparse(_build_url())
        self.assertEqual(parse_qs(parsed.query)["options"], ["-csearch_path=paroquia"])


if __name__ == "__main__":
    unittest.main()
