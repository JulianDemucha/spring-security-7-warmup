package com.sigmaproject1.project1.service;

import com.sigmaproject1.project1.dto.CustomerDto;
import com.sigmaproject1.project1.dto.request.CustomerCreationRequest;
import com.sigmaproject1.project1.entity.Customer;
import com.sigmaproject1.project1.exception.CustomerAlreadyExistsException;
import com.sigmaproject1.project1.exception.CustomerNotFoundException;
import com.sigmaproject1.project1.repository.CustomerRepository;
import com.sigmaproject1.project1.security.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.sigmaproject1.project1.utils.EmailUtils.normalizeEmail;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository repo;

    @Transactional
    public CustomerDto createCustomerAndGetDto(final CustomerCreationRequest request) {
        return CustomerDto.from(
                createAndSave(request)
        );
    }

    @Transactional
    public Customer createCustomer(final CustomerCreationRequest request) {
        return createAndSave(request);
    }

    private Customer createAndSave(final CustomerCreationRequest request) {
        String email = normalizeEmail(request.email());

        if (Boolean.TRUE.equals(repo.existsByEmail(email)))
            throw new CustomerAlreadyExistsException();

        return repo.save(
                Customer.builder()
                        .firstName(request.firstName())
                        .lastName(request.lastName())
                        .email(email)
                        .build()
        );
    }

    public CustomerDto getCustomerDto(final long customerId, final UserContext userContext) {
        if (userContext.isAdmin())
            return CustomerDto.from(
                    getById(customerId)
            );

        return CustomerDto.from(
                getByIdAndUserId(customerId, userContext.userId())
        );
    }

    public Customer getById(final long id) {
        return repo.findById(id).orElseThrow(
                () -> new CustomerNotFoundException(id)
        );
    }

    public Customer getByEmail(final String email) {
        return repo.findByEmail(email).orElseThrow(
                CustomerNotFoundException::new
        );
    }

    public Customer getByIdAndUserId(final long customerId, final long userId) {
        return repo.findByIdAndUserId(customerId, userId).orElseThrow(
                () -> new CustomerNotFoundException(customerId)
        );
    }


}
