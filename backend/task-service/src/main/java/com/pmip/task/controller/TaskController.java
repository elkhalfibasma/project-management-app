package com.pmip.task.controller;

import com.pmip.task.model.Task;
import com.pmip.task.repo.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")
@CrossOrigin(origins = "*")
public class TaskController {
    @Autowired private TaskRepository repo;
    @Autowired(required = false) private KafkaTemplate<String, String> kafkaTemplate;
    @Autowired(required = false) private SimpMessagingTemplate ws;

    @GetMapping
    public List<Task> all(){ return repo.findAll(); }

    @GetMapping("/{id}")
    public ResponseEntity<Task> get(@PathVariable Long id){
        return repo.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/by-project/{projectId}")
    public List<Task> byProject(@PathVariable Long projectId){ return repo.findByProjectId(projectId); }

    @PostMapping
    public Task create(@RequestBody Task t){
        Task saved = repo.save(t);
        send("task-created","{\"id\":"+saved.getId()+"}");
        notifyWs("created", saved.getId());
        return saved;
    }

    @PutMapping("/{id}")
    public ResponseEntity<Task> update(@PathVariable Long id, @RequestBody Task t){
        return repo.findById(id).map(ex -> {
            ex.setTitle(t.getTitle()); ex.setDescription(t.getDescription()); ex.setPriority(t.getPriority());
            ex.setStatus(t.getStatus()); ex.setEstimatedDuration(t.getEstimatedDuration()); ex.setDueDate(t.getDueDate());
            ex.setAssigneeId(t.getAssigneeId()); ex.setProjectId(t.getProjectId());
            Task saved = repo.save(ex);
            send("task-updated","{\"id\":"+saved.getId()+"}");
            notifyWs("updated", saved.getId());
            return ResponseEntity.ok(saved);
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        if(!repo.existsById(id)) return ResponseEntity.notFound().build();
        repo.deleteById(id);
        send("task-deleted","{\"id\":"+id+"}");
        notifyWs("deleted", id);
        return ResponseEntity.noContent().build();
    }

    private void send(String key, String payload){ if (kafkaTemplate!=null) kafkaTemplate.send("task-events", key, payload); }
    private void notifyWs(String type, Long id){ if(ws!=null) ws.convertAndSend("/topic/tasks", java.util.Map.of("type", type, "id", id)); }
}
