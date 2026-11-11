package com.sigmaproject1.project1.service;

import com.sigmaproject1.project1.dto.PolicyDto;
import com.sigmaproject1.project1.dto.request.PolicyCreationRequest;
import com.sigmaproject1.project1.entity.Customer;
import com.sigmaproject1.project1.entity.Policy;
import com.sigmaproject1.project1.exception.PolicyAlreadyExistsException;
import com.sigmaproject1.project1.exception.PolicyNotFoundException;
import com.sigmaproject1.project1.repository.PolicyRepository;
import com.sigmaproject1.project1.security.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PolicyService {
    private final PolicyRepository policyRepository;
    private final CustomerService customerService;

    @Transactional
    public PolicyDto createPolicy(final PolicyCreationRequest request) {
        if(!request.endDate().isAfter(request.startDate()))
            throw new IllegalArgumentException("Start date cannot be after end date");

        if(policyRepository.existsByPolicyNumber(request.policyNumber()))
            throw new PolicyAlreadyExistsException(request.policyNumber());

        Customer customer = customerService.getById(request.customerId());

        return PolicyDto.from(policyRepository.save(
                Policy.builder()
                        .policyNumber(request.policyNumber())
                        .startDate(request.startDate())
                        .endDate(request.endDate())
                        .sumInsured(request.sumInsured())
                        .customer(customer)
                        .build()
        ));
    }

    public PolicyDto getPolicyDto(final long policyId, final UserContext userContext) {
        if (userContext.isAdmin())
            return PolicyDto.from(
                    getPolicy(policyId)
            );

        return PolicyDto.from(
                getPolicyByIdAndOwner(policyId, userContext.userId())
        );
    }

    public Policy getPolicy(final long policyId) {
        return policyRepository.findById(policyId).orElseThrow(
                () -> new PolicyNotFoundException(policyId)
        );
    }

    //With lock
    public Policy getPolicyForUpdate(final long policyId) {
        return policyRepository.findByIdForUpdate(policyId).orElseThrow(
                () -> new PolicyNotFoundException(policyId)
        );
    }

    // throws exception if policy doesn't belong to user or policy doesn't exist
    public Policy getPolicyByIdAndOwner(final long policyId, final long userId) {
        return policyRepository.findByIdAndOwnerUserId(policyId, userId).orElseThrow(
                () -> new PolicyNotFoundException(policyId)
        );
    }

    public Boolean existsById(final long policyId) {
        return policyRepository.existsById(policyId);
    }

}
