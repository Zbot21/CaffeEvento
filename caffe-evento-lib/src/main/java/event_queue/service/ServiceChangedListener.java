package event_queue.service;

import event_queue.EventHandler;
import event_queue.EventSource;

/**
 * Created by chris on 7/1/16.
 */
public interface ServiceChangedListener {
    void removeEventHandler(EventHandler theEventHandler);
    void addEventHandler(EventHandler theEventHandler);

    void removeEventSource(EventSource theEventSource);
    void addEventSource(EventSource theEventSource);
}
