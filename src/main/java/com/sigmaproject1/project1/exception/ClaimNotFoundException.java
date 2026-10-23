package com.sigmaproject1.project1.exception;

public class ClaimNotFoundException extends RuntimeException {
    public ClaimNotFoundException(Long id) {
        super("Claim not found: " + id);
    }
}
