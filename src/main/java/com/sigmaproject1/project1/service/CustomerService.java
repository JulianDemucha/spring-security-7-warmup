package com.sigmaproject1.project1.service;

import com.sigmaproject1.project1.dto.CustomerDto;
import com.sigmaproject1.project1.dto.request.CustomerCreationRequest;
import com.sigmaproject1.project1.entity.Customer;
import com.sigmaproject1.project1.exception.CustomerAlreadyExistsException;
import com.sigmaproject1.project1.exception.CustomerNotFoundException;
import com.sigmaproject1.project1.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository repo;

    @Transactional
    public CustomerDto createCustomer(final CustomerCreationRequest request) {
        String email = request.email().toLowerCase(Locale.ROOT).strip();

        if(Boolean.TRUE.equals(repo.existsByEmail(email)))
            throw new CustomerAlreadyExistsException();

        return CustomerDto.from(
                repo.save(
                Customer.builder()
                        .firstName(request.firstName())
                        .lastName(request.lastName())
                        .email(email)
                        .build()
                )
        );
    }

    public CustomerDto getCustomerDto(final long id) {
        return CustomerDto.from(
                getCustomer(id)
        );
    }

    public Customer getCustomer(final long id) {
        return repo.findById(id).orElseThrow(
                () -> new CustomerNotFoundException(id)
        );
    }


}
