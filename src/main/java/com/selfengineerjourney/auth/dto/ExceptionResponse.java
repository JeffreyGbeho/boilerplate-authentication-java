package com.selfengineerjourney.auth.dto;

import com.selfengineerjourney.auth.model.ValidationError;

import java.time.LocalDateTime;
import java.util.List;

public record ExceptionResponse(
        int status,
        LocalDateTime timestamp,
        String message,
        String path,
        List<ValidationError> errors
) {
}

