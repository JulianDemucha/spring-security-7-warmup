package com.sigmaproject1.project1.integration;

import com.sigmaproject1.project1.config.properties.JwtProperties;
import com.sigmaproject1.project1.enums.Role;
import com.sigmaproject1.project1.service.XsrfTokenService;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class SecurityRulesIntegrationTest extends BaseIT {

    private static final UUID FAMILY_ID = UUID.randomUUID();

    @Autowired
    private XsrfTokenService xsrfTokenService;

    @Autowired
    private JwtEncoder jwtEncoder;

    @Autowired
    private JwtProperties jwtProperties;

    @Test
    @WithAnonymousUser
    void approve_notLoggedIn_returns401() throws Exception {
        mockMvc.perform(post("/claims/1/approve"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "CLIENT")
    void approve_asClient_returns403() throws Exception {
        mockMvc.perform(post("/claims/1/approve"))
                .andExpect(status().isForbidden());
    }

    @Test
    void approve_asAdmin_passesSecurityAndReturns404() throws Exception {
        mockMvc.perform(post("/claims/1/approve"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "CLIENT")
    void getPolicy_asClient_returns403() throws Exception {
        mockMvc.perform(get("/policies/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "CLIENT")
    void getCustomer_asClient_returns403() throws Exception {
        mockMvc.perform(get("/customers/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithAnonymousUser
    void approve_clientToken_returns403() throws Exception {
        mockMvc.perform(approveWithXsrf(Role.CLIENT, xsrfTokenService.generateSigned(FAMILY_ID)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithAnonymousUser
    void approve_adminToken_passesSecurityAndReturns404() throws Exception {
        mockMvc.perform(approveWithXsrf(Role.ADMIN, xsrfTokenService.generateSigned(FAMILY_ID)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithAnonymousUser
    void approve_adminTokenWithoutXsrfHeader_returns403() throws Exception {
        Cookie xsrfCookie = new Cookie("XSRF-TOKEN", xsrfTokenService.generateSigned(FAMILY_ID));

        mockMvc.perform(post("/claims/1/approve").cookie(accessCookie(token(Role.ADMIN)), xsrfCookie))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithAnonymousUser
    void approve_adminTokenWithXsrfFromOtherFamily_returns403() throws Exception {
        mockMvc.perform(approveWithXsrf(Role.ADMIN, xsrfTokenService.generateSigned(UUID.randomUUID())))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithAnonymousUser
    void getPolicy_invalidToken_returns401() throws Exception {
        mockMvc.perform(get("/policies/1").cookie(accessCookie("invalid")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithAnonymousUser
    void authPath_invalidToken_isNotRejectedByTokenFilter() throws Exception {
        int responseStatus = mockMvc.perform(post("/auth/login").cookie(accessCookie("invalid")))
                .andReturn().getResponse().getStatus();

        assertNotEquals(401, responseStatus);
    }

    @Test
    @WithAnonymousUser
    void apiDocs_notLoggedIn_returns200() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk());
    }

    private MockHttpServletRequestBuilder approveWithXsrf(Role role, String xsrfToken) {
        return post("/claims/1/approve")
                .cookie(accessCookie(token(role)), new Cookie("XSRF-TOKEN", xsrfToken))
                .header("X-XSRF-TOKEN", xsrfToken);
    }

    private Cookie accessCookie(String token) {
        return new Cookie(jwtProperties.accessCookie().name(), token);
    }

    private String token(Role role) {
        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(jwtProperties.issuer())
                .audience(List.of(jwtProperties.audience()))
                .issuedAt(now)
                .expiresAt(now.plus(jwtProperties.accessCookie().ttl()))
                .subject("1")
                .claim("roles", List.of(role))
                .claim("sid", FAMILY_ID.toString())
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }
}
