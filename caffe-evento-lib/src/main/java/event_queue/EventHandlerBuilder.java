package event_queue;

import com.google.gson.JsonElement;
import com.google.gson.annotations.Expose;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Created by chris on 7/11/16.
 */
public class EventHandlerBuilder {
    private EventHandlerBuilder() {}

    private String eventName = null;
    private String eventType = null;
    private String httpEventReceiver = null;
    private String socketEventReceiver = null;
    private Map<String, String> eventData = new HashMap<>();

    @Expose(serialize = false, deserialize = false)
    private Consumer<Event> consumer = null;

    public static EventHandlerBuilder create() {
        return new EventHandlerBuilder();
    }

    public EventHandlerBuilder eventName(String eventName) {
        this.eventName = eventName;
        return this;
    }

    public EventHandlerBuilder eventType(String eventType) {
        this.eventType = eventType;
        return this;
    }

    public EventHandlerBuilder eventData(String key, String value) {
        this.eventData.put(key, value);
        return this;
    }

    public EventHandlerBuilder httpEventReceiver(String url) {
        this.httpEventReceiver = url;
        return this;
    }

    public EventHandlerBuilder socketEventReceiver(String url) {
        this.socketEventReceiver = url;
        return this;
    }

    public EventHandlerBuilder eventHandler(Consumer<Event> consumer) {
        this.consumer = consumer;
        return this;
    }

    public EventHandler build() {
        return new EventHandler() {
            @Override
            public Predicate<Event> getHandlerCondition() {
                List<Predicate<Event>> predicates = new ArrayList<>();

                Optional.ofNullable(eventName).map(name -> (Predicate<Event>) event -> event.getEventName().equals(name)).ifPresent(predicates::add);
                Optional.ofNullable(eventType).map(type -> (Predicate<Event>) event -> event.getEventType().equals(type)).ifPresent(predicates::add);
                Optional.ofNullable(eventData).map(Map::entrySet).map(Collection::stream).ifPresent(stream ->
                    stream.forEach(entry -> predicates.add(event ->
                        Optional.ofNullable(event.getEventField(entry.getKey())).map(field -> field.equals(entry.getValue())).orElse(false)
                    ))
                );

                return predicates.stream().reduce(event -> true, Predicate::and);
            }

            @Override
            public void handleEvent(Event theEvent) {
                Optional.ofNullable(consumer).ifPresent(c -> c.accept(theEvent));
                Optional.ofNullable(httpEventReceiver).ifPresent(dest -> sendHttpEvent(dest, theEvent));
                Optional.ofNullable(socketEventReceiver).ifPresent(dest -> sendSocketEvent(dest, theEvent));
            }
        };
    }


}
