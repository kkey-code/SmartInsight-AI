from typing import Optional
from pydantic import BaseModel


class ChatRequest(BaseModel):
    prompt: str
    context: Optional[str] = None
    provider: Optional[str] = None


class ChatResponse(BaseModel):
    answer: str
    provider: str


class SummarizeRequest(BaseModel):
    text: str
    max_length: Optional[int] = None
    provider: Optional[str] = None


class SummarizeResponse(BaseModel):
    summary: str
    provider: str


class EmbedRequest(BaseModel):
    text: str
    provider: Optional[str] = None


class EmbedResponse(BaseModel):
    embedding: list
    provider: str


class ProviderListResponse(BaseModel):
    providers: list
    default: str