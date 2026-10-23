package com.sigmaproject1.project1.dto.request;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ClaimCreationRequest(
        @NotNull
        Long policyId,
        @NotNull @Size(max = 2000)
        String description,
        @NotNull @PastOrPresent
        LocalDate incidentDate,
        @NotNull @Positive @Digits(integer = 10, fraction = 2)
        BigDecimal claimedAmount
) {
}