package api.utils;

import api.event_queue.Event;
import impl.event_queue.EventImpl;

/**
 * Created by chris on 7/12/16.
 */
public class EventBuilder {
    private EventBuilder() {
        this.event = new EventImpl();
    }
    private Event event;

    public static EventBuilder create() {
        return new EventBuilder();
    }

    public EventBuilder name(String name) {
        this.event.setEventName(name);
        return this;
    }

    public EventBuilder type(String type) {
        this.event.setEventType(type);
        return this;
    }

    public EventBuilder data(String key, String value) {
        this.event.setEventField(key, value);
        return this;
    }

    public Event build() {
        return this.event;
    }
}
