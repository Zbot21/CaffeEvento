package impl.event_queue;

import api.event_queue.Event;
import api.event_queue.EventHandler;
import api.event_queue.EventSink;
import api.event_queue.EventSource;

/**
 * Created by chris on 7/13/16.
 */
public class EventQueueRemoteInterface extends EventQueueInterfaceImpl implements EventSink {

    @Override
    public void addEventHandler(EventHandler theEventHandler) {
        // TODO: Finish overriding this guy
        super.addEventHandler(theEventHandler);
    }

    @Override
    public void removeEventHandler(EventHandler theEventHandler) {
        // TODO: Finish overriding this guy
        super.removeEventHandler(theEventHandler);
    }

    @Override
    public void addEventSource(EventSource eventSource) {
        eventSource.addListener(this);
        super.addEventSource(eventSource);
    }

    @Override
    public void removeEventSource(EventSource eventSource) {
        eventSource.removeListener(this);
        super.addEventSource(eventSource);
    }

    @Override
    public void receiveEvent(Event e) {
        sendEvent(e);
    }

    private void sendEvent(Event e) {

    }
}
