package org.bahmni.hns.bahmniResourceSyncClient.workers;

import org.ict4h.atomfeed.client.domain.Event;
import org.ict4h.atomfeed.client.service.EventWorker;

import java.util.HashMap;
import java.util.Map;

public class EventWorkerFactory implements EventWorker {
  private Map<String, EventWorker> workers = new HashMap<String, EventWorker>();
  private static EventWorkerFactory eventWorkerFactory;

  private EventWorkerFactory() {
    workers.put("location", new LocationEventWorker());
    //TODO: Fix Hardcoding, find better way of adding worker
  }

  public static EventWorkerFactory getInstance() {
    if (eventWorkerFactory == null) {
      eventWorkerFactory = new EventWorkerFactory();
    }
    return eventWorkerFactory;
  }

  public EventWorker getEventWorker(Event event) {
    return workers.get(event.getTitle().toLowerCase());
  }

  public void process(Event event) {
    getEventWorker(event).process(event);
  }

  public void cleanUp(Event event) {
    getEventWorker(event).cleanUp(event);

  }
}
