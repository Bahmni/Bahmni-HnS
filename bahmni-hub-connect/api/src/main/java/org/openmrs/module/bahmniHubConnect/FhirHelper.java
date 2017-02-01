package org.openmrs.module.bahmniHubConnect;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.IGenericClient;
import ca.uhn.fhir.rest.client.interceptor.BasicAuthInterceptor;

public class FhirHelper {
    public static IGenericClient getClient() {
        FhirContext fhirContext = FhirContext.forDstu3();
        BahmniResourceSyncProperties bahmniResourceSyncProperties = BahmniResourceSyncProperties.getInstance();
        String fhirBaseUrl = bahmniResourceSyncProperties.getFhirBaseUrl();
        //todo : figure out way for https
        IGenericClient genericClient = fhirContext.getRestfulClientFactory().newGenericClient(fhirBaseUrl);
        BasicAuthInterceptor authInterceptor =
                new BasicAuthInterceptor(bahmniResourceSyncProperties.getFhirApiUserName(),
                        bahmniResourceSyncProperties.getFhirApiPassword());
        genericClient.registerInterceptor(authInterceptor);
        return genericClient;
    }
}
