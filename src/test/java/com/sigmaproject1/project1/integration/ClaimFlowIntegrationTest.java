package com.sigmaproject1.project1.integration;

import com.jayway.jsonpath.JsonPath;
import com.sigmaproject1.project1.repository.ClaimRepository;
import com.sigmaproject1.project1.repository.CustomerRepository;
import com.sigmaproject1.project1.repository.PolicyRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ClaimFlowIntegrationTest extends BaseIT {
    @Autowired
    private ClaimRepository claimRepository;

    @Autowired
    private PolicyRepository policyRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    void claimFlowIntegrationTest() throws Exception {
        // Customer
        String customerRequestBody = """
                {
                "firstName" : "sigma",
                "lastName" : "sigmakolo",
                "email" : "sigma@sigma.sigma"
                }
                """;

        String customerResponse =
                mockMvc.perform(post("/customers")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(customerRequestBody)
                        )
                        .andExpectAll(
                                status().isCreated(),
                                jsonPath("$.firstName").value("sigma"),
                                jsonPath("$.lastName").value("sigmakolo"),
                                jsonPath("$.email").value("sigma@sigma.sigma")
                        ).andReturn().getResponse().getContentAsString();

        long customerId = ((Number) JsonPath.read(customerResponse, "$.id")).longValue();

        // Policy
        String policyRequestBody = """
                {
                "policyNumber" : "123-1234-1234",
                "startDate" : "2020-09-20",
                "endDate" : "2021-10-21",
                "sumInsured" : 1000000,
                "customerId" : %d
                }
                """.formatted(customerId);

        String policyResponse =
                mockMvc.perform(post("/policies")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(policyRequestBody)
                        )
                        .andExpectAll(
                                status().isCreated(),
                                jsonPath("$.policyNumber").value("123-1234-1234"),
                                jsonPath("$.startDate").value("2020-09-20"),
                                jsonPath("$.endDate").value("2021-10-21"),
                                jsonPath("$.sumInsured").value(1000000),
                                jsonPath("$.customerId").value(customerId)
                        ).andReturn().getResponse().getContentAsString();

        long policyId = ((Number)JsonPath.read(policyResponse, "$.id")).longValue();

        // Claim
        String claimRequestBody = """
                {
                "policyId" : %d,
                "description" : "ok ok ok ok ok",
                "incidentDate" : "2021-05-20",
                "claimedAmount" : 200000
                }
                """.formatted(policyId);

        mockMvc.perform(post("/claims")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(claimRequestBody)
                )
                .andExpectAll(
                        status().isCreated(),
                        jsonPath("$.policyId").value(policyId),
                        jsonPath("$.description").value("ok ok ok ok ok"),
                        jsonPath("$.incidentDate").value("2021-05-20"),
                        jsonPath("$.claimedAmount").value(200000),
                        jsonPath("$.status").value("NEW")
                );

        // Assert
        assertEquals(1, customerRepository.count());
        assertEquals(1, policyRepository.count());
        assertEquals(1, claimRepository.count());
    }
}
