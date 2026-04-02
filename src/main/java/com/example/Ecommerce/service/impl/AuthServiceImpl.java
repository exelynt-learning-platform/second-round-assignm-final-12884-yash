package com.example.Ecommerce.service.impl;

import com.example.Ecommerce.dto.request.LoginRequest;
import com.example.Ecommerce.dto.request.RegisterRequest;
import com.example.Ecommerce.dto.response.AuthResponse;
import com.example.Ecommerce.entity.User;
import com.example.Ecommerce.enums.Role;
import com.example.Ecommerce.exception.custom.BadRequestException;
import com.example.Ecommerce.exception.custom.ResourceNotFoundException;
import com.example.Ecommerce.exception.custom.UnauthorizedException;
import com.example.Ecommerce.repository.UserRepository;
import com.example.Ecommerce.security.jwt.JwtUtil;
import com.example.Ecommerce.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    public void register(RegisterRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new BadRequestException("User already exists");
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();

        userRepository.save(user);
    }

    @Override
    public AuthResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Invalid credentials");
        }

        String token = jwtUtil.generateToken(user.getEmail());

        return new AuthResponse(token);
    }
}