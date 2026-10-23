package com.sigmaproject1.project1.exception;

public class CustomerAlreadyExistsException extends RuntimeException {
    public CustomerAlreadyExistsException(Long id) {
        super("Customer already exists: " + id);
    }

    public CustomerAlreadyExistsException(String email) {
        super("Customer already exists: " + email);
    }
}
