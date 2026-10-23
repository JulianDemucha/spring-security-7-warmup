package com.sigmaproject1.project1.exception;

public class PolicyNotFoundException extends RuntimeException {
    public PolicyNotFoundException(Long id) {
        super("Policy not found: " + id);
    }
}
