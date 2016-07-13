package api.event_queue;

import java.util.UUID;

/**
 * Created by chris on 7/13/16.
 */
public interface EventSource {
    void addListener(EventQueue theEventSink);

    void removeListener(EventQueue theEventSink);

    void registerEvent(Event theEvent);

    UUID getEventSourceId();
}
