package com.quickcache.QuickCache.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

    
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private String token;
    private String username;
    private String email;
    private Integer totalScore;

    public static AuthResponse success(String token, String username, String email, Integer totalScore) {
        return new AuthResponse(token, username, email, totalScore);
    }
}