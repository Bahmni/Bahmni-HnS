package org.openmrs.healthStandard.converter.fhir;

import org.hl7.fhir.dstu3.model.DomainResource;
import org.openmrs.Location;
import org.openmrs.OpenmrsObject;
import org.openmrs.api.APIException;
import org.openmrs.healthStandard.converter.Converter;
import org.openmrs.healthStandard.util.ConverterClassScanner;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FHIRConverterFactory {

    private Map<Class<? extends OpenmrsObject>, FHIRConverter> fhirConverters;

    private static FHIRConverterFactory fhirConverterFactory;

    private FHIRConverterFactory() {
        if (fhirConverters == null) {
            fhirConverters = new HashMap<>();
        }
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
        if (fhirConverters != null) {
            return;
        }
        List<Class<? extends FHIRConverter>> converterClasses = getConverterClassesFromClassPath();
        for (Class<? extends FHIRConverter> converterClass : converterClasses) {
            Converter converterAnnotation = null;
            try {
                converterAnnotation = converterClass.getAnnotation(Converter.class);
            } catch (Exception e) {
                continue;
            }
            if (converterAnnotation != null) {
                FHIRConverter existingConverter = fhirConverters.get(converterAnnotation.type());
                if (existingConverter == null) {
                    //add
                } else {
                    int order = existingConverter.getClass().getAnnotation(Converter.class).order();
                    if (converterAnnotation.order() < order) {

                    }
                }
            }
        }
    }

    private List<Class<? extends FHIRConverter>> getConverterClassesFromClassPath() {
        List<Class<? extends FHIRConverter>> converterClasses;
        try {
            converterClasses = ConverterClassScanner.getInstance().getClasses(FHIRConverter.class, true);
        } catch (IOException e) {
            throw new APIException("cannot access converters", e);
        }
        return converterClasses;
    }
}
