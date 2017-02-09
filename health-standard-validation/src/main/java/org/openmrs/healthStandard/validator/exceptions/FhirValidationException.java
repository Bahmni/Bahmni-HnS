package org.openmrs.healthStandard.validator.exceptions;

public class FhirValidationException extends RuntimeException {
    public FhirValidationException(String message) {
        super(message);
    }
}
