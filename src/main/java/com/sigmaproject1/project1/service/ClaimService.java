package com.sigmaproject1.project1.service;

import com.sigmaproject1.project1.dto.ClaimDto;
import com.sigmaproject1.project1.dto.request.ClaimCreationRequest;
import com.sigmaproject1.project1.entity.Claim;
import com.sigmaproject1.project1.entity.Policy;
import com.sigmaproject1.project1.repository.ClaimRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class ClaimService {
    private final ClaimRepository claimRepository;
    private final PolicyService policyService;
    private final Clock clock;

    @Transactional
    public ClaimDto createClaim(final ClaimCreationRequest request) {

        Policy policy = policyService.getPolicy(request.policyId());

        return ClaimDto.from(claimRepository.save(
                Claim.builder()
                        .policy(policy)
                        .claimedAmount(request.claimedAmount())
                        .description(request.description())
                        .incidentDate(request.incidentDate())
                        .createdAt(Instant.now(clock))
                        .build()
        ));
    }

    public ClaimDto getClaim(final long claimId) {
        return ClaimDto.from(
                claimRepository.findById(claimId).orElseThrow(
                        () -> new IllegalArgumentException("Claim doesn't exist id: " + claimId)
                ) // todo make custom exception
        );
    }



}
