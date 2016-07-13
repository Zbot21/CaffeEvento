package api.event_queue;

import com.google.gson.GsonBuilder;

import java.io.Reader;
import java.util.Map;
import java.util.UUID;

/**
 * Created by chris on 7/13/16.
 */
public interface Event {

    String getEventName();

    void setEventName(String name);

    String getEventType();

    void setEventType(String type);

    void setEventField(String field, String value);

    String getEventField(String field);

    UUID getEventId();

    Map<String, String> getEventDetails();

    String encodeEvent();
}
