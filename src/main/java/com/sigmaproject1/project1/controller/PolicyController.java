package com.sigmaproject1.project1.controller;

import com.sigmaproject1.project1.dto.ClaimDto;
import com.sigmaproject1.project1.dto.PolicyDto;
import com.sigmaproject1.project1.dto.request.PolicyCreationRequest;
import com.sigmaproject1.project1.service.ClaimService;
import com.sigmaproject1.project1.service.PolicyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/policies")
@RequiredArgsConstructor
public class PolicyController {
    private final PolicyService policyService;
    private final ClaimService claimService;


    // todo: check if user is owner or admin
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping()
    public ResponseEntity<PolicyDto> createPolicy(@RequestBody @Valid final PolicyCreationRequest request) {
        return new ResponseEntity<>(policyService.createPolicy(request), HttpStatus.CREATED);
    }


    // todo: check if user is owner or admin
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<PolicyDto> getPolicy(@PathVariable final Long id) {
        return new ResponseEntity<>(policyService.getPolicyDto(id), HttpStatus.OK);
    }


    // todo: check if user is owner or admin
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/{id}/claims")
    public ResponseEntity<List<ClaimDto>> getClaimsByPolicyId(@PathVariable final Long id) {
        return new ResponseEntity<>(claimService.getClaimsByPolicyId(id), HttpStatus.OK);
    }

}
