package impl.event_queue;

import api.event_queue.Event;
import api.event_queue.EventHandler;
import api.event_queue.EventSink;
import api.event_queue.EventSource;

import java.util.List;

/**
 * Created by chris on 7/13/16.
 */
public class EventQueueRemoteInterface extends EventQueueInterfaceImpl implements EventSink {
    EventSource eventGenerator = new EventSourceImpl();

    public EventQueueRemoteInterface() {
        addEventSource(eventGenerator);
    }

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
        super.removeEventSource(eventSource);
    }

    @Override
    public void receiveEvent(Event e) {
        sendEvent(e);
    }

    private void sendEvent(Event e) {
        // How do we make it so that our handlers do not get triggered twice by the same event?
        // The event handlers could keep a "list" of events handled, and purge it every once in awhile.
    }
}
