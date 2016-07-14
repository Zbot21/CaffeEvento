package api.event_queue;

import api.service.Service;
import impl.event_queue.EventQueueImpl;

/**
 * Created by chris on 7/10/16.
 */
public interface EventQueue extends EventQueueInterfaceChangedListener, EventSink {
    static EventQueueImpl getInstance() {
        return new EventQueueImpl();
    }

    void registerService(Service theService);

    void unRegisterService(Service theService);

    void addEventQueueInterface(EventQueueInterface theEventQueueInterface);

    void removeEventQueueInterface(EventQueueInterface theEventQueueInterface);
}
