package org.openmrs.healthStandard.converter.fhir;

public interface FHIRConverter<S, T> {

    T convert(S source);

    default int getOrder() {
        return Integer.MAX_VALUE;
    }
}
