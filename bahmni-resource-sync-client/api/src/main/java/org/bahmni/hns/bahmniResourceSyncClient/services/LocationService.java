package org.bahmni.hns.bahmniResourceSyncClient.services;

import org.hl7.fhir.dstu3.model.Location;
import org.openmrs.LocationTag;
import org.openmrs.api.context.Context;
import org.openmrs.healthStandard.converter.fhir.FHIRConverter;
import org.openmrs.healthStandard.converter.fhir.FHIRConverterRegistry;

import java.util.Set;

public class LocationService {

    public org.openmrs.Location save(Location fhirLocation) {
        FHIRConverter fhirConverter = FHIRConverterRegistry.getInstance()
                .getConverterFor(Location.class, org.openmrs.Location.class);
        org.openmrs.Location openmrsLocation = (org.openmrs.Location) fhirConverter.convert(fhirLocation);
        org.openmrs.api.LocationService omrsLocationService = Context.getLocationService();
        saveTags(openmrsLocation, omrsLocationService);
        return omrsLocationService.saveLocation(openmrsLocation);
    }

    private void saveTags(org.openmrs.Location openmrsLocation, org.openmrs.api.LocationService omrsLocationService) {
        Set<LocationTag> tags = openmrsLocation.getTags();
        if (tags != null) {
            for (LocationTag tag : tags) {
                omrsLocationService.saveLocationTag(tag);
            }
        }
    }
}
