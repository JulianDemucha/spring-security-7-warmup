package com.sigmaproject1.project1.repository;

import com.sigmaproject1.project1.dto.ClaimDto;
import com.sigmaproject1.project1.entity.Claim;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface ClaimRepository extends JpaRepository<Claim, Long> {

    @Query("""
            SELECT COALESCE(SUM(c.claimedAmount), 0)
                FROM Claim c WHERE c.policy.id = :policyId
                    AND c.status = ClaimStatus.APPROVED
    """)
    BigDecimal sumApprovedClaimAmountByPolicyId(@Param("policyId") long policyId);

    @Query("""
           SELECT new com.sigmaproject1.project1.dto.ClaimDto(
                      c.id, c.policy.id, c.description, c.incidentDate, c.claimedAmount,
                      c.status, c.createdAt, c.rejectionReason, c.version
           )
           FROM Claim c
           WHERE c.policy.id =:policyId
           """)
    List<ClaimDto> findAllAsDtoByPolicyId(@Param("policyId") long policyId);
}
