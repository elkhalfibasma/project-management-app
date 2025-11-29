package com.pmip.auth.service;

import com.pmip.auth.dto.AuthRequest;
import com.pmip.auth.dto.AuthResponse;
import com.pmip.auth.dto.RegisterRequest;
import com.pmip.auth.model.UserEntity;
import com.pmip.auth.repo.UserRepository;
import com.pmip.auth.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {
    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private AuthenticationManager authenticationManager;
    @Autowired private JwtService jwtService;

    public AuthResponse register(RegisterRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new RuntimeException("Email déjà utilisé");
        }
        UserEntity u = new UserEntity();
        u.setName(req.getName());
        u.setEmail(req.getEmail());
        u.setPassword(passwordEncoder.encode(req.getPassword()));
        u.setRole(req.getRole());
        userRepository.save(u);
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", u.getRole());
        String token = jwtService.generateToken(u.getEmail(), claims);
        return new AuthResponse(token, 60*60*24);
    }

    public AuthResponse login(AuthRequest req) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword()));
        UserEntity u = userRepository.findByEmail(req.getEmail()).orElseThrow();
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", u.getRole());
        String token = jwtService.generateToken(u.getEmail(), claims);
        return new AuthResponse(token, 60*60*24);
    }

    public AuthResponse refresh(String oldToken) {
        String username = jwtService.extractUsername(oldToken);
        UserEntity u = userRepository.findByEmail(username).orElseThrow();
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", u.getRole());
        String token = jwtService.generateToken(username, claims);
        return new AuthResponse(token, 60*60*24);
    }
}
