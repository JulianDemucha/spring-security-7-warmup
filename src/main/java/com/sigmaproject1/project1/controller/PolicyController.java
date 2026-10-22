package com.sigmaproject1.project1.controller;

import com.sigmaproject1.project1.dto.PolicyDto;
import com.sigmaproject1.project1.dto.request.PolicyCreationRequest;
import com.sigmaproject1.project1.service.PolicyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class PolicyController {
    private final PolicyService policyService;

    @PostMapping("/policies")
    public ResponseEntity<PolicyDto> createPolicy(@RequestBody final PolicyCreationRequest request) {
        return new ResponseEntity<>(policyService.createPolicy(request), HttpStatus.CREATED);
    }

    @GetMapping("/policies/{id}")
    public ResponseEntity<PolicyDto> getPolicy(@PathVariable final Long id) {
        return new ResponseEntity<>(policyService.getPolicy(id), HttpStatus.OK);
    }

}
