package org.bahmni.hns.bahmniResourceSyncClient.client;

import org.hl7.fhir.dstu3.model.DomainResource;

public interface FhirClient {
    DomainResource getResource(String uuid);
}
