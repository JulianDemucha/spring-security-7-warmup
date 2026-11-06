package com.sigmaproject1.project1.config.properties;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@Validated
@ConfigurationProperties(prefix = "app.jwt")
public record JwtProperties(
        @NotBlank String secret,
        @NotBlank String issuer,
        @NotBlank String audience,
        @NotNull @Valid CookieProperties refreshCookie,
        @NotNull @Valid CookieProperties accessCookie,
        boolean cookieSecure
) {
    public record CookieProperties(
            @NotBlank String name,
            @NotBlank String path,
            @NotBlank String sameSite,
            @NotNull Duration ttl,
            boolean httpOnly
    ){}
}