package impl.event_queue;

import api.event_queue.Event;

import java.util.function.Predicate;

/**
 * Created by chris on 7/14/16.
 */
public class FilteredEventQueue extends SynchronousEventQueue {
    private Predicate<Event> eventAcceptCriteria;

    public FilteredEventQueue() {
        this(e -> true);
    }

    public FilteredEventQueue(Predicate<Event> acceptCriteria) {
        this.eventAcceptCriteria = acceptCriteria;
    }

    @Override
    public synchronized void receiveEvent(Event e) {
        if(eventAcceptCriteria.test(e)) super.receiveEvent(e);
    }


}
