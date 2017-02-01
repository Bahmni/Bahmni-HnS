package org.openmrs.module.bahmniHubConnect.client;

import org.hl7.fhir.dstu3.model.DomainResource;

public interface FhirClient {
    DomainResource getResource(String uuid);
}
