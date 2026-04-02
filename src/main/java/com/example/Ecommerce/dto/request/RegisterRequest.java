package com.example.Ecommerce.dto.request;

import lombok.Data;

import jakarta.validation.constraints.*;

@Data
public class RegisterRequest {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 6)
    private String password;
}