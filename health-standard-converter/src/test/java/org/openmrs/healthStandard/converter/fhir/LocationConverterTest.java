package org.openmrs.healthStandard.converter.fhir;

import org.hl7.fhir.dstu3.model.Address;
import org.hl7.fhir.dstu3.model.Location;
import org.hl7.fhir.dstu3.model.StringType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.openmrs.api.LocationService;
import org.openmrs.api.context.Context;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Context.class)
public class LocationConverterTest {

    @Mock
    LocationService locationService;

    @Before
    public void setUp() throws Exception {
        PowerMockito.mockStatic(Context.class);
        when(Context.getLocationService()).thenReturn(locationService);
    }

    @Test
    public void toEMRResource_shouldConvertNewFhirLocationToOpenmrsLocation() throws Exception {
        LocationConverter locationConverter = new LocationConverter();
        Location fhirLocation = new Location();
        String locationName = "Inpatient ward";
        String uuid = "123";
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


        org.openmrs.Location location = locationConverter.toEMRResource(fhirLocation);

        assertEquals(uuid, location.getUuid());
        assertEquals(locationName, location.getName());
        assertFalse(location.getRetired());
        assertEquals("Bangalore", location.getCityVillage());
        assertEquals("Bengaluru district", location.getCountyDistrict());
        assertEquals("Karnataka", location.getStateProvince());
        assertEquals("142", location.getAddress1());
        assertEquals("M.G. Road", location.getAddress2());
        assertEquals("Gandhi nagar", location.getAddress3());
    }

    @Test
    public void toEMRResource_shouldConvertExistingFhirLocationToOpenmrsLocation() throws Exception {
        LocationConverter locationConverter = new LocationConverter();
        String uuid = "123";
        org.openmrs.Location existingOpenmrsLocation = new org.openmrs.Location();
        existingOpenmrsLocation.setUuid(uuid);
        existingOpenmrsLocation.setName("adt ward");
        when(locationService.getLocationByUuid(uuid)).thenReturn(existingOpenmrsLocation);
        Location fhirLocation = new Location();
        fhirLocation.setId(uuid);
        String locationName = "Inpatient ward";
        fhirLocation.setName(locationName);
        fhirLocation.setStatus(Location.LocationStatus.ACTIVE);
        org.openmrs.Location location = locationConverter.toEMRResource(fhirLocation);

        assertEquals(locationName, location.getName());
        assertEquals(uuid, location.getUuid());
    }
}