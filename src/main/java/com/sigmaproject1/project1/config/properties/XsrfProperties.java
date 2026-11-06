package com.sigmaproject1.project1.config.properties;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app.xsrf")
public record XsrfProperties(
        @NotBlank String secret
) {}
