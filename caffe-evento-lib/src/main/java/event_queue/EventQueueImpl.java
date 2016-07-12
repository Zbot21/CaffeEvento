package event_queue;

import event_queue.service.Service;
import event_queue.service.ServiceChangedListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chris on 7/1/16.
 */
public class EventQueueImpl implements ServiceChangedListener, EventSink, EventQueue {

    private List<Service> services = new ArrayList<>();
    private List<EventHandler> eventHandlers = new ArrayList<>();
    private List<EventSource> eventSources = new ArrayList<>();

    @Override
    public void registerService(Service theService) {
        services.add(theService);
        theService.getEventHandlers().forEach(this::addEventHandler);
        theService.getEventSources().forEach(this::addEventSource);
        theService.addServiceChangedListener(this);
    }

    @Override
    public void unRegisterService(Service theService) {
        theService.removeServiceChangedListener(this);
        theService.getEventHandlers().forEach(this::removeEventHandler);
        theService.getEventSources().forEach(this::removeEventSource);
        services.remove(theService);
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
