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
import java.time.LocalDate;

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
            throw new RuntimeException("Incident date falls outside the time period specified in the policy"); //todo: make custom exception


        if(request.claimedAmount().compareTo(policy.getSumInsured()) > 0)
            throw new RuntimeException("Claimed amount exceeds the sum of insured amount"); //todo: make custom exception

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

    public ClaimDto getClaim(final long claimId) {
        return ClaimDto.from(
                claimRepository.findById(claimId).orElseThrow(
                        () -> new IllegalArgumentException("Claim doesn't exist id: " + claimId)
                ) // todo make custom exception
        );
    }



}
