package org.openmrs.module.bahmniHubConnect.client;

import ca.uhn.fhir.rest.client.IGenericClient;
import org.openmrs.module.bahmniHubConnect.FhirHelper;
import org.hl7.fhir.dstu3.model.DomainResource;
import org.hl7.fhir.dstu3.model.Location;
import org.openmrs.healthStandard.converter.fhir.fhirModels.FhirLocation;

import java.util.HashMap;
import java.util.Map;

public class FhirClientImpl implements FhirClient {

    private Map<Class,Class> resourceMap = new HashMap<>();
    private  Class<? extends DomainResource> resourceType;


    public FhirClientImpl(Class<? extends DomainResource> resourceType){
        resourceMap.put(Location.class, FhirLocation.class);

        this.resourceType = resourceMap.containsKey(resourceType)?
                resourceMap.get(resourceType):resourceType;
    }
    @Override
    public DomainResource getResource(String uuid) {
        IGenericClient client = FhirHelper.getClient();
        return client.read().resource(resourceType).withId(uuid).execute();
    }

}
