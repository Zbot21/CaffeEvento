package api.event_queue;

import java.util.UUID;

/**
 * Created by chris on 7/13/16.
 */
public interface EventSource {
    void addListener(EventQueue.EventSink theEventSink);

    void removeListener(EventQueue.EventSink theEventSink);

    void registerEvent(Event theEvent);

    UUID getEventSourceId();
}
