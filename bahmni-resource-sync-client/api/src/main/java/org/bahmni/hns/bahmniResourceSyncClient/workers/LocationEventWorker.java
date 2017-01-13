package org.bahmni.hns.bahmniResourceSyncClient.workers;

import ca.uhn.fhir.rest.client.IGenericClient;
import org.bahmni.hns.bahmniResourceSyncClient.FhirHelper;
import org.bahmni.hns.bahmniResourceSyncClient.services.LocationService;
import org.hl7.fhir.dstu3.model.Location;
import org.ict4h.atomfeed.client.domain.Event;
import org.ict4h.atomfeed.client.service.EventWorker;

import java.net.URI;
import java.net.URISyntaxException;

public class LocationEventWorker implements EventWorker {

  LocationService locationService;

  public LocationEventWorker() {
    locationService = new LocationService();
  }

  public void process(Event event) {
    String contentUrl = event.getContent();
    String uuid = getUuid(contentUrl);
    IGenericClient client = FhirHelper.getClient();
    Location location = client.read().resource(Location.class).withId(uuid).execute();
    locationService.save(location);
  }

  private String getUuid(String contentUrl) {
    try {
      URI uri = new URI(contentUrl);
      String path = uri.getPath();
      return path.substring(path.lastIndexOf("/") + 1);
    } catch (URISyntaxException e) {
      //log and throw
    }

    return null;
  }

  public void cleanUp(Event event) {

  }
}
