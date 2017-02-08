package org.openmrs.module.bahmniHubConnect.client;

import ca.uhn.fhir.rest.client.IGenericClient;
import org.hl7.fhir.dstu3.model.DomainResource;
import org.hl7.fhir.dstu3.model.Location;
import org.openmrs.healthStandard.converter.fhir.fhirModels.FhirLocation;
import org.openmrs.module.bahmniHubConnect.FhirHelper;

import java.util.HashMap;
import java.util.Map;

public class FhirClient {
    private Map<Class,Class> resourceMap = new HashMap<>();

    public FhirClient() {
        resourceMap.put(Location.class, FhirLocation.class);
    }

    public DomainResource getResource(Class<? extends DomainResource> resourceType, String uuid) {
        IGenericClient client = FhirHelper.getClient();
        Class<? extends DomainResource> resType =
                resourceMap.containsKey(resourceType) ? resourceMap.get(resourceType) : resourceType;

        return client.read().resource(resType).withId(uuid).execute();
    }
}
