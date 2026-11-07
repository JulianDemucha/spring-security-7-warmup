package com.sigmaproject1.project1.dto.request;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserCreationRequest(
        @Email @NotBlank @Size(max =100)
        String email,
        @NotBlank @Size(min = 6, max = 24)
        String password
) {
}
