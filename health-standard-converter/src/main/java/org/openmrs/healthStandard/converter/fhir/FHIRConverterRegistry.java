package org.openmrs.healthStandard.converter.fhir;

import org.openmrs.Location;
import org.openmrs.healthStandard.converter.ConverterSameOrderException;
import org.openmrs.healthStandard.converter.fhir.fhirToOpenMRS.FhirToOpenMRSLocationConverter;
import org.openmrs.healthStandard.converter.fhir.openMRSToFhir.OpenMRSToFhirLocationConverter;

import java.util.HashMap;
import java.util.Map;

public class FHIRConverterRegistry {

    private static FHIRConverterRegistry fhirConverterRegistry;
    private Map<ConverterType, FHIRConverter> fhirConverters;

    private FHIRConverterRegistry() {
        if (fhirConverters == null) {
            fhirConverters = new HashMap<>();
        }
        initialiseConverters();
    }

    public static FHIRConverterRegistry getInstance() {
        if (fhirConverterRegistry == null) {
            fhirConverterRegistry = new FHIRConverterRegistry();
        }
        return fhirConverterRegistry;
    }

    private void initialiseConverters() {
        registerConverter(Location.class, org.hl7.fhir.dstu3.model.Location.class, new OpenMRSToFhirLocationConverter());
        registerConverter(org.hl7.fhir.dstu3.model.Location.class, Location.class, new FhirToOpenMRSLocationConverter());
    }

    public <S, T> FHIRConverter<S, T> getConverterFor(Class<S> sourceClass, Class<T> targetClass) {
        return fhirConverters.get(new ConverterType(sourceClass, targetClass));
    }

    public void registerConverter(Class sourceClass, Class targetClass, FHIRConverter converterToRegister) {
        ConverterType converterType = new ConverterType(sourceClass, targetClass);
        FHIRConverter existingConverter = fhirConverters.get(converterType);
        if (canAdd(converterToRegister, existingConverter)) {
            fhirConverters.put(converterType, converterToRegister);
        }

    }

    private boolean canAdd(FHIRConverter converterToRegister, FHIRConverter existingConverter) {
        if (existingConverter != null && converterToRegister.getOrder() == existingConverter.getOrder()) {
            throw new ConverterSameOrderException();
        }
        return existingConverter == null || existingConverter.getOrder() > converterToRegister.getOrder();
    }

    private class ConverterType {
        private final Class source;
        private final Class target;

        public ConverterType(Class source, Class target) {

            this.source = source;
            this.target = target;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ConverterType that = (ConverterType) o;

            if (!source.equals(that.source)) return false;
            return target.equals(that.target);
        }

        @Override
        public int hashCode() {
            int result = source.hashCode();
            result = 31 * result + target.hashCode();
            return result;
        }
    }
}
