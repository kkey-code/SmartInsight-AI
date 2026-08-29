from abc import ABC, abstractmethod
from typing import List, Optional


class AIProvider(ABC):
    """所有 AI 提供者的统一接口"""

    @abstractmethod
    def chat(self, prompt: str, context: Optional[str] = None) -> str:
        """对话/问答"""
        pass

    @abstractmethod
    def embed(self, text: str) -> List[float]:
        """文本向量化"""
        pass

    @abstractmethod
    def summarize(self, text: str, max_length: Optional[int] = None) -> str:
        """文本摘要"""
        pass

    @abstractmethod
    def get_provider_name(self) -> str:
        """返回提供商名称"""
        pass