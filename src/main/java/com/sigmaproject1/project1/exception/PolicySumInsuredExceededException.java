package com.sigmaproject1.project1.exception;

import java.math.BigDecimal;

public class PolicySumInsuredExceededException extends RuntimeException {
    public PolicySumInsuredExceededException(
            BigDecimal claimedAmount, BigDecimal sumInsured,
            BigDecimal alreadyApproved, Long policyId
    ) {
        super("Approving claim of " + claimedAmount.toPlainString()
            + " would exceed sum insured " + sumInsured.toPlainString()
            + " of policy " + policyId
            + " (already approved: " + alreadyApproved.toPlainString() + ")");
    }
}
