package com.sigmaproject1.project1.service;

import com.sigmaproject1.project1.dto.CustomerDto;
import com.sigmaproject1.project1.dto.request.CustomerCreationRequest;
import com.sigmaproject1.project1.entity.Customer;
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

        if(repo.existsByEmail(email)) {
            throw new RuntimeException("Customer already exists"); // todo: make custom exception
        }

        return CustomerDto.toDto(
                repo.save(
                Customer.builder()
                        .firstName(request.firstName())
                        .lastName(request.lastName())
                        .email(email)
                        .build()
                )
        );
    }

    public CustomerDto getCustomer(final long id) {
        return CustomerDto.toDto(
                repo.findById(id).orElseThrow(
                        RuntimeException::new // todo: make custom exception
                )
        );
    }


}
