package com.selfengineerjourney.auth.controller;

import com.selfengineerjourney.auth.dto.RegisterRequest;
import com.selfengineerjourney.auth.entity.User;
import com.selfengineerjourney.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@Slf4j
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest request) {
        try {
            log.info(" POST api/v1/auth/register - START");
            User userCreated = authService.register(request);
            return ResponseEntity.ok("Registered");
        } finally {
            log.info(" POST api/v1/auth/register - DONE");
        }
    }
}
