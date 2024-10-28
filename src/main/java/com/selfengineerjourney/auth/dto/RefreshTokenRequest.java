package com.selfengineerjourney.auth.dto;

import jakarta.validation.constraints.NotNull;

public record RefreshTokenRequest(
        @NotNull(message = "The refresh token is required")
        String refreshToken
) {
}
