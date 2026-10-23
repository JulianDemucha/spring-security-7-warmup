package com.sigmaproject1.project1.dto;
import com.sigmaproject1.project1.entity.Claim;
import com.sigmaproject1.project1.enums.ClaimStatus;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Builder
public record ClaimDto(
        Long id,
        Long policyId,
        String description,
        LocalDate incidentDate,
        BigDecimal claimedAmount,
        ClaimStatus status,
        Instant createdAt,
        Long version
){

    public static ClaimDto from(final Claim claim) {

        return ClaimDto.builder()
                .id(claim.getId())
                .policyId(claim.getPolicy().getId())
                .description(claim.getDescription())
                .incidentDate(claim.getIncidentDate())
                .claimedAmount(claim.getClaimedAmount())
                .status(claim.getStatus())
                .createdAt(claim.getCreatedAt())
                .version(claim.getVersion())
                .build();
    }
}
