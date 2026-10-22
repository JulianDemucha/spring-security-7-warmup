package com.sigmaproject1.project1.dto.request;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PolicyCreationRequest (
        @NotNull @Size(max = 30)
        String policyNumber,
        @NotNull
        LocalDate startDate,
        @NotNull
        LocalDate endDate,
        @NotNull @Positive @Digits(integer = 10, fraction = 2)
        BigDecimal sumInsured,
        @NotNull
        Long customerId
){}
