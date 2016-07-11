package event_queue;

import java.util.UUID;
import java.util.function.Predicate;

/**
 * Created by chris on 7/1/16.
 */
public abstract class EventHandler {
    private UUID eventHandlerID = UUID.randomUUID();
    public abstract Predicate<Event> getHandlerCondition();
    public abstract void handleEvent(Event theEvent);
    public final UUID getEventHandlerId() {
        return eventHandlerID;
    }
}
