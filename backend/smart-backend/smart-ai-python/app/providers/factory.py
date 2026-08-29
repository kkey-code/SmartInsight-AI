import os
from typing import Optional
from .base import AIProvider
from .ollama_provider import OllamaProvider


class ProviderFactory:
    """AI Provider 工厂"""

    _providers = {
        "ollama": OllamaProvider,
    }

    @classmethod
    def get_provider(cls, provider_name: Optional[str] = None) -> AIProvider:
        if provider_name is None:
            provider_name = os.getenv("AI_PROVIDER", "ollama")

        provider_name = provider_name.lower()
        provider_class = cls._providers.get(provider_name)

        if not provider_class:
            raise ValueError(
                f"不支持的 AI 提供商: {provider_name}。"
                f"支持: {', '.join(cls._providers.keys())}"
            )

        if provider_name == "ollama":
            return provider_class(
                base_url=os.getenv("OLLAMA_BASE_URL", "http://localhost:11434"),
                model=os.getenv("OLLAMA_MODEL", "qwen3:4b"),
                temperature=float(os.getenv("TEMPERATURE", 0.7))
            )

        return provider_class()

    @classmethod
    def list_providers(cls) -> list:
        return list(cls._providers.keys())