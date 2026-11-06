package com.sigmaproject1.project1.service;

import com.sigmaproject1.project1.dto.request.CustomerCreationRequest;
import com.sigmaproject1.project1.entity.Customer;
import com.sigmaproject1.project1.exception.CustomerAlreadyExistsException;
import com.sigmaproject1.project1.exception.CustomerNotFoundException;
import com.sigmaproject1.project1.repository.CustomerRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {
    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    @Captor
    private ArgumentCaptor<Customer> customerCaptor;

    @Nested
    class CreateCustomerTests{

        @Test
        void createCustomer_customerAlreadyExists_throwsException(){
            //Arrange
            String email = "test@test.com";
            CustomerCreationRequest request = new CustomerCreationRequest(
                    "sigma",
                    "czekolada",
                    email
            );

            when(customerRepository.existsByEmail(email)).thenReturn(true);

            // Act and Assert
            assertThrows(
                    CustomerAlreadyExistsException.class,
                    () -> customerService.createCustomer(request)
            );
            verify(customerRepository, never()).save(any());
        }

        @Test
        void createCustomer_validData_savesAndReturnsCustomer(){
            // Arrange
            CustomerCreationRequest request = new CustomerCreationRequest(
                    "sigma",
                    "czekolada",
                    "test@test.com"
            );
            when(customerRepository.existsByEmail(any())).thenReturn(false);
            when(customerRepository.save(customerCaptor.capture())).thenReturn(
                    Customer.builder()
                            .id(1L)
                            .firstName(request.firstName())
                            .lastName(request.lastName())
                            .email(request.email())
                            .build()
            );

            //Act
            customerService.createCustomer(request);

            //Assert
            Customer saved = customerCaptor.getValue();

            assertEquals("sigma", saved.getFirstName());
            assertEquals("czekolada", saved.getLastName());
            assertEquals("test@test.com", saved.getEmail());
        }
    }

    @Test
    void getCustomer_customerDoesntExist_throwsException(){
        //Arrange
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        //Act and Assert
        assertThrows(
                CustomerNotFoundException.class,
                () -> customerService.getById(1L)
        );
    }

}
