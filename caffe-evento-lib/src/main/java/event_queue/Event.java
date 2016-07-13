package event_queue;

import com.google.gson.GsonBuilder;

import java.io.Reader;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;
import java.util.UUID;

/**
 * Created by chris on 7/1/16.
 */
public class Event {
    private String eventName;
    private String eventType;
    private UUID eventId = UUID.randomUUID();
    private Map<String, String> eventDetails = new HashMap<>();
    private Date timestamp = Date.from(Instant.now());

    public Event(){
        eventName = null;
        eventType = null;
    }

    public Event(String eventName, String eventType){
        this.eventName = eventName;
        this.eventType = eventType;
    }

    public String getEventName(){
        return this.eventName;
    }
    public void setEventName(String name){
        this.eventName = name;
    }

    public String getEventType() { return this.eventType; }
    public void setEventType(String type) { this.eventType = type; }

    public void setEventField(String field, String value) {
        eventDetails.put(field, value);
    }
    public String getEventField(String field) {
        return eventDetails.get(field);
    }

    public UUID getEventId() {
        return eventId;
    }

    public Map<String, String> getEventDetails() {
        return new HashMap<>(eventDetails);
    }

    public String encodeEvent() {
        return (new GsonBuilder()).create().toJson(this);
    }

    public static Event decodeEvent(String theEvent) {
        return (new GsonBuilder()).create().fromJson(theEvent, Event.class);
    }

    public static Event decodeEvent(Reader theEvent) {
        return (new GsonBuilder()).create().fromJson(theEvent, Event.class);
    }
}
