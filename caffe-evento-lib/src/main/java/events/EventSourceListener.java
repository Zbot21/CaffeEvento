package events;

/**
 * Created by chris on 7/1/16.
 */
public interface EventSourceListener {
    void newEvent(Event theEvent);
}
