package com.sigmaproject1.project1.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.NonNull;

public record CustomerCreationRequest(
        @NotBlank @NonNull @Size(max =100)
        String firstName,
        @NotBlank @NonNull @Size(max =100)
        String lastName,
        @Email @NotBlank @Size(max =100)
        String email
){}