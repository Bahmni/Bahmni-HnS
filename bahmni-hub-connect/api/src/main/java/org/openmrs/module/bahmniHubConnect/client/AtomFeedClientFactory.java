package org.openmrs.module.bahmniHubConnect.client;

import org.openmrs.module.bahmniHubConnect.workers.EventWorkerFactory;
import org.ict4h.atomfeed.client.AtomFeedProperties;
import org.ict4h.atomfeed.client.repository.AllFailedEvents;
import org.ict4h.atomfeed.client.repository.AllFeeds;
import org.ict4h.atomfeed.client.repository.AllMarkers;
import org.ict4h.atomfeed.client.repository.jdbc.AllFailedEventsJdbcImpl;
import org.ict4h.atomfeed.client.repository.jdbc.AllMarkersJdbcImpl;
import org.ict4h.atomfeed.client.service.AtomFeedClient;
import org.ict4h.atomfeed.client.service.FeedClient;
import org.ict4h.atomfeed.jdbc.JdbcConnectionProvider;
import org.ict4h.atomfeed.transaction.AFTransactionManager;
import org.openmrs.api.context.Context;
import org.openmrs.module.atomfeed.transaction.support.AtomFeedSpringTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

public class AtomFeedClientFactory {

  public FeedClient getFeedClient(String feedURI, AtomFeedProperties atomFeedProperties) throws URISyntaxException {
    URI uri = new URI(feedURI);
    return this.getFeedClient(uri, atomFeedProperties);
  }

  private FeedClient getFeedClient(URI feedURI, AtomFeedProperties atomFeedProperties) {
    AFTransactionManager txMgr = getAtomFeedTransactionManager();
    JdbcConnectionProvider connectionProvider = getConnectionProvider(txMgr);
    return new AtomFeedClient(
       new AllFeeds(atomFeedProperties,null),
        getAllMarkers(connectionProvider),
        getAllFailedEvent(connectionProvider),
        atomFeedProperties,
        txMgr,
        feedURI,
        EventWorkerFactory.getInstance());
  }

  private AllFailedEvents getAllFailedEvent(JdbcConnectionProvider connectionProvider) {
    return new AllFailedEventsJdbcImpl(connectionProvider);
  }

  private AllMarkers getAllMarkers(JdbcConnectionProvider connectionProvider) {
    return new AllMarkersJdbcImpl(connectionProvider);
  }

  private JdbcConnectionProvider getConnectionProvider(AFTransactionManager txMgr) {
    if (txMgr instanceof AtomFeedSpringTransactionManager) {
      return (AtomFeedSpringTransactionManager) txMgr;
    }
    throw new RuntimeException("Atom Feed TransactionManager should provide a connection provider.");
  }

  private AFTransactionManager getAtomFeedTransactionManager() {
    return new AtomFeedSpringTransactionManager(getSpringPlatformTransactionManager());
  }

  private PlatformTransactionManager getSpringPlatformTransactionManager() {
    List<PlatformTransactionManager> platformTransactionManagers = Context.getRegisteredComponents(PlatformTransactionManager.class);
    return platformTransactionManagers.get(0);
  }


}
