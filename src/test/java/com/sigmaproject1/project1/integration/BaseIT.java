package com.sigmaproject1.project1.integration;

import com.jayway.jsonpath.JsonPath;
import com.sigmaproject1.project1.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import com.sigmaproject1.project1.TestcontainersConfiguration;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
@WithJwtUser
abstract class BaseIT {
    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void cleanDatabase() {
        jdbcTemplate.execute("TRUNCATE TABLE app_user, claim, policy, customer CASCADE");
    }

    // HELPERS

    // customer -----
    long createCustomer(String firstName, String lastName, String email) throws Exception {
        String customerRequestBody = """
                {
                "firstName" : "%s",
                "lastName" : "%s",
                "email" : "%s"
                }
                """.formatted(firstName, lastName, email);

        String customerResponse =
                mockMvc.perform(post("/customers")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(customerRequestBody)
                        )
                        .andExpect(status().isCreated())
                        .andReturn().getResponse().getContentAsString();

        return ((Number) JsonPath.read(customerResponse, "$.id")).longValue();
    }

    long createCustomer(String email) throws Exception {
        return createCustomer("sigma", "sigmakolo", email);
    }

    // policy -----
    long createPolicy(String policyNumber, String startDate, String endDate, BigDecimal sumInsured, long customerId) throws Exception {
        String policyRequestBody = """
                {
                "policyNumber" : "%s",
                "startDate" : "%s",
                "endDate" : "%s",
                "sumInsured" : %s,
                "customerId" : %d
                }
                """.formatted(policyNumber, startDate, endDate, sumInsured.toPlainString(), customerId);

        String policyResponse =
                mockMvc.perform(post("/policies")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(policyRequestBody)
                        )
                        .andExpect(status().isCreated())
                        .andReturn().getResponse().getContentAsString();

        return ((Number) JsonPath.read(policyResponse, "$.id")).longValue();
    }

    long createPolicy(String policyNumber, BigDecimal sumInsured, long customerId) throws Exception {
        return createPolicy(policyNumber, "2020-09-20", "2021-10-21", sumInsured, customerId);
    }

    // claim -----
    long createClaim(long policyId, String description, String incidentDate, BigDecimal claimedAmount) throws Exception {
        String claimRequestBody = """
                {
                "policyId" : %d,
                "description" : "%s",
                "incidentDate" : "%s",
                "claimedAmount" : %s
                }
                """.formatted(policyId, description, incidentDate, claimedAmount.toPlainString());

        String claimResponse = mockMvc.perform(post("/claims")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(claimRequestBody)
                )
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        return ((Number) JsonPath.read(claimResponse, "$.id")).longValue();
    }

    long createClaim(long policyId, BigDecimal claimedAmount) throws Exception {
        return createClaim(policyId, "ok ok ok ok ok", "2021-05-20", claimedAmount);
    }

    // user -----
    long createUser(String email, String password, Role role) throws Exception {
        String userRequestBody = """
                {
                "email" : "%s",
                "password" : "%s"
                }
                """.formatted(email, password);

        String requestUrl = switch (role) {
            case Role.ADMIN -> "/admin";
            case Role.CLIENT -> "/client-link-customer";
        };

        String userResponse = mockMvc.perform(post("/users/register" + requestUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userRequestBody)
                )
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return ((Number) JsonPath.read(userResponse, "$.id")).longValue();
    }

    long createAdminUser(String email) throws Exception {
        return createUser(email, "password", Role.ADMIN);
    }

    long createClientUser(String email) throws Exception {
        return createUser(email, "password", Role.CLIENT);
    }
}
