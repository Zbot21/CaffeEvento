package test_util;

import event_queue.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Created by chris on 7/9/16.
 */
public class EventCollector extends EventHandler {
    private List<Event> collectedEvents = new ArrayList<>();

    @Override
    public Predicate<Event> getHandlerCondition() {
        return e -> true;
    }

    @Override
    public void handleEvent(Event theEvent) {
        collectedEvents.add(theEvent);
    }

    public List<Event> findEventsWithName(String name) {
        return collectedEvents.stream().filter(e -> e.getEventName().equals(name)).collect(Collectors.toList());
    }

    public List<Event> findEventsWithType(String type) {
        return collectedEvents.stream().filter(e -> e.getEventType().equals(type)).collect(Collectors.toList());
    }

    public List<Event> getCollectedEvents() {
        return new ArrayList<>(collectedEvents);
    }

    public void printState() {
        getCollectedEvents().stream().map(Event::encodeEvent).forEach(System.out::println);
    }
}
