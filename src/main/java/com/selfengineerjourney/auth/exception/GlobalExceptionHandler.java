package com.selfengineerjourney.auth.exception;

import com.selfengineerjourney.auth.dto.ExceptionResponse;
import com.selfengineerjourney.auth.model.ValidationError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ExceptionResponse> handleUserAlreadyExistsException(final UserAlreadyExistsException ex, final WebRequest request) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                new ExceptionResponse(
                        HttpStatus.CONFLICT.value(),
                        LocalDateTime.now(),
                        ex.getMessage(),
                        ((ServletWebRequest) request).getRequest().getRequestURI(),
                        ex.hasValidationErrors() ? ex.getErrors() : null
                )
        );
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleUserNotFoundException(final UserNotFoundException ex, final WebRequest request) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new ExceptionResponse(
                        HttpStatus.BAD_REQUEST.value(),
                        LocalDateTime.now(),
                        ex.getMessage(),
                        ((ServletWebRequest) request).getRequest().getRequestURI(),
                        null
                )
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleValidationErrors(MethodArgumentNotValidException ex, WebRequest request) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new ExceptionResponse(
                        HttpStatus.BAD_REQUEST.value(),
                        LocalDateTime.now(),
                        "Validation Error",
                        ((ServletWebRequest) request).getRequest().getRequestURI(),
                        ex.getBindingResult()
                                .getFieldErrors()
                                .stream()
                                .map(fieldError -> new ValidationError(
                                        fieldError.getField(),
                                        fieldError.getDefaultMessage()
                                ))
                                .collect(Collectors.toList())
                )
        );
    }
}
