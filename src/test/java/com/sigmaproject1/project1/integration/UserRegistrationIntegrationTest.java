package com.sigmaproject1.project1.integration;

import com.sigmaproject1.project1.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.ResultActions;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserRegistrationIntegrationTest extends BaseIT {

    @Autowired
    private UserRepository userRepository;

    @Test
    void registerAdmin_asAdmin_returns201AndSavesUser() throws Exception {
        register("admin", user("New.Admin@Test.test"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.role").value("ADMIN"));

        assertTrue(userRepository.existsByEmail("new.admin@test.test"));
    }

    @Test
    void registerClientAndCreateCustomer_asAdmin_returns201AndSavesUser() throws Exception {
        String body = """
                {
                "email" : "client@test.test",
                "password" : "secret123",
                "firstName" : "sigma",
                "lastName" : "sigmakolo"
                }
                """;

        register("client-create-customer", body)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.role").value("CLIENT"))
                .andExpect(jsonPath("$.customerId").isNumber());

        assertTrue(userRepository.existsByEmail("client@test.test"));
    }

    @Test
    void registerClientAndLinkCustomer_existingCustomer_returns201() throws Exception {
        long customerId = createCustomer("client@test.test");

        register("client-link-customer", user("client@test.test"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.customerId").value(customerId));

        assertTrue(userRepository.existsByEmail("client@test.test"));
    }

    @Test
    void registerClientAndLinkCustomer_noCustomer_returns404() throws Exception {
        register("client-link-customer", user("client@test.test"))
                .andExpect(status().isNotFound());
    }

    @Test
    void register_duplicateEmail_returns409() throws Exception {
        register("admin", user("admin2@test.test"))
                .andExpect(status().isCreated());

        register("admin", user("admin2@test.test"))
                .andExpect(status().isConflict());
    }

    @Test
    void register_invalidBody_returns400() throws Exception {
        register("admin", """
                {
                "email" : "not-an-email",
                "password" : "123"
                }
                """)
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "CLIENT")
    void register_asClient_returns403() throws Exception {
        register("admin", user("admin2@test.test"))
                .andExpect(status().isForbidden());
    }

    private ResultActions register(String path, String body) throws Exception {
        return mockMvc.perform(post("/users/register/" + path)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body));
    }

    private String user(String email) {
        return """
                {
                "email" : "%s",
                "password" : "secret123"
                }
                """.formatted(email);
    }
}
