package services;

import events.Event;
import events.EventHandler;
import events.EventSink;
import events.EventSource;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chris on 7/1/16.
 */
public class EventQueue implements ServiceChangedListener, EventSink {

    private List<Service> services = new ArrayList<>();
    private List<EventHandler> eventHandlers = new ArrayList<>();
    private List<EventSource> eventSources = new ArrayList<>();

    public void registerService(Service theService){
        services.add(theService);
        theService.getEventHandlers().forEach(this::addEventHandler);
        theService.getEventSources().forEach(this::addEventSource);
        theService.addServiceChangedListener(this);
    }

    public void unRegisterService(Service theService){
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
    }

    @Override
    public void removeEventSource(EventSource theEventSource) {
        eventSources.remove(theEventSource);
    }

    @Override
    public void receiveEvent(Event e) {

    }
}
