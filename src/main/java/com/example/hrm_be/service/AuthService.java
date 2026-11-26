package com.example.hrm_be.service;

import com.example.hrm_be.service.dto.AuthResponse;
import com.example.hrm_be.service.dto.LoginRequest;
import com.example.hrm_be.service.dto.RegisterRequest;

public interface AuthService {

    AuthResponse login(LoginRequest request);

    AuthResponse register(RegisterRequest request);
}
