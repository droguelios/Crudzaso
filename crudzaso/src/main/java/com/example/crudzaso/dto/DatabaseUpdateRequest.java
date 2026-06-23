package com.example.crudzaso.dto;

import jakarta.validation.constraints.NotBlank;

public record DatabaseUpdateRequest(
    @NotBlank String name,
    @NotBlank String description
) {}
