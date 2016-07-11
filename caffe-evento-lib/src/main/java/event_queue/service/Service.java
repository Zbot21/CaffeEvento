package event_queue.service;

import event_queue.EventHandler;
import event_queue.EventSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Created by chris on 7/1/16.
 */
public abstract class Service {

    private List<ServiceChangedListener> serviceChangedListeners = new ArrayList<>();
    private List<EventSource> eventSources = new ArrayList<>();
    private List<EventHandler> eventHandlers = new ArrayList<>();

    protected Log log;

    public List<EventSource> getEventSources() {
        return new ArrayList<>(eventSources);
    }

    public List<EventHandler> getEventHandlers() {
        return new ArrayList<>(eventHandlers);
    }

    public Service() {
        log = LogFactory.getLog(getClass());
    }

    public void addServiceChangedListener(ServiceChangedListener theServiceChangedListener) {
        serviceChangedListeners.add(theServiceChangedListener);
    }

    public void removeServiceChangedListener(ServiceChangedListener theServiceChangedListener) {
        serviceChangedListeners.remove(theServiceChangedListener);
    }

    public final void removeEventSource(UUID id) {
        eventSources.stream().filter(s -> s.getEventSourceId().equals(id)).findFirst()
                .ifPresent(this::removeEventSource);
    }

    protected void addEventSource(EventSource theEventSource) {
        eventSources.add(theEventSource);
        updateServiceChangedListeners(l -> l.addEventSource(theEventSource));
    }

    protected void removeEventSource(EventSource theEventSource) {
        eventSources.remove(theEventSource);
        updateServiceChangedListeners(l -> l.removeEventSource(theEventSource));
    }

    public final void removeEventHandler(UUID id) {
        eventHandlers.stream().filter(h -> h.getEventHandlerId().equals(id)).findFirst()
                .ifPresent(this::removeEventHandler);
    }

    protected void addEventHandler(EventHandler theEventHandler) {
        eventHandlers.add(theEventHandler);
        updateServiceChangedListeners(l -> l.addEventHandler(theEventHandler));
    }

    protected void removeEventHandler(EventHandler theEventHandler) {
        eventHandlers.remove(theEventHandler);
        updateServiceChangedListeners(l -> l.removeEventHandler(theEventHandler));
    }

    private void updateServiceChangedListeners(Consumer<ServiceChangedListener> serviceChange) {
        serviceChangedListeners.forEach(serviceChange);
    }
}
