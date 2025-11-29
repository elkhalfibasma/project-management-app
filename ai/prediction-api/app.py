from fastapi import FastAPI
from pydantic import BaseModel
from typing import List, Optional
from datetime import date
import math

app = FastAPI(title="PMIP Prediction API")

class ProjectInput(BaseModel):
    project_id: int
    progress: int  # 0..100
    days_left: int
    risk_factors: Optional[List[str]] = None

class PredictionOut(BaseModel):
    project_id: int
    predicted_delay_days: int
    confidence: float

class PerformanceOut(BaseModel):
    project_id: int
    score: float
    explanation: str

class RecommendOut(BaseModel):
    project_id: int
    actions: List[str]

@app.get("/")
def root():
    return {"status": "ok"}

@app.post("/predict-delay", response_model=PredictionOut)
def predict_delay(inp: ProjectInput):
    # Simple heuristic model as a stand-in for linear regression
    progress_ratio = max(0.01, min(0.99, inp.progress / 100.0))
    ideal_days_left = (1 - progress_ratio) * 100
    gap = ideal_days_left - inp.days_left
    risk_bonus = 0
    if inp.risk_factors:
        risk_bonus = len(inp.risk_factors) * 1.5
    delay = max(0, int(gap + risk_bonus))
    conf = max(0.5, min(0.95, 0.9 - abs(gap)/150))
    return {"project_id": inp.project_id, "predicted_delay_days": delay, "confidence": round(conf, 2)}

@app.post("/predict-performance", response_model=PerformanceOut)
def predict_performance(inp: ProjectInput):
    score = max(0.0, min(100.0, 100 - (inp.days_left * 0.4) + (inp.progress * 0.6)))
    exp = "Score basé sur progression et temps restant"
    return {"project_id": inp.project_id, "score": round(score,2), "explanation": exp}

@app.post("/recommend", response_model=RecommendOut)
def recommend(inp: ProjectInput):
    actions = [
        "Réallouer des ressources",
        "Replanifier tâches critiques",
        "Activer mode focus pour l'équipe",
    ]
    if inp.progress < 50:
        actions.append("Diviser les grandes tâches en sous-tâches")
    if inp.days_left < 14:
        actions.append("Mettre en place des points quotidiens")
    return {"project_id": inp.project_id, "actions": actions}

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)
