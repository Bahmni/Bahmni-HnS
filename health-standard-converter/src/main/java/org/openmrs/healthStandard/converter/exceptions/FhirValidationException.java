package org.openmrs.healthStandard.converter.exceptions;

public class FhirValidationException extends RuntimeException {
    public FhirValidationException(String message) {
        super(message);
    }
}
