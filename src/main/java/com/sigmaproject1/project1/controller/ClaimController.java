package com.sigmaproject1.project1.controller;

import com.sigmaproject1.project1.dto.ClaimDto;
import com.sigmaproject1.project1.dto.request.ClaimCreationRequest;
import com.sigmaproject1.project1.service.ClaimService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ClaimController {
    private final ClaimService claimService;

    @PostMapping("/claims")
    public ResponseEntity<ClaimDto> createClaim(@Valid @RequestBody final ClaimCreationRequest claimCreationRequest) {
        return new ResponseEntity<>(
                claimService.createClaim(claimCreationRequest),
                HttpStatus.CREATED
        );
    }

    @GetMapping("/claims/{id}")
    public ResponseEntity<ClaimDto> getClaim(@PathVariable final Long id) {
        return new ResponseEntity<>(
                claimService.getClaim(id), HttpStatus.OK
        );
    }
}
