package services;

import events.EventHandler;
import events.EventSource;

/**
 * Created by chris on 7/1/16.
 */
public interface ServiceChangedListener {
    void removedEventHandler(EventHandler theEventHandler);
    void addedEventHandler(EventHandler theEventHandler);

    void removedEventSource(EventSource theEventSource);
    void addedEventSource(EventSource theEventSource);
}
