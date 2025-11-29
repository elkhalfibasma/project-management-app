package com.pmip.ai.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pmip.ai.dto.AiDtos;
import com.pmip.ai.model.AiPrediction;
import com.pmip.ai.model.Project;
import com.pmip.ai.repo.AiPredictionRepository;
import com.pmip.ai.repo.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/ai")
@CrossOrigin(origins = "*")
public class AiController {

    @Autowired private ProjectRepository projectRepository;
    @Autowired private AiPredictionRepository aiPredictionRepository;
    @Autowired private RestTemplate restTemplate;
    @Value("${ai.url:http://localhost:8000}")
    private String aiUrl;

    private final ObjectMapper mapper = new ObjectMapper();

    @GetMapping("/predictDelay/{projectId}")
    public ResponseEntity<?> predictDelay(@PathVariable Long projectId) throws JsonProcessingException {
        Project p = projectRepository.findById(projectId).orElse(null);
        if (p == null) return ResponseEntity.notFound().build();
        AiDtos.ProjectInput input = buildInputFromProject(p);
        AiDtos.PredictionOut out = post("/predict-delay", input, AiDtos.PredictionOut.class);
        save("delay", p.getId(), out);
        return ResponseEntity.ok(out);
    }

    @GetMapping("/performance/{projectId}")
    public ResponseEntity<?> performance(@PathVariable Long projectId) throws JsonProcessingException {
        Project p = projectRepository.findById(projectId).orElse(null);
        if (p == null) return ResponseEntity.notFound().build();
        AiDtos.ProjectInput input = buildInputFromProject(p);
        AiDtos.PerformanceOut out = post("/predict-performance", input, AiDtos.PerformanceOut.class);
        save("performance", p.getId(), out);
        return ResponseEntity.ok(out);
    }

    @GetMapping("/recommendations/{projectId}")
    public ResponseEntity<?> recommendations(@PathVariable Long projectId) throws JsonProcessingException {
        Project p = projectRepository.findById(projectId).orElse(null);
        if (p == null) return ResponseEntity.notFound().build();
        AiDtos.ProjectInput input = buildInputFromProject(p);
        AiDtos.RecommendOut out = post("/recommend", input, AiDtos.RecommendOut.class);
        save("recommend", p.getId(), out);
        return ResponseEntity.ok(out);
    }

    private AiDtos.ProjectInput buildInputFromProject(Project p) {
        AiDtos.ProjectInput in = new AiDtos.ProjectInput();
        in.project_id = p.getId().intValue();
        in.progress = p.getProgress() == null ? 0 : p.getProgress();
        LocalDate end = p.getEndDate() == null ? LocalDate.now().plusDays(30) : p.getEndDate();
        int daysLeft = (int) ChronoUnit.DAYS.between(LocalDate.now(), end);
        in.days_left = Math.max(0, daysLeft);
        in.risk_factors = List.of();
        return in;
    }

    private <T> T post(String path, Object body, Class<T> clazz) {
        String url = aiUrl + path;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Object> entity = new HttpEntity<>(body, headers);
        ResponseEntity<T> res = restTemplate.postForEntity(url, entity, clazz);
        return res.getBody();
    }

    private void save(String type, Long projectId, Object value) throws JsonProcessingException {
        AiPrediction ap = new AiPrediction();
        ap.setProjectId(projectId);
        ap.setPredictionType(type);
        ap.setValue(mapper.writeValueAsString(value));
        aiPredictionRepository.save(ap);
    }
}
