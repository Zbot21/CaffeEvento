package api.service;

import api.event_queue.EventQueueInterface;

/**
 * Created by chris on 7/13/16.
 */
public interface Service {
    EventQueueInterface getEventQueueInterface();
}
