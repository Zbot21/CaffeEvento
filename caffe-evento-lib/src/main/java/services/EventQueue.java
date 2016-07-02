package services;

import events.EventHandler;
import events.EventSource;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chris on 7/1/16.
 */
public class EventQueue implements ServiceChangedListener {

    private List<Service> services = new ArrayList<>();
    private List<EventHandler> eventHandlers = new ArrayList<>();
    private List<EventSource> eventSources = new ArrayList<>();

    public void registerService(Service theService){
        services.add(theService);
        eventHandlers.addAll(theService.getEventHandlers());
        eventSources.addAll(theService.getEventSources());
        theService.addServiceChangedListener(this);
    }

    public void unRegisterService(Service theService){
        theService.removeServiceChangedListener(this);
        eventHandlers.removeAll(theService.getEventHandlers());
        eventSources.removeAll(theService.getEventSources());
        services.remove(theService);
    }

    @Override
    public void addedEventHandler(EventHandler theEventHandler) {
        eventHandlers.add(theEventHandler);
    }

    @Override
    public void removedEventHandler(EventHandler theEventHandler) {
        eventHandlers.remove(theEventHandler);
    }

    @Override
    public void addedEventSource(EventSource theEventSource) {
        eventSources.add(theEventSource);
    }

    @Override
    public void removedEventSource(EventSource theEventSource) {
        eventSources.remove(theEventSource);
    }
}
