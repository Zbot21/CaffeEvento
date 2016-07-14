package impl.event_queue;

import api.event_queue.*;
import lib.AutoRotatedLogger;
import lib.RotatedLogger;

import java.util.UUID;

/**
 * Created by chris on 7/13/16.
 */
public class BufferedEventQueueInterface extends EventQueueInterfaceImpl implements EventSink{
    private EventSource internalEventGenerator = new EventSourceImpl();
    private EventSource externalEventGenerator = new EventSourceImpl();
    private RotatedLogger<UUID> eventLogger;
    private EventQueue bufferEventQueue;

    public BufferedEventQueueInterface() {
        this(new SynchronousEventQueue());

    }

    BufferedEventQueueInterface(EventQueue internalEventQueue) {
        this.bufferEventQueue = internalEventQueue;
        this.eventLogger = new AutoRotatedLogger<>();

        // Forward all external events to the internal event queue
        internalEventQueue.addEventSource(internalEventGenerator);

        // Add an event handler that only registers the event if we don't already have it
        super.addEventHandler(EventHandler.create().eventHandler(e -> {
            if(!eventLogger.contains(e.getEventId())) {
                internalEventGenerator.registerEvent(e);
            }
        }).build());

        // Register an external event generator to send events to external things (might not be used in standalone)
        super.addEventSource(externalEventGenerator);
    }

    @Override
    public void addEventHandler(EventHandler theEventHandler) {
        bufferEventQueue.addEventHandler(theEventHandler);
    }

    @Override
    public void removeEventHandler(EventHandler theEventHandler) {
        bufferEventQueue.removeEventHandler(theEventHandler);
    }

    @Override
    public void addEventSource(EventSource eventSource) {
        eventSource.addListener(this);
        bufferEventQueue.addEventSource(eventSource);
    }

    @Override
    public void removeEventSource(EventSource eventSource) {
        eventSource.addListener(this);
        bufferEventQueue.addEventSource(eventSource);
    }

    @Override
    public void receiveEvent(Event e) {
        eventLogger.add(e.getEventId());
        internalEventGenerator.registerEvent(e);
        externalEventGenerator.registerEvent(e);
    }
}
