package com.sigmaproject1.project1.controller;

import com.sigmaproject1.project1.dto.CustomerDto;
import com.sigmaproject1.project1.dto.request.CustomerCreationRequest;
import com.sigmaproject1.project1.security.CurrentUser;
import com.sigmaproject1.project1.security.UserContext;
import com.sigmaproject1.project1.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService customerService;

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/customers")
    public ResponseEntity<CustomerDto> createCustomer(@RequestBody @Valid final CustomerCreationRequest request) {
        CustomerDto customerDto = customerService.createCustomerAndGetDto(request);

        return new ResponseEntity<>(customerDto,HttpStatus.CREATED);
    }

    @GetMapping("/customers/{id}")
    public ResponseEntity<CustomerDto> getCustomer(
            @PathVariable final Long id,
            @CurrentUser final UserContext userContext
    ) {
        return ResponseEntity.ok(customerService.getCustomerDto(id, userContext));
    }

}
