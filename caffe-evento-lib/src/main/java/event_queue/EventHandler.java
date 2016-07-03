package event_queue;

import java.util.function.Predicate;

/**
 * Created by chris on 7/1/16.
 */
public interface EventHandler {
    Predicate<Event> getHandlerCondition();
    void handleEvent(Event theEvent);
}
