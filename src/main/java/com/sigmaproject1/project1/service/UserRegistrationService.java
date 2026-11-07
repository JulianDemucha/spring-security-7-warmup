package com.sigmaproject1.project1.service;

import com.sigmaproject1.project1.dto.request.CustomerCreationRequest;
import com.sigmaproject1.project1.dto.request.UserAndCustomerCreationRequest;
import com.sigmaproject1.project1.dto.UserDto;
import com.sigmaproject1.project1.dto.request.UserCreationRequest;
import com.sigmaproject1.project1.entity.Customer;
import com.sigmaproject1.project1.entity.User;
import com.sigmaproject1.project1.enums.Role;
import com.sigmaproject1.project1.exception.UserAlreadyExistsException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.sigmaproject1.project1.utils.EmailUtils.normalizeEmail;

@Service
@RequiredArgsConstructor
public class UserRegistrationService {
    private final UserService userService;
    private final CustomerService customerService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserDto registerClientAndLinkCustomer(final UserCreationRequest request) {
        String email = normalizeEmail(request.email());

        if(userService.existsByEmail(email))
            throw new UserAlreadyExistsException();

        Customer customer = customerService.getByEmail(email);

        return UserDto.from(
                saveUser(email, request.password(), Role.CLIENT, customer)
        );
    }

    @Transactional
    public UserDto registerClientAndCreateCustomer(final UserAndCustomerCreationRequest request) {
        String email = normalizeEmail(request.email());
        if(userService.existsByEmail(email))
            throw new UserAlreadyExistsException();

        Customer customer = customerService.createCustomer(CustomerCreationRequest.from(request));

        return UserDto.from(
                saveUser(email, request.password(), Role.CLIENT, customer)
        );
    }

    @Transactional
    public UserDto registerAdmin(final UserCreationRequest request) {
        String email = normalizeEmail(request.email());
        if(userService.existsByEmail(email))
            throw new UserAlreadyExistsException();

        return UserDto.from(
                saveUser(email, request.password(), Role.ADMIN, null)
        );
    }

    private User saveUser(String email, String password, Role role, Customer customer) {
        return userService.save(User.builder()
                .email(email)
                .passwordHash(passwordEncoder.encode(password))
                .role(role)
                .customer(customer)
                .build()
        );
    }


}
