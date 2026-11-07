package com.sigmaproject1.project1.controller;

import com.sigmaproject1.project1.dto.request.UserAndCustomerCreationRequest;
import com.sigmaproject1.project1.dto.UserDto;
import com.sigmaproject1.project1.dto.request.UserCreationRequest;
import com.sigmaproject1.project1.service.UserRegistrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserRegistrationService userRegistrationService;


    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/register/client-link-customer")
    public ResponseEntity<UserDto> registerUserAndLinkCustomer(@RequestBody @Valid final UserCreationRequest request) {
        return new ResponseEntity<>(
                userRegistrationService.registerClientAndLinkCustomer(request),
                HttpStatus.CREATED
        );
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/register/client-create-customer")
    public ResponseEntity<UserDto> registerUserAndCreateCustomer(@RequestBody @Valid final UserAndCustomerCreationRequest request) {
        return new ResponseEntity<>(
                userRegistrationService.registerClientAndCreateCustomer(request),
                HttpStatus.CREATED
        );
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/register/admin")
    public ResponseEntity<UserDto> registerAdmin(@RequestBody @Valid final UserCreationRequest request) {
        return new ResponseEntity<>(
                userRegistrationService.registerAdmin(request),
                HttpStatus.CREATED
        );
    }


}
