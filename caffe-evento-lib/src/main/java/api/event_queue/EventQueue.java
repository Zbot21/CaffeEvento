package api.event_queue;

import impl.event_queue.EventQueueImpl;

/**
 * Created by chris on 7/10/16.
 */
public interface EventQueue extends EventQueueInterfaceChangedListener, EventSink {
    static EventQueueImpl getInstance() {
        return new EventQueueImpl();
    }

    void registerService(EventQueueInterface theEventQueueInterface);

    void unRegisterService(EventQueueInterface theEventQueueInterface);

    void addEventHandler(EventHandler theEventHandler);

    void removeEventHandler(EventHandler theEventHandler);

    void addEventSource(EventSource theEventSource);

    void removeEventSource(EventSource theEventSource);
}
