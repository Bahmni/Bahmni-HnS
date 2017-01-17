package org.bahmni.hns.bahmniResourceSyncClient.workers;

import org.bahmni.hns.bahmniResourceSyncClient.client.FhirClient;
import org.bahmni.hns.bahmniResourceSyncClient.client.FhirClientFactory;
import org.bahmni.hns.bahmniResourceSyncClient.services.LocationService;
import org.hl7.fhir.dstu3.model.Location;
import org.ict4h.atomfeed.client.domain.Event;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@PrepareForTest(FhirClientFactory.class)
@RunWith(PowerMockRunner.class)
public class LocationEventWorkerTest {
    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Mock
    private LocationService locationService;
    @Mock
    private FhirClient fhirLocationClient;


    private LocationEventWorker locationEventWorker;


    @Before
    public void setUp() throws Exception {
        initMocks(this);
        PowerMockito.mockStatic(FhirClientFactory.class);
        when(FhirClientFactory.getClientFor(Location.class)).thenReturn(fhirLocationClient);

    }

    @Test
    public void shouldFetchLocationAndSave() throws Exception {
        locationEventWorker = new LocationEventWorker(locationService);
        String locationUuid ="8d6c993e-c2cc-11de-8d13-0010c6dffd0f" ;
        Event event = new Event("someId", "/openmrs/ws/rest/v1/location/8d6c993e-c2cc-11de-8d13-0010c6dffd0f?v=full");
        Location fhirLocation = new Location();
        when(fhirLocationClient.getResource(locationUuid)).thenReturn(fhirLocation);
        locationEventWorker.process(event);
        verify(locationService).save(fhirLocation);
    }

    @Test
    public void shouldThrowExcetionWhenUrlIsInvalid() throws Exception {
        expectedEx.expect(IllegalArgumentException.class);
        expectedEx.expectMessage("Invalid Url: :adcd:dd:dddd");
        locationEventWorker = new LocationEventWorker(locationService);
        Event event = new Event("someId", ":adcd:dd:dddd");
        locationEventWorker.process(event);
    }
}