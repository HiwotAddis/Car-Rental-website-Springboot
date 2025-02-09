package com.example.carrentalsystem.service;

import com.example.carrentalsystem.dto.LoginResponse;
import com.example.carrentalsystem.model.User;
import com.example.carrentalsystem.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final String SECRET_KEY = "f0cb7a4b2c0a6b812ed1d16f0c4327e6d27e392b8b5f9a671ff07f13e9fcd9f3"; // Secure key

    public String register(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return "User registered successfully!";
    }

    public LoginResponse login(String email, String password) {
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (passwordEncoder.matches(password, user.getPassword())) {
                String token = generateToken(user);
                return new LoginResponse(token, user); // Return token and user details
            }
        }
        throw new RuntimeException("Invalid credentials");
    }

    private String generateToken(User user) {
        return Jwts.builder()
                .setSubject(user.getEmail()) // Use email as subject
                .claim("role", user.getRole().name()) // Add role as claim
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 1-day expiration
                .signWith(SignatureAlgorithm.HS384, SECRET_KEY) // Secure algorithm
                .compact();
    }
}
