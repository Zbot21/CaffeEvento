package api.event_queue;

/**
 * Created by chris on 7/10/16.
 */
public interface EventQueue extends EventQueueInterfaceChangedListener {
    void registerService(EventQueueInterface theEventQueueInterface);

    void unRegisterService(EventQueueInterface theEventQueueInterface);

    void addEventHandler(EventHandler theEventHandler);

    void removeEventHandler(EventHandler theEventHandler);

    void addEventSource(EventSource theEventSource);

    void removeEventSource(EventSource theEventSource);

    void receiveEvent(Event e);
}
