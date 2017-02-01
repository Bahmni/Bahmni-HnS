package org.openmrs.module.bahmniHubConnect.task;

import org.openmrs.module.bahmniHubConnect.BahmniResourceSyncProperties;
import org.openmrs.module.bahmniHubConnect.client.AtomFeedClientFactory;
import org.ict4h.atomfeed.client.service.FeedClient;
import org.openmrs.scheduler.tasks.AbstractTask;

import java.net.URISyntaxException;

public class LocationResourceFailedEventsSyncTask extends AbstractTask {


  @Override
  public void execute() {
    BahmniResourceSyncProperties properties = BahmniResourceSyncProperties.getInstance();
    FeedClient feedClient;
    try {
      feedClient = new AtomFeedClientFactory().getFeedClient(properties.getLocationFeedUri(), properties);
      feedClient.processFailedEvents();
    } catch (URISyntaxException e) {
    }
  }
}
