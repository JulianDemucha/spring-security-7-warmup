package com.sigmaproject1.project1.integration;

import com.sigmaproject1.project1.config.properties.JwtProperties;
import com.sigmaproject1.project1.entity.User;
import com.sigmaproject1.project1.enums.Role;
import com.sigmaproject1.project1.repository.UserRepository;
import com.sigmaproject1.project1.service.JwtService;
import com.sigmaproject1.project1.service.XsrfTokenService;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.test.context.support.WithAnonymousUser;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class JwtServiceIntegrationTest extends BaseIT {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private JwtDecoder jwtDecoder;

    @Autowired
    private JwtProperties jwtProperties;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private XsrfTokenService xsrfTokenService;

    @Test
    @WithAnonymousUser
    void issueJwtToken_validUser_tokenHasClaimsAndIsAcceptedByApi() throws Exception {
        User admin = userRepository.save(
                User.builder()
                        .email("admin@sigma.sigma")
                        .passwordHash("hash")
                        .role(Role.ADMIN)
                        .build()
        );
        UUID familyId = UUID.randomUUID();

        String token = jwtService.issueJwtToken(admin, familyId);

        Jwt jwt = jwtDecoder.decode(token);
        assertEquals(admin.getId().toString(), jwt.getSubject());
        assertEquals("admin@sigma.sigma", jwt.getClaimAsString("preferred_username"));
        assertEquals(List.of("ADMIN"), jwt.getClaimAsStringList("roles"));
        assertEquals(familyId.toString(), jwt.getClaimAsString("sid"));

        String xsrfToken = xsrfTokenService.generateSigned(familyId);

        mockMvc.perform(post("/claims/1/approve")
                        .cookie(
                                new Cookie(jwtProperties.accessCookie().name(), token),
                                new Cookie("XSRF-TOKEN", xsrfToken)
                        )
                        .header("X-XSRF-TOKEN", xsrfToken))
                .andExpect(status().isNotFound());
    }
}
