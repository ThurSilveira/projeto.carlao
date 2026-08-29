"""Ajustes de esquema compatíveis com bancos existentes.

O projeto ainda não usa uma ferramenta de migrations. Estes comandos são
idempotentes e executados antes do bootstrap de autenticação.
"""

from sqlalchemy import Engine, text


def apply_schema_migrations(engine: Engine) -> None:
    with engine.begin() as connection:
        connection.execute(
            text(
                """
                ALTER TABLE usuario
                ADD COLUMN IF NOT EXISTS deve_alterar_senha
                BOOLEAN NOT NULL DEFAULT FALSE
                """
            )
        )
