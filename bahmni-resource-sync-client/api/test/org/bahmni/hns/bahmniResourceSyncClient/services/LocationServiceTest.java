package org.bahmni.hns.bahmniResourceSyncClient.services;

import org.hl7.fhir.dstu3.model.Location.LocationStatus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.openmrs.LocationTag;
import org.openmrs.api.context.Context;
import org.openmrs.healthStandard.converter.fhir.fhirModels.FhirLocation;
import org.openmrs.healthStandard.converter.fhir.fhirModels.FhirLocationTag;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@PrepareForTest(Context.class)
@RunWith(PowerMockRunner.class)
public class LocationServiceTest {

    @Mock
    private org.openmrs.api.LocationService openmrsLocationService;
    private LocationService fhirLocationsService;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        PowerMockito.mockStatic(Context.class);
        when(Context.getLocationService()).thenReturn(openmrsLocationService);
    }

    @Test
    public void shouldSaveLocationToOpenmrs() throws Exception {
        FhirLocation fhirLocation = new FhirLocation();
        fhirLocation.setId("SomeId");
        fhirLocation.setStatus(LocationStatus.ACTIVE);

        fhirLocationsService = new LocationService();
        fhirLocationsService.save(fhirLocation);

        ArgumentCaptor<org.openmrs.Location> locationCaptor = ArgumentCaptor.forClass(org.openmrs.Location.class);
        Mockito.verify(openmrsLocationService).saveLocation(locationCaptor.capture());
        org.openmrs.Location savedLocation = locationCaptor.getValue();
        assertEquals(fhirLocation.getIdElement().getIdPart(), savedLocation.getUuid());
    }

    @Test
    public void shouldSaveLocationTagToOpenmrs() throws Exception {
        FhirLocation fhirLocation = new FhirLocation();
        fhirLocation.setId("SomeId");
        fhirLocation.setStatus(LocationStatus.ACTIVE);
        FhirLocationTag fhirLocationTag = new FhirLocationTag("TestTag","Test Tag");
        fhirLocation.setFhirLocationTags(Arrays.asList(fhirLocationTag));

        fhirLocationsService = new LocationService();
        fhirLocationsService.save(fhirLocation);

        ArgumentCaptor<org.openmrs.LocationTag> locationTagCaptor = ArgumentCaptor.forClass(org.openmrs.LocationTag.class);
        Mockito.verify(openmrsLocationService).saveLocationTag(locationTagCaptor.capture());
        LocationTag locationTag = locationTagCaptor.getValue();
        assertEquals(fhirLocationTag.getName(), locationTag.getName());
    }
}