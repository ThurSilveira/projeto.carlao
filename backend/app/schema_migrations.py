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
        connection.execute(
            text(
                """
                DO $$
                BEGIN
                    IF EXISTS (
                        SELECT 1
                        FROM information_schema.columns
                        WHERE table_schema = current_schema()
                          AND table_name = 'ministro'
                          AND column_name = 'visitas_ao_infermo'
                    ) AND NOT EXISTS (
                        SELECT 1
                        FROM information_schema.columns
                        WHERE table_schema = current_schema()
                          AND table_name = 'ministro'
                          AND column_name = 'visitas_aos_enfermos'
                    ) THEN
                        ALTER TABLE ministro
                        RENAME COLUMN visitas_ao_infermo TO visitas_aos_enfermos;
                    END IF;
                END
                $$
                """
            )
        )
