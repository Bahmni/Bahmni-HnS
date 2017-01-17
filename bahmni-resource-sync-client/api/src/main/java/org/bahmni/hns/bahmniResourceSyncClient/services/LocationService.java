package org.bahmni.hns.bahmniResourceSyncClient.services;

import org.hl7.fhir.dstu3.model.Location;
import org.openmrs.api.context.Context;
import org.openmrs.healthStandard.converter.fhir.FHIRConverterFactory;
import org.openmrs.healthStandard.converter.fhir.LocationConverter;

public class LocationService {
  public org.openmrs.Location save(Location fhirLocation) {
    LocationConverter locationConverter = (LocationConverter) FHIRConverterFactory.getInstance().getConverterFor(org.openmrs.Location.class);
    org.openmrs.Location openmrsLocation = locationConverter.toEMRResource(fhirLocation);
    return Context.getLocationService().saveLocation(openmrsLocation);
  }
}
