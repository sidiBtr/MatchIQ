
package com.matchiq.matchiq.service;

import com.matchiq.matchiq.repository.UserRepository;
import com.matchiq.matchiq.Dto.SignupRequest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import com.matchiq.matchiq.entity.User;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import com.matchiq.matchiq.Dto.LoginRequest;
import java.util.Optional;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    @Test
    void shouldRejectRegistrationWhenEmailAlreadyExists() {

        // Arrange: Prepare the test data
        SignupRequest request = new SignupRequest(
                "John",
                "john@example.com",
                "password123"
        );

        when(userRepository.existsByEmail("john@example.com"))
                .thenReturn(true);

        // Act: Execute the method and capture its exception
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> authService.register(request)
        );

        // Assert: Verify the HTTP status is 409 Conflict
        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
    }
    @Test
    void shouldRegisterNewUserSuccessfully() {

    // ARRANGE: Prepare the signup request
    SignupRequest request = new SignupRequest(
            "John",
            "john@example.com",
            "password123"
    );

    // Pretend the email is not registered
    when(userRepository.existsByEmail("john@example.com"))
            .thenReturn(false);

    // Pretend BCrypt hashed the password
    when(passwordEncoder.encode("password123"))
            .thenReturn("hashedPassword123");

    // Pretend PostgreSQL saved the user
    when(userRepository.save(any(User.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

    // ACT: Execute the real registration method
    User result = authService.register(request);

    // ASSERT: Check the returned user
    assertNotNull(result);

    assertEquals("John", result.getName());
    assertEquals("john@example.com", result.getEmail());
    assertEquals("hashedPassword123", result.getPasswordHash());

    // Verify that the repository saved a user
    verify(userRepository).save(any(User.class));
    }

    @Test
void shouldRejectLoginWhenPasswordIsIncorrect() {

    // ARRANGE: Create a fake user
    User user = new User(
            "John",
            "john@example.com",
            "hashedPassword123"
    );

    // Create a login request with the wrong password
    LoginRequest request = new LoginRequest(
            "john@example.com",
            "wrongPassword"
    );

    // Pretend PostgreSQL found this user
    when(userRepository.findByEmail("john@example.com"))
            .thenReturn(Optional.of(user));

    // Pretend password verification failed
    when(passwordEncoder.matches(
            "wrongPassword",
            "hashedPassword123"
    )).thenReturn(false);

    // ACT: Attempt login and capture the exception
    ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> authService.login(request)
    );

    // ASSERT: Verify login was rejected with HTTP 401
    assertEquals(
            HttpStatus.UNAUTHORIZED,
            exception.getStatusCode()
    );

    // Verify that no JWT was generated
    verify(jwtService, never()).generateToken(any(User.class));
}
    @Test
void shouldLoginSuccessfullyWithCorrectCredentials() {

    // ARRANGE: Create a fake user
    User user = new User(
            "John",
            "john@example.com",
            "hashedPassword123"
    );

    // Create a login request
    LoginRequest request = new LoginRequest(
            "john@example.com",
            "correctPassword"
    );

    // Simulate finding the user in PostgreSQL
    when(userRepository.findByEmail("john@example.com"))
            .thenReturn(Optional.of(user));

    // Simulate successful password verification
    when(passwordEncoder.matches(
            "correctPassword",
            "hashedPassword123"
    )).thenReturn(true);

    // Simulate JWT generation
    when(jwtService.generateToken(user))
            .thenReturn("fake-jwt-token");

    // ACT: Call the real login method
    String token = authService.login(request);

    // ASSERT: Verify the returned token
    assertEquals("fake-jwt-token", token);

    // Verify JWT generation happened exactly once
    verify(jwtService, times(1)).generateToken(user);
}
@Test
void shouldRejectLoginWhenEmailDoesNotExist() {

    // ARRANGE: Create a login request
    LoginRequest request = new LoginRequest(
            "unknown@example.com",
            "password123"
    );

    // Simulate PostgreSQL not finding the user
    when(userRepository.findByEmail("unknown@example.com"))
            .thenReturn(Optional.empty());

    // ACT: Attempt to log in
    ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> authService.login(request)
    );

    // ASSERT: Verify HTTP 401 Unauthorized
    assertEquals(
            HttpStatus.UNAUTHORIZED,
            exception.getStatusCode()
    );

    // Verify no JWT was generated
    verify(jwtService, never()).generateToken(any(User.class));

    // Verify password verification was never attempted
    verify(passwordEncoder, never()).matches(
            any(String.class),
            any(String.class)
    );
}
}
