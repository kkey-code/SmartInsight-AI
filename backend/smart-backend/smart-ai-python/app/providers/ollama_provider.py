import httpx
from typing import List, Optional
from .base import AIProvider


class OllamaProvider(AIProvider):
    def __init__(
        self,
        base_url: str = "http://localhost:11434",
        model: str = "qwen3:4b",
        temperature: float = 0.7
    ):
        self.base_url = base_url.rstrip('/')
        self.model = model
        self.temperature = temperature
        self.client = httpx.Client(timeout=120.0)

    def chat(self, prompt: str, context: Optional[str] = None) -> str:
        full_prompt = f"{context}\n\n{prompt}" if context else prompt

        response = self.client.post(
            f"{self.base_url}/api/generate",
            json={
                "model": self.model,
                "prompt": full_prompt,
                "stream": False,
                "temperature": self.temperature
            }
        )
        response.raise_for_status()
        return response.json().get("response", "")

    def embed(self, text: str) -> List[float]:
        response = self.client.post(
            f"{self.base_url}/api/embeddings",
            json={
                "model": self.model,
                "prompt": text
            }
        )
        response.raise_for_status()
        return response.json().get("embedding", [])

    def summarize(self, text: str, max_length: Optional[int] = None) -> str:
        prompt = f"请用{'{}字以内'.format(max_length) if max_length else '简洁的方式'}总结以下内容：\n\n{text}"
        return self.chat(prompt)

    def get_provider_name(self) -> str:
        return f"ollama/{self.model}"