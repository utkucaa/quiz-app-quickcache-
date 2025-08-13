package com.quickcache.QuickCache.controller;

import com.quickcache.QuickCache.dto.AuthRequest;
import com.quickcache.QuickCache.dto.AuthResponse;
import com.quickcache.QuickCache.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "User authentication and registration")
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Creates a new user account and returns JWT token")
    public ResponseEntity<AuthResponse> registerUser(@Valid @RequestBody AuthRequest authRequest) {
        log.info("Registration attempt for username: {}", authRequest.getUsername());
        
        try {
            AuthResponse response = userService.registerUser(authRequest);
            log.info("User registered successfully: {}", authRequest.getUsername());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("Registration failed for username {}: {}", authRequest.getUsername(), e.getMessage());
            throw e;
        }
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate user", description = "Authenticates user credentials and returns JWT token")
    public ResponseEntity<AuthResponse> authenticateUser(@Valid @RequestBody AuthRequest authRequest) {
        log.info("Login attempt for username: {}", authRequest.getUsername());
        
        try {
            AuthResponse response = userService.authenticateUser(authRequest);
            log.info("User authenticated successfully: {}", authRequest.getUsername());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Authentication failed for username {}: {}", authRequest.getUsername(), e.getMessage());
            throw e;
        }
    }

    @GetMapping("/check-username")
    @Operation(summary = "Check username availability", description = "Checks if a username is available for registration")
    public ResponseEntity<Boolean> checkUsernameAvailability(@RequestParam String username) {
        boolean isAvailable = userService.isUsernameAvailable(username);
        return ResponseEntity.ok(isAvailable);
    }

    @GetMapping("/check-email")
    @Operation(summary = "Check email availability", description = "Checks if an email is available for registration")
    public ResponseEntity<Boolean> checkEmailAvailability(@RequestParam String email) {
        boolean isAvailable = userService.isEmailAvailable(email);
        return ResponseEntity.ok(isAvailable);
    }
    
    @GetMapping("/health")
    @Operation(summary = "Authentication service health check", description = "Returns the health status of the authentication service")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Authentication service is running");
    }
}
