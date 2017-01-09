package org.openmrs.healthStandard.converter;

import org.openmrs.OpenmrsObject;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface Converter {
    Class<? extends OpenmrsObject> type();

    int order() default Integer.MAX_VALUE;
}
