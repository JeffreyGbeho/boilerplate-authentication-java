package com.selfengineerjourney.auth.dto;

import jakarta.validation.constraints.NotNull;

public record LoginRequest(
        @NotNull(message = "The username is required")
        String username,
        @NotNull(message = "The password is required")
        String password
) {
}
