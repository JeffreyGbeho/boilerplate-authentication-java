package com.selfengineerjourney.auth.controller;

import com.selfengineerjourney.auth.dto.*;
import com.selfengineerjourney.auth.entity.User;
import com.selfengineerjourney.auth.security.jwt.JwtService;
import com.selfengineerjourney.auth.service.AuthService;
import com.selfengineerjourney.auth.utils.mapper.UserMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Slf4j
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@Valid @RequestBody RegisterRequest request) {
        try {
            log.info(" POST api/auth/register - START");
            User userCreated = authService.register(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(UserMapper.INSTANCE.toDto(userCreated));
        } finally {
            log.info(" POST api/auth/register - DONE");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> register(@Valid @RequestBody LoginRequest request) {
        try {
            log.info(" POST api/auth/login - START");
            User userAuthenticated = authService.authenticate(request);
            JwtResponse response = jwtService.generateJwtToken(userAuthenticated);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } finally {
            log.info(" POST api/auth/login - DONE");
        }
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<JwtResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        try {
            log.info("POST /api/v1/auth/refresh-token - START");
            User userFromToken = authService.refreshToken(request.refreshToken());
            JwtResponse response = jwtService.generateJwtToken(userFromToken);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } finally {
            log.info("POST /api/v1/auth/refresh-token - DONE");
        }
    }

    // TODO: Remove this request and change Oauth2 success handler redirect
    @GetMapping("/oauth")
    public void testOauth(HttpServletRequest request) {
        StringBuilder requestDetails = new StringBuilder("\n=== Détails de la Requête ===\n");

        requestDetails.append("URL: ").append(request.getRequestURL()).append("\n");

        requestDetails.append("\n=== Query Parameters ===\n");
        request.getParameterMap().forEach((key, values) -> {
            requestDetails.append(key).append(": ");
            requestDetails.append(String.join(", ", values)).append("\n");
        });

        System.out.println(requestDetails.toString());
    }
}
