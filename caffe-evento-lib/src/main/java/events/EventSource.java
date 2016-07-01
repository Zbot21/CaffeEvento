package events;

/**
 * Created by chris on 7/1/16.
 */
public interface EventSource {
    void addListener(EventSourceListener theEventSourceListener);
}
