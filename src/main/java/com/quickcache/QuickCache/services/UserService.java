package com.quickcache.QuickCache.services;

import com.quickcache.QuickCache.dto.AuthRequest;
import com.quickcache.QuickCache.dto.AuthResponse;
import com.quickcache.QuickCache.entities.User;
import com.quickcache.QuickCache.exception.UserAlreadyExistsException;
import com.quickcache.QuickCache.exception.UserNotFoundException;
import com.quickcache.QuickCache.repository.UserRepository;
import com.quickcache.QuickCache.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * UserService - Kullanıcı yönetimi ve authentication işlemlerini yönetir
 * 
 * Bu servis şu temel işlevleri yerine getirir:
 * 1. USER REGISTRATION: Yeni kullanıcı kaydetme ve doğrulama
 * 2. USER AUTHENTICATION: Kullanıcı girişi ve JWT token üretimi
 * 3. USER MANAGEMENT: Kullanıcı bulma, skor güncelleme işlemleri
 * 4. VALIDATION: Username/email müsaitlik kontrolü
 **/

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthResponse registerUser(AuthRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException("Username already exists");
        }
        
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("Email already exists");
        }
        
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setTotalScore(0);
        
        User savedUser = userRepository.save(user);
        
        String token = jwtUtil.generateTokenWithClaims(
            savedUser.getUsername(), 
            "USER", 
            savedUser.getId()
        );
        
        return AuthResponse.success(token, savedUser.getUsername(), 
                                  savedUser.getEmail(), savedUser.getTotalScore());
    }

    public AuthResponse authenticateUser(AuthRequest request) {
        // Find user
        User user = findByUsername(request.getUsername());
        
        // Check password
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid credentials");
        }
        
        String token = jwtUtil.generateTokenWithClaims(
            user.getUsername(),
            "USER",
            user.getId()
        );
        
        return AuthResponse.success(token, user.getUsername(), 
                                  user.getEmail(), user.getTotalScore());
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + id));
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + username));
    }

    public User updateUserScore(Long userId, Integer additionalScore) {
        User user = findById(userId);
        user.updateTotalScore(additionalScore);
        return userRepository.save(user);
    }

    public boolean isUsernameAvailable(String username) {
        return !userRepository.existsByUsername(username);
    }


    public boolean isEmailAvailable(String email) {
        return !userRepository.existsByEmail(email);
    }
}