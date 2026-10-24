package com.sigmaproject1.project1.exception;

public class CustomerAlreadyExistsException extends RuntimeException {
    public CustomerAlreadyExistsException(Long id) {
        super("Customer already exists: " + id);
    }

    public CustomerAlreadyExistsException() {
        super("Customer with the provided email already exists");
    }
}
