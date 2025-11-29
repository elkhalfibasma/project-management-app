package com.pmip.project.controller;

import com.pmip.project.model.Project;
import com.pmip.project.repo.ProjectRepository;
import com.pmip.project.messaging.ProjectEventProducer;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/projects")
@CrossOrigin(origins = "*")
public class ProjectController {

    @Autowired private ProjectRepository projectRepository;
    @Autowired private ProjectEventProducer producer;

    @GetMapping
    public List<Project> all() { return projectRepository.findAll(); }

    @GetMapping("/{id}")
    public ResponseEntity<Project> get(@PathVariable Long id) {
        return projectRepository.findById(id).map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Project create(@Valid @RequestBody Project p) {
        Project saved = projectRepository.save(p);
        producer.send("project-created", "{\"id\":"+saved.getId()+"}");
        return saved;
    }

    @PutMapping("/{id}")
    public ResponseEntity<Project> update(@PathVariable Long id, @Valid @RequestBody Project p) {
        return projectRepository.findById(id).map(existing -> {
            existing.setTitle(p.getTitle());
            existing.setDescription(p.getDescription());
            existing.setBudget(p.getBudget());
            existing.setStartDate(p.getStartDate());
            existing.setEndDate(p.getEndDate());
            existing.setStatus(p.getStatus());
            existing.setProgress(p.getProgress());
            existing.setManagerId(p.getManagerId());
            Project saved = projectRepository.save(existing);
            producer.send("project-updated", "{\"id\":"+saved.getId()+"}");
            return ResponseEntity.ok(saved);
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!projectRepository.existsById(id)) return ResponseEntity.notFound().build();
        projectRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
