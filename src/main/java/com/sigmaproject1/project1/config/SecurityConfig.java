package com.sigmaproject1.project1.config;

import com.sigmaproject1.project1.filter.XsrfValidationFilter;
import com.sigmaproject1.project1.service.XsrfTokenService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(
            HttpSecurity http, JwtAuthenticationConverter jwtAuthenticationConverter,
            JwtDecoder jwtDecoder, BearerTokenResolver tokenResolver,
            XsrfTokenService xsrfTokenService
    ) {
        http.
                csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(
                        s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/auth/login", "/auth/refresh", "/auth/logout",
                                "/swagger-ui/**", "/v3/api-docs/**"
                        ).permitAll()
                .anyRequest().authenticated())

                .oauth2ResourceServer(oauth -> oauth
                        .jwt(jwt -> {
                            jwt.decoder(jwtDecoder);
                            jwt.jwtAuthenticationConverter(jwtAuthenticationConverter);
                        })
                        .bearerTokenResolver(tokenResolver)
                )

                .addFilterAfter(
                        new XsrfValidationFilter(xsrfTokenService),
                        BearerTokenAuthenticationFilter.class
                );

        return http.build();
    }
}
