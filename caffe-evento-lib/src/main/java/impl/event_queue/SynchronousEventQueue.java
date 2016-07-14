package impl.event_queue;

import api.event_queue.*;
import api.event_queue.EventQueueInterface;
import api.event_queue.EventSource;
import api.services.Service;
import lib.RotatedLogger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chris on 7/1/16.
 */
public class SynchronousEventQueue implements EventQueue {

    private List<EventQueueInterface> eventQueueInterfaces = new ArrayList<>();
    private List<EventHandler> eventHandlers = new ArrayList<>();
    private List<EventSource> eventSources = new ArrayList<>();

    public SynchronousEventQueue() {
    }

    @Override
    public void registerService(Service theService) {
        addEventQueueInterface(theService.getEventQueueInterface());
    }

    @Override
    public void addEventQueueInterface(EventQueueInterface theEventQueueInterface) {
        eventQueueInterfaces.add(theEventQueueInterface);
        theEventQueueInterface.getEventHandlers().forEach(this::addEventHandler);
        theEventQueueInterface.getEventSources().forEach(this::addEventSource);
        theEventQueueInterface.addEventQueueInterfaceChangedListener(this);
    }

    @Override
    public void unRegisterService(Service theService) {
        removeEventQueueInterface(theService.getEventQueueInterface());

    }

    @Override
    public void removeEventQueueInterface(EventQueueInterface theEventQueueInterface) {
        theEventQueueInterface.removeEventQueueInterfaceChangedListener(this);
        theEventQueueInterface.getEventHandlers().forEach(this::removeEventHandler);
        theEventQueueInterface.getEventSources().forEach(this::removeEventSource);
        eventQueueInterfaces.remove(theEventQueueInterface);
    }

    @Override
    public void addEventHandler(EventHandler theEventHandler) {
        eventHandlers.add(theEventHandler);
    }

    @Override
    public void removeEventHandler(EventHandler theEventHandler) {
        eventHandlers.remove(theEventHandler);
    }

    @Override
    public void addEventSource(EventSource theEventSource) {
        eventSources.add(theEventSource);
        theEventSource.addListener(this);
    }

    @Override
    public void removeEventSource(EventSource theEventSource) {
        eventSources.remove(theEventSource);
        theEventSource.removeListener(this);
    }

    @Override
    public synchronized void receiveEvent(Event e) {
        List<EventHandler> tempEventHandlers = new ArrayList<>(eventHandlers);
        tempEventHandlers.stream()
                .filter(handler -> handler.getHandlerCondition().test(e))
                .forEach(handler -> handler.handleEvent(e));
    }
}
