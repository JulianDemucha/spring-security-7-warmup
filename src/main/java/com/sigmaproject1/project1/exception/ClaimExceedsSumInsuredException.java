package com.sigmaproject1.project1.exception;

import java.math.BigDecimal;

public class ClaimExceedsSumInsuredException extends RuntimeException {
    public ClaimExceedsSumInsuredException(BigDecimal claimedAmount, BigDecimal sumInsured, long policyId) {
        super("Claimed amount of "+claimedAmount+" exceeds sum insured "+sumInsured+" in policyId: "+policyId);
    }
}
