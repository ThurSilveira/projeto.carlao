from dataclasses import dataclass


ADMINISTRADOR = "ADMINISTRADOR"
COORDENADOR = "COORDENADOR"
CONSULTA = "CONSULTA"
MINISTRO = "MINISTRO"


@dataclass(frozen=True)
class ProfileDefinition:
    nome: str
    descricao: str
    permissoes: frozenset[str]


PROFILES: dict[str, ProfileDefinition] = {
    ADMINISTRADOR: ProfileDefinition(
        nome=ADMINISTRADOR,
        descricao="Acesso integral, incluindo usuários, perfis, auditoria e configurações de segurança.",
        permissoes=frozenset({"*"}),
    ),
    COORDENADOR: ProfileDefinition(
        nome=COORDENADOR,
        descricao="Gerencia ministros, eventos, escalas, indisponibilidades e feedbacks; consulta auditoria.",
        permissoes=frozenset(
            {
                "ministros:ler",
                "ministros:gerir",
                "eventos:ler",
                "eventos:gerir",
                "escalas:ler",
                "escalas:gerir",
                "feedbacks:ler",
                "feedbacks:gerir",
                "auditoria:ler",
            }
        ),
    ),
    CONSULTA: ProfileDefinition(
        nome=CONSULTA,
        descricao="Acesso somente para consulta dos dados operacionais, sem alterações ou auditoria.",
        permissoes=frozenset(
            {
                "ministros:ler",
                "eventos:ler",
                "escalas:ler",
                "feedbacks:ler",
            }
        ),
    ),
    MINISTRO: ProfileDefinition(
        nome=MINISTRO,
        descricao="Acesso pessoal a calendário, indisponibilidades e feedback dos próprios eventos.",
        permissoes=frozenset({"portal_ministro:usar"}),
    ),
}


def normalize_profile(profile: str) -> str:
    normalized = profile.strip().upper()
    if normalized not in PROFILES:
        raise ValueError(f"Perfil inválido. Use: {', '.join(PROFILES)}.")
    return normalized


def has_permission(profile: str, permission: str) -> bool:
    definition = PROFILES.get(profile)
    return bool(definition and ("*" in definition.permissoes or permission in definition.permissoes))
