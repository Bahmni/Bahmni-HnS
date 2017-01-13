package org.bahmni.hns.bahmniResourceSyncClient;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ict4h.atomfeed.client.AtomFeedProperties;
import org.openmrs.util.OpenmrsUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class BahmniResourceSyncProperties extends AtomFeedProperties {

  private static Log log = LogFactory.getLog(BahmniResourceSyncProperties.class);

  private static final String SYNC_SERVER_BASE_URI = "sync.server.baseUri";
  private static final String CONNECT_TIMEOUT = "feed.connectionTimeoutInMilliseconds";
  private static final String MAX_FAILED_EVENTS = "feed.maxFailedEvents";
  private static final String READ_TIMEOUT = "feed.replyTimeoutInMilliseconds";
  private static final String LOCATION_FEED_URI = "feed.location.uri";
  private static final String FHIR_BASE_URL = "fhir.base.url";

  private static BahmniResourceSyncProperties bahmniResourceSyncProperties;
  private Properties properties;

  private BahmniResourceSyncProperties() {
    String propertyFile = new File(OpenmrsUtil.getApplicationDataDirectory(), "bahmniResourceSync.properties").getAbsolutePath();
    log.info(String.format("Reading bahmni resource sync properties from : %s", propertyFile));
    try {
      properties = new Properties(System.getProperties());
      properties.load(new FileInputStream(propertyFile));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static BahmniResourceSyncProperties getInstance() {
    if (bahmniResourceSyncProperties == null) {
      bahmniResourceSyncProperties = new BahmniResourceSyncProperties();
    }
    return bahmniResourceSyncProperties;
  }

  @Override
  public int getMaxFailedEvents() {
    return Integer.parseInt(getProperty(MAX_FAILED_EVENTS));
  }

  @Override
  public int getReadTimeout() {
    return Integer.parseInt(getProperty(READ_TIMEOUT));
  }

  @Override
  public int getConnectTimeout() {
    return Integer.parseInt(getProperty(CONNECT_TIMEOUT));
  }

  public String getLocationFeedUri() {
    return getSyncServerBaseUri() + getProperty(LOCATION_FEED_URI);
  }
  public String getFhirBaseUrl() {
    return  getProperty(FHIR_BASE_URL);
  }

  private String getProperty(String key) {
    return properties.getProperty(key);
  }

  private String getSyncServerBaseUri() {
    return getProperty(SYNC_SERVER_BASE_URI);
  }
}
