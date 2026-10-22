package com.sigmaproject1.project1.dto;

import com.sigmaproject1.project1.entity.Customer;
import lombok.Builder;

@Builder
public record CustomerDto(
        Long id,
        String firstName,
        String lastName,
        String email
){
    public static CustomerDto toDto(Customer customer) {
        return CustomerDto.builder()
                .id(customer.getId())
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .email(customer.getEmail())
                .build();
    }
}
