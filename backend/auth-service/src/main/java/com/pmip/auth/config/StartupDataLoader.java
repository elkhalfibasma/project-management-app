package com.pmip.auth.config;

import com.pmip.auth.model.UserEntity;
import com.pmip.auth.repo.UserRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Profile({"default","dev"})
public class StartupDataLoader implements ApplicationRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;

    public StartupDataLoader(UserRepository userRepository, PasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.encoder = encoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!userRepository.existsByEmail("admin@pmip.local")) {
            UserEntity u = new UserEntity();
            u.setName("Admin");
            u.setEmail("admin@pmip.local");
            u.setPassword(encoder.encode("admin"));
            u.setRole("ADMIN");
            userRepository.save(u);
        }
    }
}
