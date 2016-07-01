package events;

import java.util.Map;
import java.util.HashMap;

/**
 * Created by chris on 7/1/16.
 */
public class Event {
    private String eventName;
    private Map<String, String> eventDetails;

    public Event(){
        eventName = null;
        eventDetails = new HashMap<>();
    }

    public Event(String eventName){
        this.eventName = eventName; 
        eventDetails = new HashMap<>();
    }

    public String getEventName(){
        return this.eventName;
    }

    public void setEventName(String name){
        this.eventName = name;
    }

    public void setEventField(String field, String value) {
        eventDetails.put(field, value);
    }
    public String getEventField(String field) {
        return eventDetails.get(field);
    }
}
