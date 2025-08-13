package com.quickcache.QuickCache.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthRequest {

    @NotBlank
    private String username;

    @Email
    private String email;

    @NotBlank
    private String password;

                
    public AuthRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }
}