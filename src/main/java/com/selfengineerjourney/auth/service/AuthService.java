package com.selfengineerjourney.auth.service;

import com.selfengineerjourney.auth.dto.RegisterRequest;
import com.selfengineerjourney.auth.entity.User;

public interface AuthService {
    User register(RegisterRequest request);
}
