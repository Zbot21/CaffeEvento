package api.event_queue;

import com.google.gson.GsonBuilder;
import impl.event_queue.EventHandlerImpl;

import java.util.UUID;
import java.util.function.Predicate;

/**
 * Created by chris on 7/13/16.
 */
public interface EventHandler {
    UUID getEventHandlerId();
    Predicate<Event> getHandlerCondition();
    void handleEvent(Event theEvent);

    static EventHandler fromJson(String json) {
        return EventHandlerImpl.fromJson(json);
    }

    static EventHandlerImpl.EventHandlerBuilder create() {
        return EventHandlerImpl.create();
    }
}
