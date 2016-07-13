package api.event_queue;

/**
 * Created by chris on 7/1/16.
 */
public interface EventQueueInterfaceChangedListener {
    void removeEventHandler(EventHandler theEventHandler);
    void addEventHandler(EventHandler theEventHandler);

    void removeEventSource(EventSource theEventSource);
    void addEventSource(EventSource theEventSource);
}
