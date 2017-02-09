package org.openmrs.module.bahmniHubConnect.services;

import org.hl7.fhir.dstu3.model.Location;
import org.openmrs.api.context.Context;
import org.openmrs.healthStandard.converter.fhir.FHIRConverter;
import org.openmrs.healthStandard.converter.fhir.FHIRConverterRegistry;
import org.openmrs.healthStandard.validator.fhir.FhirResourceValidator;

public class LocationService {

    public org.openmrs.Location save(Location fhirLocation) {
        FhirResourceValidator.validateResource(fhirLocation);

        FHIRConverter fhirConverter = FHIRConverterRegistry.getInstance()
                .getConverterFor(Location.class, org.openmrs.Location.class);
        org.openmrs.Location openmrsLocation = (org.openmrs.Location) fhirConverter.convert(fhirLocation);
        org.openmrs.api.LocationService omrsLocationService = Context.getLocationService();
        return omrsLocationService.saveLocation(openmrsLocation);
    }
}
