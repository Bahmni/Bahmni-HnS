package org.openmrs.healthStandard.converter;

import org.openmrs.OpenmrsObject;

public @interface Converter {
    Class<? extends OpenmrsObject> type();

    int order() default Integer.MAX_VALUE;
}
