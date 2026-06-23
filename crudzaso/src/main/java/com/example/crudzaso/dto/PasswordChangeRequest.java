package com.example.crudzaso.dto;

import jakarta.validation.constraints.NotBlank;

public record PasswordChangeRequest(
    @NotBlank String oldPassword,
    @NotBlank String newPassword
) {}
