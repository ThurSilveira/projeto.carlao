from typing import Any, Callable
from fastapi import APIRouter


class CamelRouter(APIRouter):
    """APIRouter que serializa respostas com aliases camelCase por padrão."""

    def add_api_route(self, path: str, endpoint: Callable, *, response_model_by_alias: bool = True, **kwargs: Any) -> None:
        super().add_api_route(path, endpoint, response_model_by_alias=response_model_by_alias, **kwargs)
