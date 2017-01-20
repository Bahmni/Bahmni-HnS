package org.openmrs.healthStandard.converter.fhir;

import org.openmrs.healthStandard.converter.HealthStandardConverter;

public interface FHIRConverter<S, T> extends HealthStandardConverter {

    T convert(S source);

    default int getOrder() {
        return Integer.MAX_VALUE;
    }
}
