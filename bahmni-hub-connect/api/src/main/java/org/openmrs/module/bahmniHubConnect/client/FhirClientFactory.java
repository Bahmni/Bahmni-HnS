package org.openmrs.module.bahmniHubConnect.client;

import org.hl7.fhir.dstu3.model.DomainResource;

public class FhirClientFactory {

    public  FhirClient getClientFor(Class<?extends DomainResource> resourceType){
        return new FhirClientImpl(resourceType);
    }
}
