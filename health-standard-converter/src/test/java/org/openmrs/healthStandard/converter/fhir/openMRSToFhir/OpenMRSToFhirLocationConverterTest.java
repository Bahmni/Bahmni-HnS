package org.openmrs.healthStandard.converter.fhir.openMRSToFhir;

import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.dstu3.model.Address;
import org.hl7.fhir.dstu3.model.Location;
import org.hl7.fhir.dstu3.model.Location.LocationStatus;
import org.hl7.fhir.dstu3.model.StringType;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.healthStandard.converter.fhir.FHIRConverter;
import org.openmrs.healthStandard.converter.fhir.FHIRConverterRegistry;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class OpenMRSToFhirLocationConverterTest extends BaseModuleContextSensitiveTest {

    static final String LOCATION_DATA_SET = "location-data-set.xml";
    private FHIRConverter<org.openmrs.Location, Location> converter;

    @Before
    public void setUp() throws Exception {
        converter = FHIRConverterRegistry.getInstance().getConverterFor(org.openmrs.Location.class, Location.class);
        executeDataSet(LOCATION_DATA_SET);
    }

    @Test
    public void shouldConvertOpenMrsLocationToFhirLocation() throws Exception {
        org.openmrs.Location openMRSLocation = Context.getLocationService().getLocation(1002);

        Location fhirLocation = converter.convert(openMRSLocation);

        assertEquals(openMRSLocation.getUuid(), fhirLocation.getId());
        assertEquals(openMRSLocation.getName(), fhirLocation.getName());
        assertEquals(openMRSLocation.getDescription(), fhirLocation.getDescription());
        Address fhirLocationAddress = fhirLocation.getAddress();
        assertEquals(openMRSLocation.getCityVillage(), fhirLocationAddress.getCity());
        assertEquals(openMRSLocation.getCountyDistrict(), fhirLocationAddress.getDistrict());
        assertEquals(openMRSLocation.getStateProvince(), fhirLocationAddress.getState());
        assertEquals(openMRSLocation.getCountry(), fhirLocationAddress.getCountry());
        assertEquals(openMRSLocation.getPostalCode(), fhirLocationAddress.getPostalCode());
        List<StringType> addressLines = fhirLocationAddress.getLine();
        assertEquals(StringUtils.defaultString(openMRSLocation.getAddress1(), ""), addressLines.get(0).getValueNotNull());
        assertEquals(StringUtils.defaultString(openMRSLocation.getAddress2(), ""), addressLines.get(1).getValueNotNull());
        assertEquals(StringUtils.defaultString(openMRSLocation.getAddress3(), ""), addressLines.get(2).getValueNotNull());
        assertEquals(StringUtils.defaultString(openMRSLocation.getAddress4(), ""), addressLines.get(3).getValueNotNull());
        assertEquals(StringUtils.defaultString(openMRSLocation.getAddress5(), ""), addressLines.get(4).getValueNotNull());
        assertEquals(openMRSLocation.getLatitude(),fhirLocation.getPosition().getLatitude().toString());
        assertEquals(openMRSLocation.getLongitude(),fhirLocation.getPosition().getLongitude().toString());
        assertEquals(LocationStatus.ACTIVE,fhirLocation.getStatus());
        assertEquals(openMRSLocation.getParentLocation().getUuid(), fhirLocation.getPartOf().getReferenceElement().getIdPart());
    }

    @Test
    public void shouldSetStatusToInactiveWhenLocationIsRetired() throws Exception {
        org.openmrs.Location openMRSLocation = Context.getLocationService().getLocation(1003);
        Location fhirLocation = converter.convert(openMRSLocation);
        assertEquals(LocationStatus.INACTIVE,fhirLocation.getStatus());
    }
}