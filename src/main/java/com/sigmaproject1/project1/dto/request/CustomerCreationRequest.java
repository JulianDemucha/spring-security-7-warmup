package com.sigmaproject1.project1.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CustomerCreationRequest(
        @NotBlank @Size(max = 100)
        String firstName,
        @NotBlank @Size(max = 100)
        String lastName,
        @Email @NotBlank @Size(max = 100)
        String email
) {
    public static CustomerCreationRequest from(final UserAndCustomerCreationRequest request) {
        return new CustomerCreationRequest(request.firstName(), request.lastName(), request.email());
    }
}