package org.openmrs.module.bahmniHubConnect.workers;

import org.ict4h.atomfeed.client.domain.Event;
import org.ict4h.atomfeed.client.service.EventWorker;

import java.util.HashMap;
import java.util.Map;

public class EventWorkerFactory implements EventWorker {
  private Map<String, EventWorker> workers = new HashMap<>();
  private static EventWorkerFactory eventWorkerFactory;

  private EventWorkerFactory() {
   registerWorker("location", new LocationEventWorker());
    //TODO: Fix Hardcoding, find better way of adding worker
  }

  public static EventWorkerFactory getInstance() {
    if (eventWorkerFactory == null) {
      eventWorkerFactory = new EventWorkerFactory();
    }
    return eventWorkerFactory;
  }

  public void registerWorker(String title, EventWorker worker) {
    workers.put(title, worker);
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
