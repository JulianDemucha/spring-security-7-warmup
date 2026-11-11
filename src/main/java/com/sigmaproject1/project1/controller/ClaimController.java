package com.sigmaproject1.project1.controller;

import com.sigmaproject1.project1.dto.ClaimDto;
import com.sigmaproject1.project1.security.CurrentUser;
import com.sigmaproject1.project1.security.UserContext;
import com.sigmaproject1.project1.dto.request.ClaimCreationRequest;
import com.sigmaproject1.project1.service.ClaimService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ClaimController {
    private final ClaimService claimService;

    @PostMapping("/claims")
    public ResponseEntity<ClaimDto> createClaim(
            @Valid @RequestBody final ClaimCreationRequest claimCreationRequest,
            @CurrentUser final UserContext userContext
    ) {
        return new ResponseEntity<>(
                claimService.createClaim(claimCreationRequest, userContext),
                HttpStatus.CREATED
        );
    }

    @GetMapping("/claims/{id}")
    public ResponseEntity<ClaimDto> getClaim(
            @PathVariable final long id,
            @CurrentUser final UserContext userContext
    ) {
        return new ResponseEntity<>(
                claimService.getClaimDto(id, userContext), HttpStatus.OK
        );
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/claims/{id}/review")
    public ResponseEntity<ClaimDto> moveToReview(@PathVariable final long id) {
        return new ResponseEntity<>(
                claimService.moveToReview(id), HttpStatus.OK
        );
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/claims/{id}/reject")
    public ResponseEntity<ClaimDto> rejectClaim(@PathVariable final long id, @RequestParam String reason) {
        return new ResponseEntity<>(
                claimService.rejectClaim(id, reason), HttpStatus.OK
        );
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/claims/{id}/approve")
    public ResponseEntity<ClaimDto> approveClaim(@PathVariable final long id) {
        return new ResponseEntity<>(
                claimService.approveClaim(id), HttpStatus.OK
        );
    }
}
