package com.sigmaproject1.project1.exception;

import java.time.LocalDate;

public class IncidentOutsidePolicyPeriodException extends RuntimeException {
    public IncidentOutsidePolicyPeriodException(LocalDate incidentDate, LocalDate policyStartDate, LocalDate policyEndDate) {
        super("Incident at "+incidentDate+" Outside Policy Period: " + policyStartDate + " - " + policyEndDate);
    }
}
