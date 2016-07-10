package event_queue;

import event_queue.service.Service;

/**
 * Created by chris on 7/10/16.
 */
public interface EventQueue {
    void registerService(Service theService);

    void unRegisterService(Service theService);

    void addEventHandler(EventHandler theEventHandler);

    void removeEventHandler(EventHandler theEventHandler);

    void addEventSource(EventSource theEventSource);

    void removeEventSource(EventSource theEventSource);

    void receiveEvent(Event e);
}
