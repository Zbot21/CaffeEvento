package event_queue;

import com.google.gson.annotations.Expose;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Created by chris on 7/1/16.
 */
public abstract class EventHandler {
    private UUID eventHandlerID = UUID.randomUUID();
    private EventHandlerBuilder builder;

    @Expose(serialize = false, deserialize = false)
    protected Log log;

    public final UUID getEventHandlerId() {
        return eventHandlerID;
    }

    private EventHandler() {
        log = LogFactory.getLog(getClass());
        builder = new EventHandlerBuilder();
    }
    public static EventHandlerBuilder create() {
        return new EventHandlerBuilder();
    }

    public final EventHandler getFromBuilder() {
        return builder.build(eventHandlerID);
    }

    public abstract Predicate<Event> getHandlerCondition();
    public abstract void handleEvent(Event theEvent);

    protected void sendHttpEvent(String destination, Event theEvent) {
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

    protected void sendSocketEvent(String destination, Event theEvent) {
        // TODO: Implement this
        log.error("Could not send socket event because socket events are not implemented.");
        throw new NotImplementedException();
    }

    /**
     * Created by chris on 7/11/16.
     */
    public static class EventHandlerBuilder {
        private EventHandlerBuilder() {}

        private String eventName = null;
        private String eventType = null;
        private String httpEventReceiver = null;
        private String socketEventReceiver = null;
        private Map<String, String> eventData = new HashMap<>();

        @Expose(serialize = false, deserialize = false)
        private Consumer<Event> consumer = null;

        public EventHandlerBuilder eventName(String eventName) {
            this.eventName = eventName;
            return this;
        }

        public EventHandlerBuilder eventType(String eventType) {
            this.eventType = eventType;
            return this;
        }

        public EventHandlerBuilder eventData(String key, String value) {
            this.eventData.put(key, value);
            return this;
        }

        public EventHandlerBuilder httpEventReceiver(String url) {
            this.httpEventReceiver = url;
            return this;
        }

        public EventHandlerBuilder socketEventReceiver(String url) {
            this.socketEventReceiver = url;
            return this;
        }

        public EventHandlerBuilder eventHandler(Consumer<Event> consumer) {
            this.consumer = consumer;
            return this;
        }

        public EventHandler build(UUID id) {
            EventHandler handler =  new EventHandler() {
                @Override
                public Predicate<Event> getHandlerCondition() {
                    List<Predicate<Event>> predicates = new ArrayList<>();

                    Optional.ofNullable(eventName).map(name -> (Predicate<Event>) event -> event.getEventName().equals(name)).ifPresent(predicates::add);
                    Optional.ofNullable(eventType).map(type -> (Predicate<Event>) event -> event.getEventType().equals(type)).ifPresent(predicates::add);
                    eventData.entrySet().stream().forEach(entry -> predicates.add(event -> Optional.ofNullable(event.getEventField(entry.getKey()))
                            .map(field -> field.equals(entry.getValue())).orElse(false))
                    );

                    return predicates.stream().reduce(event -> true, Predicate::and);
                }

                @Override
                public void handleEvent(Event theEvent) {
                    Optional.ofNullable(consumer).ifPresent(c -> c.accept(theEvent));
                    Optional.ofNullable(httpEventReceiver).ifPresent(dest -> sendHttpEvent(dest, theEvent));
                    Optional.ofNullable(socketEventReceiver).ifPresent(dest -> sendSocketEvent(dest, theEvent));
                }
            };
            handler.eventHandlerID = id;
            return handler;
        }

        public EventHandler build() {
            return build(UUID.randomUUID());
        }
    }
}
