package api.event_queue;

import api.services.Service;
import impl.event_queue.SynchronousEventQueue;

/**
 * Created by chris on 7/10/16.
 */
public interface EventQueue extends EventQueueInterfaceChangedListener, EventSink {

    void registerService(Service theService);

    void unRegisterService(Service theService);

    void addEventQueueInterface(EventQueueInterface theEventQueueInterface);

    void removeEventQueueInterface(EventQueueInterface theEventQueueInterface);
}
