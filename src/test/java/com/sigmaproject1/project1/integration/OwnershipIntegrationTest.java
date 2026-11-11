package com.sigmaproject1.project1.integration;

import com.sigmaproject1.project1.enums.Role;
import org.springframework.http.MediaType;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.math.BigDecimal;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class OwnershipIntegrationTest extends BaseIT {

    @Test
    void getClaim_asOwner_returns200() throws Exception {
        long customerId = createCustomer("sigma@test.test");
        long userId = createClientUser("sigma@test.test");
        long policyId = createPolicy("POL-1", BigDecimal.TEN, customerId);
        long claimId = createClaim(policyId, BigDecimal.ONE);

        mockMvc.perform(get("/claims/" + claimId)
                .with(asClient(userId))).andExpect(status().isOk());
    }

    @Test
    void getClaim_asOtherClient_returns404() throws Exception {
        long customerId = createCustomer("sigma@test.test");
        createClientUser("sigma@test.test");
        createCustomer("non-sigma@test.test");
        long otherUserId = createClientUser("non-sigma@test.test");
        long policyId = createPolicy("POL-1", BigDecimal.TEN, customerId);
        long claimId = createClaim(policyId, BigDecimal.ONE);

        mockMvc.perform(get("/claims/" + claimId)
                .with(asClient(otherUserId))).andExpect(status().isNotFound());
    }

    @Test
    void createClaim_onOtherClientsPolicy_returns404() throws Exception {
        long customerId = createCustomer("sigma@test.test");
        createClientUser("sigma@test.test");
        createCustomer("non-sigma@test.test");
        long otherUserId = createClientUser("non-sigma@test.test");
        long policyId = createPolicy("POL-1", BigDecimal.TEN, customerId);

        mockMvc.perform(post("/claims")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                "policyId" : %d,
                                "description" : "ok ok",
                                "incidentDate" : "%s",
                                "claimedAmount" : %s
                                }
                                """.formatted(policyId, "2021-05-20", BigDecimal.ONE))
                        .with(asClient(otherUserId)))
                .andExpect(status().isNotFound());
    }

    private RequestPostProcessor asClient(long userId) {
        return authentication(WithJwtUserSecurityContextFactory.authentication(userId, Role.CLIENT));
    }
}