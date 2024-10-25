package com.selfengineerjourney.auth.model;

public record ValidationError(
        String field,
        String message
) {
}
