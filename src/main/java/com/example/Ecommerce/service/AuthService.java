package com.example.Ecommerce.service;

import com.example.Ecommerce.dto.request.LoginRequest;
import com.example.Ecommerce.dto.request.RegisterRequest;
import com.example.Ecommerce.dto.response.AuthResponse;

public interface AuthService {
    void register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}