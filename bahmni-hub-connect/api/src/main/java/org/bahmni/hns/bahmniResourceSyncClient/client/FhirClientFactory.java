package org.bahmni.hns.bahmniResourceSyncClient.client;

import org.hl7.fhir.dstu3.model.DomainResource;

public class FhirClientFactory {

    public  FhirClient getClientFor(Class<?extends DomainResource> resourceType){
        return new FhirClientImpl(resourceType);
    }
}
