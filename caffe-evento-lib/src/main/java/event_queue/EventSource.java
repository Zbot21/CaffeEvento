package event_queue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by chris on 7/1/16.
 */
public abstract class EventSource {

    private List<EventSink> eventSinks = new ArrayList<>();

    public void addListener(EventSink theEventSink) {
        eventSinks.add(theEventSink);
    }

    public void removeListener(EventSink theEventSink) {
        eventSinks.remove(theEventSink);
    }

    protected void registerEvent(Event theEvent) {
        eventSinks.forEach(eventSink -> eventSink.receiveEvent(theEvent));
    }
}
