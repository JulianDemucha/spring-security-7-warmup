package com.sigmaproject1.project1.exception;
import com.sigmaproject1.project1.enums.ClaimStatus;

public class InvalidClaimStatusTransitionException extends RuntimeException {
    public InvalidClaimStatusTransitionException(ClaimStatus current, ClaimStatus target) {
        super("Cannot change claim status from "+current+" to "+target);
    }
}
