package com.matchiq.matchiq.service;

import java.util.Locale;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.matchiq.matchiq.Dto.SignupRequest;
import com.matchiq.matchiq.Dto.LoginRequest;
import com.matchiq.matchiq.entity.User;
import com.matchiq.matchiq.repository.UserRepository;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public User register(SignupRequest request) {

        String email = request.email()
                .trim()
                .toLowerCase(Locale.ROOT);

        if (userRepository.existsByEmail(email)) {
                     throw new ResponseStatusException(
                     HttpStatus.CONFLICT,
                     "Email is already registered"
                );
        }

        String hashedPassword =
                passwordEncoder.encode(request.password());

        User user = new User(
                request.name().trim(),
                email,
                hashedPassword
        );

        return userRepository.save(user);
    }

    public String login(LoginRequest request) {

    String email = request.email()
            .trim()
            .toLowerCase(Locale.ROOT);

    User user = userRepository.findByEmail(email)
            .orElseThrow(() ->
                    new ResponseStatusException(
                            HttpStatus.UNAUTHORIZED,
                            "Invalid email or password"
                    )
            );

    boolean passwordMatches = passwordEncoder.matches(
            request.password(),
            user.getPasswordHash()
    );

    if (!passwordMatches) {
        throw new ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                "Invalid email or password"
        );
    }

    return jwtService.generateToken(user);
  }
}