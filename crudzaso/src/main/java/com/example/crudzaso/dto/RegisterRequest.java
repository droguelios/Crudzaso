package com.example.crudzaso.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegisterRequest(
    @NotBlank String name,
    @NotBlank String lastName,
    @Email @NotBlank String email,
    @NotBlank String password
) {}
