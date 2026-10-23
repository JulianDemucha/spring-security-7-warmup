package com.sigmaproject1.project1.controller;

import com.sigmaproject1.project1.dto.CustomerDto;
import com.sigmaproject1.project1.dto.request.CustomerCreationRequest;
import com.sigmaproject1.project1.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService customerService;

    @PostMapping("/customers")
    public ResponseEntity<CustomerDto> createCustomer(@RequestBody @Valid final CustomerCreationRequest request) {
        CustomerDto customerDto = customerService.createCustomer(request);

        return new ResponseEntity<>(customerDto,HttpStatus.CREATED);
    }

    @GetMapping("/customers/{id}")
    public ResponseEntity<CustomerDto> getCustomer(@PathVariable final Long id) {
        return ResponseEntity.ok(customerService.getCustomerDto(id));
    }

}
