package com.sigmaproject1.project1.service;

import com.sigmaproject1.project1.dto.ClaimDto;
import com.sigmaproject1.project1.dto.request.ClaimCreationRequest;
import com.sigmaproject1.project1.entity.Claim;
import com.sigmaproject1.project1.entity.Policy;
import com.sigmaproject1.project1.enums.ClaimStatus;
import com.sigmaproject1.project1.exception.*;
import com.sigmaproject1.project1.repository.ClaimRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ClaimService {
    private final ClaimRepository claimRepository;
    private final PolicyService policyService;
    private final Clock clock;

    @Transactional
    public ClaimDto createClaim(final ClaimCreationRequest request) {

        Policy policy = policyService.getPolicy(request.policyId());

        LocalDate incidentDate = request.incidentDate();

        if(incidentDate.isAfter(policy.getEndDate()) || incidentDate.isBefore(policy.getStartDate()))
            throw new IncidentOutsidePolicyPeriodException(incidentDate, policy.getStartDate(), policy.getEndDate());


        if(request.claimedAmount().compareTo(policy.getSumInsured()) > 0)
            throw new ClaimExceedsSumInsuredException(request.claimedAmount(), policy.getSumInsured(), policy.getId());

        return ClaimDto.from(claimRepository.save(
                Claim.builder()
                        .policy(policy)
                        .claimedAmount(request.claimedAmount())
                        .description(request.description().strip())
                        .incidentDate(request.incidentDate())
                        .createdAt(Instant.now(clock))
                        .build()
        ));
    }

    public ClaimDto getClaimDto(final long claimId) {
        return ClaimDto.from(getClaim(claimId));
    }

    public Claim getClaim(final long claimId) {
        return claimRepository.findById(claimId).orElseThrow(
                        () -> new ClaimNotFoundException(claimId)
                );
    }

    @Transactional
    public ClaimDto moveToReview(final long claimId) {
        Claim claim = getClaim(claimId);
        claim.moveToReview();
        claimRepository.flush();
        return ClaimDto.from(claim);
    }

    @Transactional
    public ClaimDto rejectClaim(final long claimId, final String reason) {
        Claim claim = getClaim(claimId);
        claim.rejectClaim(reason);
        claimRepository.flush();
        return ClaimDto.from(claim);
    }

    @Transactional
    public ClaimDto approveClaim(final long claimId) {
        Claim claim = getClaim(claimId);
        if(!claim.getStatus().canTransitionTo(ClaimStatus.APPROVED))
            throw new InvalidClaimStatusTransitionException(claim.getStatus(),  ClaimStatus.APPROVED);

        Policy policy = policyService.getPolicyForUpdate(claim.getPolicy().getId()); //Lock

        BigDecimal alreadyApproved = claimRepository.sumApprovedClaimAmountByPolicyId(policy.getId());
        if(alreadyApproved.add(claim.getClaimedAmount()).compareTo(policy.getSumInsured()) > 0)
            throw new PolicySumInsuredExceededException(
                    claim.getClaimedAmount(),
                    policy.getSumInsured(),
                    alreadyApproved,
                    policy.getId()
            );

        claim.approveClaim();
        claimRepository.flush();
        return ClaimDto.from(claim);
    }

    public List<ClaimDto> getClaimsByPolicyId(final long policyId) {
        if(!policyService.existsById(policyId))
            throw new PolicyNotFoundException(policyId);
        return claimRepository.findAllAsDtoByPolicyId(policyId);
    }

}
