package com.sigmaproject1.project1.integration;

import com.sigmaproject1.project1.enums.Role;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.List;

public class WithJwtUserSecurityContextFactory implements WithSecurityContextFactory<WithJwtUser> {
    @Override
    public @NonNull SecurityContext createSecurityContext(@NonNull WithJwtUser annotation) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(
                authentication(
                        annotation.userId(),
                        annotation.role()
                )
        );

        return context;
    }

    public static Authentication authentication(long userId, Role role) {
        Jwt jwt = Jwt.withTokenValue("test")
                .header("alg", "none")
                .subject(String.valueOf(userId))
                .claim("roles", List.of(role.name()))
                .build();

        return new TestingAuthenticationToken(
                jwt,
                null,
                "ROLE_"+role.name()
        );
    }
}
