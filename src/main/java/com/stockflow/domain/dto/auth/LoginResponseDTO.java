package com.stockflow.domain.dto.auth;

import java.util.UUID;

public record LoginResponseDTO(
    String accessToken,
    String refreshToken,
    String tokenType,
    long expiresIn,
    UUID userId,
    String email,
    String name,
    String role
) {
    public LoginResponseDTO(String accessToken, String refreshToken, long expiresIn,
                            UUID userId, String email, String name, String role) {
        this(accessToken, refreshToken, "Bearer", expiresIn, userId, email, name, role);
    }
}
