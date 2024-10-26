package com.selfengineerjourney.auth.exception;

import com.selfengineerjourney.auth.model.ValidationError;
import lombok.Getter;

import java.util.List;

@Getter
public class UserAlreadyExistsException extends RuntimeException {
    private List<ValidationError> errors;

    public UserAlreadyExistsException(String message) {
        super(message);
    }

    public UserAlreadyExistsException(List<ValidationError> errors) {
        super("User already exists");
        this.errors = errors;
    }

    public boolean hasValidationErrors() {
        return !errors.isEmpty();
    }
}
