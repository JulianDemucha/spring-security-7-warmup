package com.sigmaproject1.project1.controller;

import com.sigmaproject1.project1.dto.CustomerDto;
import com.sigmaproject1.project1.dto.request.CustomerCreationRequest;
import com.sigmaproject1.project1.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService customerService;

    @PostMapping("/customers")
    public ResponseEntity<CustomerDto> addCustomer(@RequestBody final CustomerCreationRequest request) {
        CustomerDto customerDto = customerService.addCustomer(request);

        return ResponseEntity
                .created(URI.create("/customers/"+customerDto.id()))
                .body(customerDto);
    }

    @GetMapping("/customers/{id}")
    public ResponseEntity<CustomerDto> getCustomer(@PathVariable final Long id) {
        return ResponseEntity.ok(customerService.getCustomer(id));
    }

}
