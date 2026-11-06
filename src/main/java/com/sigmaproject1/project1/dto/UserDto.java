package com.sigmaproject1.project1.dto;

import com.sigmaproject1.project1.entity.User;
import com.sigmaproject1.project1.enums.Role;
import lombok.Builder;

@Builder
public record UserDto (
        Long id,
        Long customerId,
        Role role
)
{
    public static UserDto from(User user){
        return UserDto.builder()
                .id(user.getId())
                .customerId(user.getCustomer() == null ? null : user.getCustomer().getId())
                .role(user.getRole())
                .build();
    }
}
