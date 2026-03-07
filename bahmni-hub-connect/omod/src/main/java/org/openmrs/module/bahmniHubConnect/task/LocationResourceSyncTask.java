/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at https://www.bahmni.org/license/mplv2hd.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.bahmniHubConnect.task;

import org.openmrs.module.bahmniHubConnect.BahmniResourceSyncProperties;
import org.openmrs.module.bahmniHubConnect.client.AtomFeedClientFactory;
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
