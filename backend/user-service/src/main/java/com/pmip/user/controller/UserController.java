package com.pmip.user.controller;

import com.pmip.user.model.User;
import com.pmip.user.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "*")
public class UserController {
    @Autowired private UserRepository repo;
    @Autowired(required = false) private KafkaTemplate<String, String> kafkaTemplate;

    @GetMapping
    public List<User> all(){return repo.findAll();}

    @GetMapping("/{id}")
    public ResponseEntity<User> get(@PathVariable Long id){
        return repo.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public User create(@RequestBody User u){
        User saved = repo.save(u);
        if (kafkaTemplate!=null) kafkaTemplate.send("user-events","user-created","{"+saved.getId()+"}");
        return saved;
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> update(@PathVariable Long id, @RequestBody User u){
        return repo.findById(id).map(ex -> {
            ex.setName(u.getName()); ex.setEmail(u.getEmail()); ex.setRole(u.getRole());
            User saved = repo.save(ex);
            if (kafkaTemplate!=null) kafkaTemplate.send("user-events","user-updated","{"+saved.getId()+"}");
            return ResponseEntity.ok(saved);
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        if(!repo.existsById(id)) return ResponseEntity.notFound().build();
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
