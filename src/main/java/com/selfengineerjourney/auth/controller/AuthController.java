package com.selfengineerjourney.auth.controller;

import com.selfengineerjourney.auth.dto.LoginRequest;
import com.selfengineerjourney.auth.dto.RegisterRequest;
import com.selfengineerjourney.auth.dto.UserDto;
import com.selfengineerjourney.auth.entity.User;
import com.selfengineerjourney.auth.service.AuthService;
import com.selfengineerjourney.auth.utils.mapper.UserMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<UserDto> register(@Valid @RequestBody LoginRequest request) {
        try {
            log.info(" POST api/auth/login - START");
            User userAuthenticated = authService.authenticate(request);
            return ResponseEntity.status(HttpStatus.OK).body(UserMapper.INSTANCE.toDto(userAuthenticated));
        } finally {
            log.info(" POST api/auth/login - DONE");
        }
    }
}
