package com.sigmaproject1.project1.dto;
import com.sigmaproject1.project1.entity.Policy;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
public record PolicyDto(
        Long id,
        String policyNumber,
        LocalDate startDate,
        LocalDate endDate,
        BigDecimal sumInsured,
        Long customerId
){
    public static PolicyDto from(Policy policy) {

        return PolicyDto.builder()
                .id(policy.getId())
                .customerId(policy.getCustomer().getId())
                .policyNumber(policy.getPolicyNumber())
                .startDate(policy.getStartDate())
                .endDate(policy.getEndDate())
                .sumInsured(policy.getSumInsured())
                .build();
    }
}
