package com.example.crudzaso.dto;

import jakarta.validation.constraints.NotBlank;

public record ProfileUpdateRequest(
    @NotBlank String name,
    @NotBlank String lastName
) {}
