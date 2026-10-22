package com.sigmaproject1.project1.dto;

import lombok.NonNull;

public record CustomerDto(
        Long id,
        String firstName,
        String lastName,
        String email
){}
