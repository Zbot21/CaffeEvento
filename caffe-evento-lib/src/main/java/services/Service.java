package services;

import events.EventHandler;
import events.EventSource;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Created by chris on 7/1/16.
 */
public abstract class Service {

    private List<ServiceChangedListener> serviceChangedListeners = new ArrayList<>();
    private List<EventSource> eventSources = new ArrayList<>();
    private List<EventHandler> eventHandlers = new ArrayList<>();

    public List<EventSource> getEventSources() {
        return new ArrayList<>(eventSources);
    }

    public List<EventHandler> getEventHandlers() {
        return new ArrayList<>(eventHandlers);
    }

    public void addServiceChangedListener(ServiceChangedListener theServiceChangedListener) {
        serviceChangedListeners.add(theServiceChangedListener);
    }

    public void removeServiceChangedListener(ServiceChangedListener theServiceChangedListener) {
        serviceChangedListeners.remove(theServiceChangedListener);
    }

    protected void addEventSource(EventSource theEventSource) {
        eventSources.add(theEventSource);
        updateServiceChangedListeners(l -> l.addedEventSource(theEventSource));
    }

    protected void removeEventSource(EventSource theEventSource) {
        eventSources.remove(theEventSource);
        updateServiceChangedListeners(l -> l.removedEventSource(theEventSource));
    }

    protected void addEventHandler(EventHandler theEventHandler) {
        eventHandlers.add(theEventHandler);
        updateServiceChangedListeners(l -> l.addedEventHandler(theEventHandler));
    }

    protected void removeEventHandler(EventHandler theEventHandler) {
        eventHandlers.remove(theEventHandler);
        updateServiceChangedListeners(l -> l.removedEventHandler(theEventHandler));
    }

    private void updateServiceChangedListeners(Consumer<ServiceChangedListener> serviceChange) {
        serviceChangedListeners.forEach(serviceChange);
    }
}
