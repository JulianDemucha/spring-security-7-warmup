package com.sigmaproject1.project1.service;

import com.sigmaproject1.project1.dto.request.ClaimCreationRequest;
import com.sigmaproject1.project1.entity.Claim;
import com.sigmaproject1.project1.entity.Policy;
import com.sigmaproject1.project1.exception.ClaimExceedsSumInsuredException;
import com.sigmaproject1.project1.exception.ClaimNotFoundException;
import com.sigmaproject1.project1.exception.IncidentOutsidePolicyPeriodException;
import com.sigmaproject1.project1.repository.ClaimRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClaimServiceTest {
    @Mock
    private ClaimRepository claimRepository;

    @Mock
    private PolicyService policyService;

    @Mock
    private final Clock clock = Clock.fixed(Instant.parse("2026-01-01T00:00:00Z"), ZoneOffset.UTC);

    @InjectMocks
    private ClaimService claimService;

    private final ArgumentCaptor<Claim> claimCaptor = ArgumentCaptor.forClass(Claim.class);

    @BeforeEach
    void setUp() {
        claimService = new ClaimService(claimRepository, policyService, clock);
    }

    @Nested
    class CreateClaimTests {

        @Test
        void createClaim_incidentDateAfterPolicyEndDate_throwsException() {
            //Arrange
            LocalDate incidentDate = LocalDate.of(2026, 1, 2);
            LocalDate policyEndDate = incidentDate.minusDays(1);
            LocalDate policyStartDate = policyEndDate.minusDays(365);

            ClaimCreationRequest request = new ClaimCreationRequest(
                    1L,
                    "ok ok",
                    incidentDate,
                    BigDecimal.valueOf(1000000)
            );

            Policy policy = Policy
                    .builder()
                    .id(1L)
                    .endDate(policyEndDate)
                    .startDate(policyStartDate)
                    .sumInsured(BigDecimal.valueOf(1000000))
                    .build();

            when(policyService.getPolicy(1L)).thenReturn(policy);

            //Act and Assert
            assertThrows(IncidentOutsidePolicyPeriodException.class, () ->
                claimService.createClaim(request)
            );
        }

        @Test
        void createClaim_incidentDateBeforePolicyStartDate_throwsException() {
            //Arrange
            LocalDate incidentDate = LocalDate.of(2026, 1, 2);
            LocalDate policyStartDate = incidentDate.plusDays(1);
            LocalDate policyEndDate = policyStartDate.plusDays(365);

            ClaimCreationRequest request = new ClaimCreationRequest(
                    1L,
                    "ok ok",
                    incidentDate,
                    BigDecimal.valueOf(1000000)
            );

            Policy policy = Policy
                    .builder()
                    .id(1L)
                    .endDate(policyEndDate)
                    .startDate(policyStartDate)
                    .sumInsured(BigDecimal.valueOf(1000000))
                    .build();

            when(policyService.getPolicy(1L)).thenReturn(policy);

            //Act and Assert
            assertThrows(IncidentOutsidePolicyPeriodException.class, () ->
                claimService.createClaim(request)
            );

            verify(claimRepository, never()).save(any());
        }

        @Test
        void createClaim_claimedAmountExceedsSumInsured_throwsException() {
            //Arrange
            LocalDate incidentDate = LocalDate.of(2026, 1, 1);
            LocalDate policyStartDate = incidentDate.minusDays(60);
            LocalDate policyEndDate = policyStartDate.plusDays(365);

            BigDecimal claimedAmount = BigDecimal.valueOf(1000000);
            BigDecimal sumInsured = claimedAmount.subtract(BigDecimal.ONE);

            ClaimCreationRequest request = new ClaimCreationRequest(
                    1L,
                    "ok ok",
                    incidentDate,
                    claimedAmount
            );

            Policy policy = Policy
                    .builder()
                    .id(1L)
                    .endDate(policyEndDate)
                    .startDate(policyStartDate)
                    .sumInsured(sumInsured)
                    .build();

            when(policyService.getPolicy(1L)).thenReturn(policy);

            //Act and Assert
            assertThrows(ClaimExceedsSumInsuredException.class, () ->
                claimService.createClaim(request)
            );

            verify(claimRepository, never()).save(any());
        }

        @Test
        void createClaim_dataIsValid_savesAndReturnsClaim() {
            //Arrange
            LocalDate incidentDate = LocalDate.of(2026, 1, 1);
            LocalDate policyStartDate = incidentDate.minusDays(60);
            LocalDate policyEndDate = policyStartDate.plusDays(365);

            ClaimCreationRequest request = new ClaimCreationRequest(
                    1L,
                    "ok ok",
                    incidentDate,
                    BigDecimal.valueOf(1000000)
            );

            Policy policy = Policy
                    .builder()
                    .id(1L)
                    .endDate(policyEndDate)
                    .startDate(policyStartDate)
                    .sumInsured(BigDecimal.valueOf(1000000))
                    .build();

            when(policyService.getPolicy(1L)).thenReturn(policy);
            when(claimRepository.save(any())).thenAnswer(i -> i.getArgument(0));
            when(Instant.now(clock)).thenReturn(incidentDate.atStartOfDay(ZoneOffset.UTC).toInstant());

            //Act
            claimService.createClaim(request);

            //Assert
            verify(claimRepository).save(claimCaptor.capture());
            Claim savedClaim = claimCaptor.getValue();

            assertEquals(request.claimedAmount(), savedClaim.getClaimedAmount());
            assertEquals(request.description(), savedClaim.getDescription());
            assertEquals(request.incidentDate(), savedClaim.getIncidentDate());
            assertEquals(request.policyId(), savedClaim.getPolicy().getId());


        }

    }

    @Test
    void getClaim_claimDoesNotExist_throwsException(){
        //Arrange
        when(claimRepository.findById(1L)).thenReturn(Optional.empty());

        //Act and Assert
        assertThrows(
                ClaimNotFoundException.class,
                () -> claimService.getClaim(1L)
        );
    }

}


