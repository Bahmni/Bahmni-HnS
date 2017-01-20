package org.openmrs.healthStandard.converter.fhir.fhirToOpenMRS;

import org.hl7.fhir.dstu3.model.Address;
import org.hl7.fhir.dstu3.model.Location;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.StringType;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.healthStandard.converter.fhir.FHIRConverterRegistry;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;

public class FhirToOpenMRSLocationConverterTest extends BaseModuleContextSensitiveTest {
    static final String LOCATION_DATA_SET = "location-data-set.xml";

    private FhirToOpenMRSLocationConverter converter;

    @Before
    public void setUp() throws Exception {
        converter = (FhirToOpenMRSLocationConverter) FHIRConverterRegistry.getInstance().getConverterFor(Location.class, org.openmrs.Location.class);
        executeDataSet(LOCATION_DATA_SET);

    }

    @Test
    public void shouldConvertNewFhirLocationToOpenmrsLocation() throws Exception {
        Location fhirLocation = new Location();
        String locationName = "Inpatient ward";
        String uuid = "123";
        String parentUuid = "f08ba64b-ea57-4a41-b33c-9dfc59b0c60a";
        fhirLocation.setName(locationName);
        fhirLocation.setId(uuid);
        fhirLocation.setStatus(Location.LocationStatus.ACTIVE);

        Address fhirAddress = new Address();
        fhirAddress.setCity("Bangalore");
        fhirAddress.setDistrict("Bengaluru district");
        fhirAddress.setState("Karnataka");
        fhirAddress.setCountry("India");
        fhirAddress.setLine(Arrays.asList(new StringType("142"), new StringType("M.G. Road"), new StringType("Gandhi nagar")));
        fhirLocation.setAddress(fhirAddress);
        Reference parent = new Reference();
        parent.setReference("Location/" + parentUuid);
        fhirLocation.setPartOf(parent);


        org.openmrs.Location location = converter.convert(fhirLocation);

        assertEquals(uuid, location.getUuid());
        assertEquals(locationName, location.getName());
        assertFalse(location.getRetired());
        assertEquals("Bangalore", location.getCityVillage());
        assertEquals("Bengaluru district", location.getCountyDistrict());
        assertEquals("Karnataka", location.getStateProvince());
        assertEquals("142", location.getAddress1());
        assertEquals("M.G. Road", location.getAddress2());
        assertEquals("Gandhi nagar", location.getAddress3());
        assertEquals(location.getParentLocation(), Context.getLocationService().getLocationByUuid(parentUuid));
    }

    @Test
    public void shouldConvertExistingFhirLocationToOpenmrsLocation() throws Exception {
        org.openmrs.Location existingOpenmrsLocation = Context.getLocationService().getLocation(1002);
        Location fhirLocation = new Location();
        fhirLocation.setId(existingOpenmrsLocation.getUuid());
        String locationName = "Inpatient ward";
        fhirLocation.setName(locationName);
        fhirLocation.setStatus(Location.LocationStatus.ACTIVE);

        assertNotEquals(locationName, existingOpenmrsLocation.getName());

        org.openmrs.Location location = converter.convert(fhirLocation);

        assertEquals(existingOpenmrsLocation, location);
        assertEquals(locationName, existingOpenmrsLocation.getName());
    }
}