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
@AllArgsConstructor(access = AccessLevel.PRIVATE)
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

    private String rejectionReason;

    public void moveToReview(){
        transitionTo(ClaimStatus.IN_REVIEW);
    }

    public void rejectClaim(String reason) {
        if(reason.isBlank())
            throw new IllegalArgumentException("Reason must not be null nor blank");

        if(reason.length() < 3 || reason.length() > 100)
            throw new IllegalArgumentException("Reason must be between 3 and 100 characters");

        transitionTo(ClaimStatus.REJECTED);
        this.rejectionReason = reason;
    }

    public void approveClaim(){
        transitionTo(ClaimStatus.APPROVED);
    }

    private void transitionTo(ClaimStatus target){
        if(!status.canTransitionTo(target))
            throw new InvalidClaimStatusTransitionException(status, target);

        this.status = target;
    }

    public static class ClaimBuilder {
        public ClaimBuilder claimedAmount(BigDecimal claimedAmount) {
            this.claimedAmount = claimedAmount == null ? null : claimedAmount.setScale(2);
            return this;
        }
    }
}
