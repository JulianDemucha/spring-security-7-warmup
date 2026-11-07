package com.sigmaproject1.project1.exception;

public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException() {
        super("User with the provided email already exists");
    }
}
