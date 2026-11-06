package com.sigmaproject1.project1.service;

import com.sigmaproject1.project1.dto.request.PolicyCreationRequest;
import com.sigmaproject1.project1.entity.Customer;
import com.sigmaproject1.project1.entity.Policy;
import com.sigmaproject1.project1.exception.PolicyAlreadyExistsException;
import com.sigmaproject1.project1.exception.PolicyNotFoundException;
import com.sigmaproject1.project1.repository.PolicyRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PolicyServiceTest {

    @Mock
    private PolicyRepository policyRepository;

    @Mock
    private CustomerService customerService;

    @InjectMocks
    private PolicyService policyService;

    private final ArgumentCaptor<Policy> policyCaptor = ArgumentCaptor.forClass(Policy.class);


    @Nested
    class CreatePolicyTests{

        @Test
        void createPolicy_startDateIsAfterEndDate_throwsException(){
            //Arrange
            LocalDate startDate = LocalDate.of(2026,1,2);
            LocalDate endDate = startDate.minusDays(1);

            PolicyCreationRequest request = new PolicyCreationRequest(
                    "123-4567-8910",
                    startDate,
                    endDate,
                    new BigDecimal("1.00"),
                    1L
            );

            //Act and Assert
            assertThrows(IllegalArgumentException.class, ()->policyService.createPolicy(request));
            verify(policyRepository, never()).save(any());
        }

        @Test
        void createPolicy_policeWithGivenNumberAlreadyExists_throwsException(){
            //Arrange
            LocalDate startDate = LocalDate.of(2026,1,2);
            LocalDate endDate = startDate.plusDays(365);

            PolicyCreationRequest request = new PolicyCreationRequest(
                    "123-4567-8910",
                    startDate,
                    endDate,
                    BigDecimal.ONE,
                    1L
            );

            when(policyRepository.existsByPolicyNumber(request.policyNumber())).thenReturn(true);

            //Act and assert
            assertThrows(
                    PolicyAlreadyExistsException.class,
                    ()->policyService.createPolicy(request)
            );

            verify(policyRepository, never()).save(any());
        }

        @Test
        void createPolicy_dataIsValid_createsAndReturnsPolicy(){
            //Arrange
            LocalDate startDate = LocalDate.of(2026,1,2);
            LocalDate endDate = startDate.plusDays(365);

            PolicyCreationRequest request = new PolicyCreationRequest(
                    "123-4567-8910",
                    startDate,
                    endDate,
                    new BigDecimal("1.00"),
                    1L
            );

            Customer customer = Customer.builder()
                    .id(request.customerId())
                    .build();

            when(policyRepository.existsByPolicyNumber(request.policyNumber())).thenReturn(false);
            when(customerService.getById(request.customerId())).thenReturn(customer);
            when(policyRepository.save(any())).thenAnswer(i -> i.getArgument(0));

            //Act
            policyService.createPolicy(request);

            //Assert
            verify(policyRepository).save(policyCaptor.capture());
            Policy saved = policyCaptor.getValue();
            assertEquals(endDate, saved.getEndDate());
            assertEquals(startDate, saved.getStartDate());
            assertEquals(request.policyNumber(), saved.getPolicyNumber());
            assertEquals(request.customerId(), saved.getCustomer().getId());
            assertEquals(request.sumInsured(), saved.getSumInsured());
        }

    }

    @Test
    void getPolicy_policyDoesntExist_throwsException(){
        //Arrange
        when(policyRepository.findById(any())).thenReturn(Optional.empty());

        //Act and Assert
        assertThrows(PolicyNotFoundException.class, () ->  policyService.getPolicy(1L));
    }


}
