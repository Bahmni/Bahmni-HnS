package org.bahmni.hns.bahmniResourceSyncClient;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.IGenericClient;

public class FhirHelper {
  public static IGenericClient getClient() {
    FhirContext fhirContext = FhirContext.forDstu3();
    String fhirBaseUrl = BahmniResourceSyncProperties.getInstance().getFhirBaseUrl();
   //todo : figure out way for https
    return fhirContext.getRestfulClientFactory().newGenericClient(fhirBaseUrl);
  }
}
