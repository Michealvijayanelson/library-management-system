package com.example.demo.service;

import com.example.demo.controller.AuthController.RegisterRequest;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User register(RegisterRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new RuntimeException("Email already in use");
        }
        String role = (req.getRole() == null || req.getRole().isBlank()) ? "ROLE_USER" : req.getRole();
        User u = new User(req.getName(), req.getEmail(), req.getDepartment(),
                passwordEncoder.encode(req.getPassword()), role);
        return userRepository.save(u);
    }
}
