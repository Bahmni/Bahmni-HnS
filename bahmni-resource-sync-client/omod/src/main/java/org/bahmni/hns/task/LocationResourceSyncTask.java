package org.bahmni.hns.task;

import org.bahmni.hns.bahmniResourceSyncClient.BahmniResourceSyncProperties;
import org.bahmni.hns.bahmniResourceSyncClient.client.AtomFeedClientFactory;
import org.ict4h.atomfeed.client.service.FeedClient;
import org.openmrs.scheduler.tasks.AbstractTask;

import java.net.URISyntaxException;

public class LocationResourceSyncTask extends AbstractTask {


  @Override
  public void execute() {
    BahmniResourceSyncProperties properties = BahmniResourceSyncProperties.getInstance();
    FeedClient feedClient;
    try {
      feedClient = new AtomFeedClientFactory().getFeedClient(properties.getLocationFeedUri(), properties);
      feedClient.processEvents();
    } catch (URISyntaxException e) {
    }
  }
}
