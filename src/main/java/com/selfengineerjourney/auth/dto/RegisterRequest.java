package com.selfengineerjourney.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record RegisterRequest(
        @NotNull(message = "The username is required")
        String username,
        @NotNull(message = "The email is required")
        @Email(message = "The email is invalid")
        String email,
        @NotNull(message = "The password is required")
        String password
) {
}
