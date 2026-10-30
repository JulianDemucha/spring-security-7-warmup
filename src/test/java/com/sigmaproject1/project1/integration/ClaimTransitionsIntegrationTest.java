package com.sigmaproject1.project1.integration;

import com.sigmaproject1.project1.entity.Claim;
import com.sigmaproject1.project1.enums.ClaimStatus;
import com.sigmaproject1.project1.repository.ClaimRepository;
import com.sigmaproject1.project1.repository.CustomerRepository;
import com.sigmaproject1.project1.repository.PolicyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ClaimTransitionsIntegrationTest extends BaseIT {

    @Autowired
    private ClaimRepository claimRepository;

    @Autowired
    private PolicyRepository policyRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @BeforeEach
    void cleanDatabase() {
        claimRepository.deleteAll();
        policyRepository.deleteAll();
        customerRepository.deleteAll();
    }

    @Nested
    class Approve {
        @Test
        void approve_valid_returns200AndStatusApproved() throws Exception {
            long customerId = createCustomer("sigma@sigma.sigma");
            long policyId = createPolicy("POL-0231-0123", BigDecimal.TEN, customerId);
            long claimId = createClaim(policyId, BigDecimal.TWO);
            mockMvc.perform(post("/claims/" + claimId + "/review"))
                    .andExpectAll(
                            status().isOk(),
                            jsonPath("$.version").value(1)
                    );

            mockMvc.perform(post("/claims/" + claimId + "/approve"))
                    .andExpectAll(
                            status().isOk(),
                            jsonPath("$.status").value(ClaimStatus.APPROVED.name()),
                            jsonPath("$.version").value(2),
                            jsonPath("$.policyId").value(policyId)
                    );

            assertEquals(ClaimStatus.APPROVED,
                    claimRepository.findById(claimId).orElseThrow().getStatus());
        }

        @Test
        void approve_policySumInsuredExceeded_returns400AndClaimStaysInReview() throws Exception {
            long customerId = createCustomer("sigma@sigma.sigma");
            long policyId = createPolicy("POL-0231-0123", BigDecimal.TEN, customerId);
            long claimId = createClaim(policyId, BigDecimal.TWO);
            long claim2Id = createClaim(policyId, BigDecimal.valueOf(9));

            mockMvc.perform(post("/claims/" + claimId + "/review"))
                    .andExpect(status().isOk());

            mockMvc.perform(post("/claims/" + claimId + "/approve"))
                    .andExpect(status().isOk());

            mockMvc.perform(post("/claims/" + claim2Id + "/review"))
                    .andExpect(status().isOk());

            mockMvc.perform(post("/claims/" + claim2Id + "/approve"))
                    .andExpect(status().isBadRequest());

            assertEquals(
                    ClaimStatus.APPROVED,
                    claimRepository.findById(claimId).orElseThrow().getStatus()
            );

            assertEquals(
                    ClaimStatus.IN_REVIEW,
                    claimRepository.findById(claim2Id).orElseThrow().getStatus()
            );
        }

        @Test
        void approve_claimNotInReview_returns409AndClaimStaysNew() throws Exception {
            long customerId = createCustomer("sigma@sigma.sigma");
            long policyId = createPolicy("POL-0231-0123", BigDecimal.TEN, customerId);
            long claimId = createClaim(policyId, BigDecimal.TWO);

            mockMvc.perform(post("/claims/" + claimId + "/approve"))
                    .andExpectAll(
                            status().isConflict(),
                            jsonPath("$.status").value(409)
                    );

            assertEquals(ClaimStatus.NEW, claimRepository.findById(claimId).orElseThrow().getStatus());
        }

    }

    @Test
    void reject_valid_returns200AndStatusRejected() throws Exception {
        long customerId = createCustomer("sigma@sigma.sigma");
        long policyId = createPolicy("POL-0231-0123", BigDecimal.TEN, customerId);
        long claimId = createClaim(policyId, BigDecimal.TWO);
        String reason = "rejected ok ok";

        mockMvc.perform(post("/claims/" + claimId + "/review")).andExpect(status().isOk());

        mockMvc.perform(post("/claims/" + claimId + "/reject").param("reason", reason))
        .andExpectAll(
                status().isOk(),
                jsonPath("$.rejectionReason").value(reason),
                jsonPath("$.status").value(ClaimStatus.REJECTED.name()),
                jsonPath("$.version").value(2)
        );

        Claim claim = claimRepository.findById(claimId).orElseThrow();

        assertEquals(reason, claim.getRejectionReason());
        assertEquals(ClaimStatus.REJECTED, claim.getStatus());
        assertEquals(2,  claim.getVersion());


        //check reason at the end

    }
}
