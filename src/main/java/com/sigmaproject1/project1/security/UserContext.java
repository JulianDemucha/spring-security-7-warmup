package com.sigmaproject1.project1.security;

import com.sigmaproject1.project1.enums.Role;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;
import java.util.Objects;

public record UserContext(
        Long userId,
        List<Role> roles
) {
    public static UserContext from(Jwt jwt) {
        List<String> roles = jwt.getClaim("roles");

        if (roles == null || roles.isEmpty()) {
            throw new IllegalArgumentException("Invalid JWT claims");
        }

        return new UserContext(
                Long.parseLong(Objects.requireNonNull(jwt.getSubject())),
                roles.stream().map(Role::valueOf).toList()
        );
    }

    public boolean isAdmin(){
        return this.roles.contains(Role.ADMIN);
    }
}
