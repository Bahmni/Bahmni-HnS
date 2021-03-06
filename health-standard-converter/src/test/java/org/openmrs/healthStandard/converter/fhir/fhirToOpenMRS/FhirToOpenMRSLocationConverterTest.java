package org.openmrs.healthStandard.converter.fhir.fhirToOpenMRS;

import org.hl7.fhir.dstu3.model.Address;
import org.hl7.fhir.dstu3.model.Location;
import org.hl7.fhir.dstu3.model.Location.LocationStatus;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.StringType;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openmrs.LocationTag;
import org.openmrs.api.context.Context;
import org.openmrs.healthStandard.converter.exceptions.EntityNotFoundException;
import org.openmrs.healthStandard.converter.fhir.FHIRConverterRegistry;
import org.openmrs.healthStandard.converter.fhir.fhirModels.FhirLocation;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.util.Arrays;
import java.util.Set;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class FhirToOpenMRSLocationConverterTest extends BaseModuleContextSensitiveTest {
    static final String LOCATION_DATA_SET = "location-data-set.xml";
    @Rule
    public ExpectedException expectedEx = ExpectedException.none();
    private FhirToOpenMRSLocationConverter converter;

    @Before
    public void setUp() throws Exception {
        converter = (FhirToOpenMRSLocationConverter) FHIRConverterRegistry.getInstance().getConverterFor(Location.class, org.openmrs.Location.class);
        executeDataSet(LOCATION_DATA_SET);

    }

    @Test
    public void shouldConvertNewFhirLocationToOpenmrsLocation() throws Exception {
        FhirLocation fhirLocation = new FhirLocation();
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
        FhirLocation fhirLocation = new FhirLocation();
        fhirLocation.setId(existingOpenmrsLocation.getUuid());
        String locationName = "Inpatient ward";
        fhirLocation.setName(locationName);
        fhirLocation.setStatus(Location.LocationStatus.ACTIVE);

        assertNotEquals(locationName, existingOpenmrsLocation.getName());

        org.openmrs.Location location = converter.convert(fhirLocation);

        assertEquals(existingOpenmrsLocation, location);
        assertEquals(locationName, existingOpenmrsLocation.getName());
    }

    @Test
    public void shouldThrowExceptionWhenLocationTagsDoseNotExist() throws Exception {
        FhirLocation fhirLocation = new FhirLocation();
        StringType fhirLocationTag = new StringType("someId");
        fhirLocation.addTag(fhirLocationTag);
        fhirLocation.setStatus(LocationStatus.ACTIVE);

        expectedEx.expect(EntityNotFoundException.class);
        expectedEx.expectMessage("Location Tag with uuid=someId does not exist");
        converter.convert(fhirLocation);
    }

    @Test
    public void shouldMapExistingLocationTags() throws Exception {
        FhirLocation fhirLocation = new FhirLocation();
        String existingUuid = "0d0eaea2-47ed-11df-bc8b-001e378eb67e";

        StringType fhirLocationTag = new StringType(existingUuid);
        fhirLocation.addTag(fhirLocationTag);
        fhirLocation.setStatus(LocationStatus.ACTIVE);

        org.openmrs.Location omrsLocation = converter.convert(fhirLocation);
        LocationTag locationTag = omrsLocation.getTags().iterator().next();
        assertEquals("Department", locationTag.getName());
        assertEquals("random department", locationTag.getDescription());
        assertEquals(existingUuid, locationTag.getUuid());
    }

    @Test
    public void shouldThrowExceptionWhenParentLocationDoesNotExist() throws Exception {
        FhirLocation fhirLocation = new FhirLocation();
        fhirLocation.setStatus(LocationStatus.ACTIVE);
        Reference parent = new Reference();
        parent.setReference("Location/" + "someIdThatIsNotPresent");
        fhirLocation.setPartOf(parent);

        expectedEx.expect(EntityNotFoundException.class);
        expectedEx.expectMessage("Parent Location with uuid=someIdThatIsNotPresent does not exist");
        converter.convert(fhirLocation);
    }

    @Test
    public void shouldResetParentLocationToNullWhenItIsRemoved() throws Exception {
        FhirLocation fhirLocation = new FhirLocation();
        fhirLocation.setId("6f42abbc-caac-40ae-a94e-9277ea15c125");
        fhirLocation.setName("SomeName");
        fhirLocation.setStatus(LocationStatus.ACTIVE);
        fhirLocation.setPartOf(null);
        org.openmrs.Location omrsLocation = converter.convert(fhirLocation);

        org.openmrs.Location parentLocation = omrsLocation.getParentLocation();
        System.out.println(parentLocation);
        assertThat(parentLocation, is(nullValue()));
    }

    @Test
    public void shouldRemoveLocationTags() throws Exception {
        FhirLocation fhirLocation = new FhirLocation();
        String locationTagUuid = "001e503a-47ed-11df-bc8b-001e378eb67e";

        fhirLocation.setId("f08ba64b-ea57-4a41-b33c-9dfc59b0c60a");
        fhirLocation.setName("someName");
        fhirLocation.addTag(new StringType(locationTagUuid));
        fhirLocation.setStatus(LocationStatus.ACTIVE);

        org.openmrs.Location omrsLocation = converter.convert(fhirLocation);
        Set<LocationTag> tags = omrsLocation.getTags();
        assertEquals(1, tags.size());

        LocationTag locationTag = tags.iterator().next();
        assertEquals("General Hospital", locationTag.getName());
        assertEquals("test desc", locationTag.getDescription());
    }

    @Test
    public void shouldRetireTheLocationWhenFhirLocationIsInactivated() throws Exception {
        FhirLocation fhirLocation = new FhirLocation();
        fhirLocation.setId("f08ba64b-ea57-4a41-b33c-9dfc59b0c60a");
        fhirLocation.setName("locationToRetire");
        fhirLocation.setStatus(LocationStatus.INACTIVE);
        String retireReason = "Useless Location";
        fhirLocation.setInactivationReason(new StringType(retireReason));

        org.openmrs.Location omrsLocation = converter.convert(fhirLocation);
        assertTrue(omrsLocation.getRetired());
        assertEquals(retireReason, omrsLocation.getRetireReason());

    }
}
