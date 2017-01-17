package org.bahmni.hns.bahmniResourceSyncClient.client;

import ca.uhn.fhir.rest.client.IGenericClient;
import org.bahmni.hns.bahmniResourceSyncClient.FhirHelper;
import org.hl7.fhir.dstu3.model.DomainResource;

public class FhirClientImpl implements FhirClient {

    private  Class<? extends DomainResource> resourceType;

    public FhirClientImpl(Class<? extends DomainResource> resourceType){

        this.resourceType = resourceType;
    }
    @Override
    public DomainResource getResource(String uuid) {
        IGenericClient client = FhirHelper.getClient();
        return client.read().resource(resourceType).withId(uuid).execute();
    }

}
