package api.event_queue;

/**
 * Created by chris on 7/1/16.
 */
public interface EventSink {
    void receiveEvent(Event e);
}
