package com.example.crudzaso.dto;

import com.example.crudzaso.entity.enums.MotorDB;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DatabaseCreateRequest(
    @NotBlank String name,
    @NotBlank String description,
    @NotNull MotorDB engine,
    @NotNull Integer port
) {}
