package com.pmip.auth.controller;

import com.pmip.auth.dto.AuthRequest;
import com.pmip.auth.dto.AuthResponse;
import com.pmip.auth.dto.RegisterRequest;
import com.pmip.auth.model.UserEntity;
import com.pmip.auth.repo.UserRepository;
import com.pmip.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired private AuthService authService;
    @Autowired private UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest req) {
        return ResponseEntity.ok(authService.register(req));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest req) {
        return ResponseEntity.ok(authService.login(req));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestHeader("Authorization") String bearer) {
        String token = bearer != null && bearer.startsWith("Bearer ") ? bearer.substring(7) : bearer;
        return ResponseEntity.ok(authService.refresh(token));
    }

    @GetMapping("/me")
    public ResponseEntity<?> me() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) return ResponseEntity.status(401).build();
        UserEntity u = userRepository.findByEmail(auth.getName()).orElse(null);
        if (u == null) return ResponseEntity.status(404).build();
        return ResponseEntity.ok(Map.of(
                "id", u.getId(),
                "name", u.getName(),
                "email", u.getEmail(),
                "role", u.getRole()
        ));
    }
}
