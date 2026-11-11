package com.sigmaproject1.project1.integration;

import com.sigmaproject1.project1.config.properties.JwtProperties;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AuthIntegrationTest extends BaseIT {

    @Autowired
    private JwtProperties jwtProperties;

    @Test
    void refresh_afterLogout_returns401() throws Exception {
        createAdminUser("logout@test.test");

        Cookie refreshCookie = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email": "logout@test.test", "password": "password"}
                                """))
                .andExpect(status().isOk())
                .andReturn().getResponse()
                .getCookie(jwtProperties.refreshCookie().name());

        assertNotNull(refreshCookie);
        mockMvc.perform(post("/auth/logout").cookie(refreshCookie))
                .andExpect(status().isNoContent());

        mockMvc.perform(post("/auth/refresh").cookie(refreshCookie))
                .andExpect(status().isUnauthorized());
    }
}