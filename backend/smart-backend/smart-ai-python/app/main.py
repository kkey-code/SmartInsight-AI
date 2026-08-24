from fastapi import FastAPI
from pydantic import BaseModel


app = FastAPI(
    title="SmartInsight AI Service",
    version="1.0.0",
)


class ChatRequest(BaseModel):
    documentId: int
    question: str


class ChatResponse(BaseModel):
    content: str


@app.get("/health")
def health():
    return {"status": "UP"}


@app.post("/api/v1/ai/chat", response_model=ChatResponse)
def chat(request: ChatRequest):
    return ChatResponse(
        content=f"AI service received: {request.question}"
    )
