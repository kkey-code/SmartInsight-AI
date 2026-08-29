import os
from fastapi import FastAPI, HTTPException
from dotenv import load_dotenv
from .models import (
    ChatRequest, ChatResponse,
    SummarizeRequest, SummarizeResponse,
    EmbedRequest, EmbedResponse,
    ProviderListResponse
)
from .providers.factory import ProviderFactory

load_dotenv()

app = FastAPI(
    title="Smart AI Service",
    description="统一 AI 接口，支持 Ollama 本地模型",
    version="1.0.0"
)

_default_provider = ProviderFactory.get_provider()


@app.get("/")
async def root():
    return {
        "service": "Smart AI Service",
        "version": "1.0.0",
        "default_provider": _default_provider.get_provider_name(),
        "providers": ProviderFactory.list_providers()
    }


@app.get("/ai/providers", response_model=ProviderListResponse)
async def list_providers():
    return ProviderListResponse(
        providers=ProviderFactory.list_providers(),
        default=os.getenv("AI_PROVIDER", "ollama")
    )


@app.post("/ai/chat", response_model=ChatResponse)
async def chat(request: ChatRequest):
    try:
        provider = ProviderFactory.get_provider(request.provider)
        answer = provider.chat(request.prompt, request.context)
        return ChatResponse(
            answer=answer,
            provider=provider.get_provider_name()
        )
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


@app.post("/ai/summarize", response_model=SummarizeResponse)
async def summarize(request: SummarizeRequest):
    try:
        provider = ProviderFactory.get_provider(request.provider)
        summary = provider.summarize(request.text, request.max_length)
        return SummarizeResponse(
            summary=summary,
            provider=provider.get_provider_name()
        )
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


@app.post("/ai/embed", response_model=EmbedResponse)
async def embed(request: EmbedRequest):
    try:
        provider = ProviderFactory.get_provider(request.provider)
        embedding = provider.embed(request.text)
        return EmbedResponse(
            embedding=embedding,
            provider=provider.get_provider_name()
        )
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))