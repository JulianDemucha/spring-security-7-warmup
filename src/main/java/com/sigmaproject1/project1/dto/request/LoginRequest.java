package com.sigmaproject1.project1.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
    @Email @NotBlank
    String email,
    @NotBlank
    String password
){}
