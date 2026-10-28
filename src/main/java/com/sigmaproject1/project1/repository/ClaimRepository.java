package com.sigmaproject1.project1.repository;

import com.sigmaproject1.project1.entity.Claim;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;

public interface ClaimRepository extends JpaRepository<Claim, Long> {

    @Query("""
            SELECT COALESCE(SUM(c.claimedAmount), 0)
                FROM Claim c WHERE c.policy.id = :policyId
                    AND c.status = ClaimStatus.APPROVED
    """)
    BigDecimal sumApprovedClaimAmountByPolicyId(@Param("policyId") long policyId);
}
