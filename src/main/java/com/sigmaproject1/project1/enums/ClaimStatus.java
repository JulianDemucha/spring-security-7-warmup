package com.sigmaproject1.project1.enums;

public enum ClaimStatus {
    NEW, IN_REVIEW, APPROVED, REJECTED;

    public boolean canTransitionTo(ClaimStatus target) {
        return switch (this) {
            case NEW -> target == IN_REVIEW;
            case IN_REVIEW -> target == APPROVED || target == REJECTED;
            case APPROVED, REJECTED -> false;
        };
    }
}