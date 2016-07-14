package impl.event_queue;

import api.event_queue.Event;
import api.event_queue.EventHandler;
import com.google.gson.GsonBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * This has to be abstract in order to allow for transparent serialization over the network.
 * Created by chris on 7/1/16.
 */
public final class EventHandlerImpl implements EventHandler {
    private EventHandlerData eventHandlerData = new EventHandlerData();
    private List<Consumer<Event>> eventConsumers = new ArrayList<>();
    private Log log = LogFactory.getLog(getClass());

    private EventHandlerImpl() {
    } // Haha, nobody can create me now!

    @Override
    public final UUID getEventHandlerId() {
        return eventHandlerData.getEventHandlerId();
    }

    @Override
    public String encodeToJson() {
        return new GsonBuilder().create().toJson(eventHandlerData);
    }

    @Override
    public void handleEvent(Event theEvent) {
        if (eventConsumers.size() == 0) {
            log.info("No event consumers registered for event handler with id: " + eventHandlerData.getEventHandlerId());
        }
        eventConsumers.forEach(c -> c.accept(theEvent));
    }

    @Override
    public Predicate<Event> getHandlerCondition() {
        List<Predicate<Event>> predicates = new ArrayList<>();

        Optional.ofNullable(eventHandlerData.eventName).map(name -> (Predicate<Event>) event -> event.getEventName().equals(name)).ifPresent(predicates::add);
        Optional.ofNullable(eventHandlerData.eventType).map(type -> (Predicate<Event>) event -> event.getEventType().equals(type)).ifPresent(predicates::add);
        eventHandlerData.eventData.entrySet().stream().forEach(entry -> predicates.add(event -> Optional.ofNullable(event.getEventField(entry.getKey()))
                .map(field -> field.equals(entry.getValue())).orElse(false))
        );

        return predicates.stream().reduce(event -> true, Predicate::and);
    }

    private void sendHttpEvent(URI destination, Event theEvent) {
        HttpClient client = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(destination);
        try {
            post.addHeader("content-type", "text/json");
            post.setEntity(new StringEntity(theEvent.encodeEvent()));
            client.execute(post);
        } catch (IOException e) {
            log.error("There was an error sending the event.", e);
        }
    }

    public static EventHandler fromJson(String json) {
        return decodeFromEventData(new GsonBuilder().create().fromJson(json, EventHandlerData.class));
    }

    public static EventHandlerBuilder create() {
        return new EventHandlerBuilder();
    }

    private static EventHandler decodeFromEventData(EventHandlerData theEventHandlerData) {
        EventHandlerImpl handler = new EventHandlerImpl();
        Optional.ofNullable(theEventHandlerData.httpEventReceiver).map(URI::create)
                .ifPresent(uri -> handler.addEventConsumer(event -> handler.sendHttpEvent(uri, event)));
        return handler;
    }

    private void addEventConsumer(Consumer<Event> eventConsumer) {
        this.eventConsumers.add(eventConsumer);
    }

    private static class EventHandlerData {
        private UUID eventHandlerId = UUID.randomUUID();
        private String eventName;
        private String eventType;
        private String httpEventReceiver;
        private String socketEventReceiver;
        private Map<String, String> eventData = new HashMap<>();

        private EventHandlerData() {
        }

        UUID getEventHandlerId() {
            return eventHandlerId;
        }

        void eventName(String eventName) {
            this.eventName = eventName;
        }

        void eventType(String eventType) {
            this.eventType = eventType;
        }

        void httpEventReceiver(String url) {
            this.httpEventReceiver = url;
        }

        void eventData(String key, String value) {
            this.eventData.put(key, value);
        }
    }

    /**
     * Created by chris on 7/11/16.
     */
    public static class EventHandlerBuilder {
        private EventHandlerData eventHandlerData = new EventHandlerData();
        private List<Consumer<Event>> eventConsumers = new ArrayList<>();

        private EventHandlerBuilder() {
        }

        public EventHandler build() {
            EventHandlerImpl handler = new EventHandlerImpl();
            handler.eventHandlerData = eventHandlerData;
            eventConsumers.forEach(handler::addEventConsumer);
            return handler;
        }

        public EventHandlerBuilder eventName(String name) {
            eventHandlerData.eventName(name);
            return this;
        }

        public EventHandlerBuilder eventType(String type) {
            eventHandlerData.eventType(type);
            return this;
        }

        public EventHandlerBuilder httpEventReceiver(String httpEventReceiver) {
            eventHandlerData.httpEventReceiver(httpEventReceiver);
            return this;
        }

        public EventHandlerBuilder eventData(String key, String value) {
            eventHandlerData.eventData(key, value);
            return this;
        }

        public EventHandlerBuilder eventHandler(Consumer<Event> consumer) {
            eventConsumers.add(consumer);
            return this;
        }
    }
}
