package com.pmip.ai.dto;

import java.util.List;

public class AiDtos {
    public static class ProjectInput {
        public int project_id;
        public int progress;
        public int days_left;
        public List<String> risk_factors;
    }
    public static class PredictionOut {
        public int project_id;
        public int predicted_delay_days;
        public double confidence;
    }
    public static class PerformanceOut {
        public int project_id;
        public double score;
        public String explanation;
    }
    public static class RecommendOut {
        public int project_id;
        public java.util.List<String> actions;
    }
}
