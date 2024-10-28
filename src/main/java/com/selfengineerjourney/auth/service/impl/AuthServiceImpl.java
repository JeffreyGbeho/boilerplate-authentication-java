package com.selfengineerjourney.auth.service.impl;

import com.selfengineerjourney.auth.dto.LoginRequest;
import com.selfengineerjourney.auth.dto.RegisterRequest;
import com.selfengineerjourney.auth.entity.Role;
import com.selfengineerjourney.auth.entity.RoleType;
import com.selfengineerjourney.auth.entity.User;
import com.selfengineerjourney.auth.exception.TokenInvalidException;
import com.selfengineerjourney.auth.exception.UserAlreadyExistsException;
import com.selfengineerjourney.auth.exception.UserNotFoundException;
import com.selfengineerjourney.auth.model.ValidationError;
import com.selfengineerjourney.auth.repository.RoleRepository;
import com.selfengineerjourney.auth.repository.UserRepository;
import com.selfengineerjourney.auth.security.jwt.JwtService;
import com.selfengineerjourney.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Override
    public User register(RegisterRequest request) {
        checkUserBeforeRegistration(request.email(), request.username());

        Set<Role> roles = new HashSet<>();
        roles.add(roleRepository.findByName(RoleType.ROLE_USER).orElse(null));

        return userRepository.save(
                User.builder()
                        .email(request.email())
                        .username(request.username())
                        .password(passwordEncoder.encode(request.password()))
                        .provider("local")
                        .roles(roles)
                        .enabled(true)
                        .build()
        );
    }

    private void checkUserBeforeRegistration(String email, String username) {
        List<ValidationError> errors = new ArrayList<>();

        if (userRepository.existsByEmail(email)) {
            errors.add(new ValidationError("email", "An user with this email already exists"));
        }
        if (userRepository.existsByUsername(username)) {
            errors.add(new ValidationError("username", "An user with the username already exists"));
        }

        if (!errors.isEmpty()) {
            throw new UserAlreadyExistsException(errors);
        }
    }

    @Override
    public User authenticate(LoginRequest request) {
        User user = userRepository.findByEmailOrUsername(request.username()).orElseThrow(() -> new UserNotFoundException("The user " + request.username() + " doesn't exist"));

        if (!user.isLocalProviderAuthentication()) {
            throw new UserNotFoundException("Credentials invalid");
        }

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.username(), request.password()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return user;
    }

    @Override
    public User refreshToken(String refreshToken) {
        if (jwtService.isExpired(refreshToken) || !jwtService.extractClaimValue(refreshToken, "typ").equals("Refresh")) {
            throw new TokenInvalidException("Token invalid");
        }

        Long userId = Long.parseLong(jwtService.extractSubject(refreshToken));
        return userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));

    }
}
