package com.sigmaproject1.project1.exception;

public class PolicyAlreadyExistsException extends RuntimeException {
    public PolicyAlreadyExistsException(String policyNumber) {
        super("Policy with number "+policyNumber+" already exists");
    }
}
