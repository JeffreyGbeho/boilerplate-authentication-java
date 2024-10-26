package com.selfengineerjourney.auth.service.impl;

import com.selfengineerjourney.auth.dto.RegisterRequest;
import com.selfengineerjourney.auth.entity.Role;
import com.selfengineerjourney.auth.entity.RoleType;
import com.selfengineerjourney.auth.entity.User;
import com.selfengineerjourney.auth.exception.UserAlreadyExistsException;
import com.selfengineerjourney.auth.model.ValidationError;
import com.selfengineerjourney.auth.repository.RoleRepository;
import com.selfengineerjourney.auth.repository.UserRepository;
import com.selfengineerjourney.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
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
}
