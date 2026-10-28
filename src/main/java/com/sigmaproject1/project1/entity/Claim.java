package com.sigmaproject1.project1.entity;

import com.sigmaproject1.project1.enums.ClaimStatus;
import com.sigmaproject1.project1.exception.InvalidClaimStatusTransitionException;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
public class Claim {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @NotNull
    private Policy policy;

    @Size(max = 2000)
    @NotNull
    private String description;

    @NotNull
    private LocalDate incidentDate;

    @Positive
    @NotNull
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal claimedAmount;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @NotNull
    private ClaimStatus status = ClaimStatus.NEW;

    @NotNull
    private Instant createdAt;

    @Version
    private Long version;

    public void moveToReview(){
        transitionTo(ClaimStatus.IN_REVIEW);
    }

    public void rejectClaim(){
        transitionTo(ClaimStatus.REJECTED);
    }

    public void approveClaim(){
        transitionTo(ClaimStatus.APPROVED);
    }

    private void transitionTo(ClaimStatus target){
        if(!status.canTransitionTo(target))
            throw new InvalidClaimStatusTransitionException(status, target);

        this.status = target;
    }
}
