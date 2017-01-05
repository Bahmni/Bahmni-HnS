package org.openmrs.healthStandard.converter.fhir;

import org.hl7.fhir.dstu3.model.DomainResource;
import org.openmrs.Location;
import org.openmrs.OpenmrsObject;
import org.openmrs.healthStandard.converter.Converter;
import org.openmrs.util.OpenmrsClassScanner;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class FHIRConverterFactory {

    private Map<Class<? extends OpenmrsObject>, FHIRConverter> fhirConverters = new HashMap<>();

    private static FHIRConverterFactory fhirConverterFactory;

    private FHIRConverterFactory() {
        fhirConverters.put(Location.class, new LocationConverter());
    }

    public static FHIRConverterFactory getInstance() {
        if (fhirConverterFactory == null) {
            fhirConverterFactory = new FHIRConverterFactory();
        }
        return fhirConverterFactory;
    }

    public <O extends OpenmrsObject, F extends DomainResource> FHIRConverter getConverterFor(Class<O> resourceClassName) {
        return fhirConverters.get(resourceClassName);
    }


    public void initialiseConverters() {
        Set<Class<?>> converterClasses = OpenmrsClassScanner.getInstance().getClassesWithAnnotation(Converter.class);
        for (Class<?> converterClass : converterClasses) {

        }
    }
}
