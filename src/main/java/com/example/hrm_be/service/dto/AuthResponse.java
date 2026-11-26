package com.example.hrm_be.service.dto;

import com.example.hrm_be.domain.Role;
import com.example.hrm_be.domain.User;

public record AuthResponse(
        String accessToken,
        String tokenType,
        String username,
        Role role
) {
    public static AuthResponse of(String accessToken, User user) {
        return new AuthResponse(accessToken, "Bearer", user.getUsername(), user.getRole());
    }
}
