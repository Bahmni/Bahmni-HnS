package org.openmrs.module.bahmniHubConnect.workers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.bahmniHubConnect.client.FhirClientFactory;
import org.openmrs.module.bahmniHubConnect.services.LocationService;
import org.hl7.fhir.dstu3.model.Location;
import org.ict4h.atomfeed.client.domain.Event;
import org.ict4h.atomfeed.client.service.EventWorker;

import java.net.URI;
import java.net.URISyntaxException;

public class LocationEventWorker implements EventWorker {
    Log log = LogFactory.getLog(LocationEventWorker.class);

    LocationService locationService;
    FhirClientFactory fhirClientFactory;

    public LocationEventWorker() {
        this(new LocationService(), new FhirClientFactory());
    }

    public LocationEventWorker(LocationService locationService, FhirClientFactory fhirClientFactory) {
        this.locationService = locationService;
        this.fhirClientFactory = fhirClientFactory;
    }

    public void process(Event event) {
        String contentUrl = event.getContent();
        String uuid = getUuid(contentUrl);
        Location location = (Location) fhirClientFactory.getClientFor(Location.class).getResource(uuid);
        locationService.save(location);
    }


    public void cleanUp(Event event) {
    }

    private String getUuid(String contentUrl) {
        try {
            URI uri = new URI(contentUrl);
            String path = uri.getPath();
            return path.substring(path.lastIndexOf("/") + 1);
        } catch (URISyntaxException e) {
            log.error("Invalid Url: " + contentUrl, e);
            throw new IllegalArgumentException("Invalid Url: " + contentUrl, e);
        }
    }

}
