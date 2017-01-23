package org.openmrs.healthStandard.converter.fhir.fhirModels;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import org.junit.Test;

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class FhirLocationTest {

    @Test
    public void shouldSerializeAndDeserializeFhirLocation() throws Exception {
        FhirLocation fhirLocation = new FhirLocation();
        fhirLocation.setId("Location/SomeId");
        FhirLocationTag fhirLocationTag = new FhirLocationTag("someUuis","VisitLocation", "VisitLocation");
        fhirLocation.setFhirLocationTags(Arrays.asList(fhirLocationTag));
        IParser parser = FhirContext.forDstu3().newJsonParser();

        String encodedLocation = parser.encodeResourceToString(fhirLocation);

        FhirLocation parsedLocation = parser.parseResource(FhirLocation.class, encodedLocation);

        assertEquals(fhirLocation.getId(), parsedLocation.getId());
        for (FhirLocationTag tag : fhirLocation.getFhirLocationTags()) {
            assertThat(fhirLocation.getFhirLocationTags(), hasItem(tag));
        }


    }
}