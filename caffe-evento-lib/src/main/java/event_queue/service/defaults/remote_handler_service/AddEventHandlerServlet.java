package event_queue.service.defaults.remote_handler_service;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import event_queue.Event;
import event_queue.EventHandler;
import event_queue.service.Service;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Created by chris on 7/11/16.
 */
public class AddEventHandlerServlet extends ServiceServlet {
    public AddEventHandlerServlet(Service service) {
        super(service);
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        EventHandler handler = eventHandlerFromJson(new JsonParser().parse(req.getReader()).getAsJsonObject());
        service.addEventHandler(handler);
        res.getWriter().write(handler.getEventHandlerId().toString()); // Return the event handler id
    }

    private EventHandler eventHandlerFromJson(JsonObject eventHandlerData) {
        return new EventHandler() {
            @Override
            public Predicate<Event> getHandlerCondition() {
                Predicate<Event> condition = e -> true; // Start as an always true condition
                Optional.ofNullable(eventHandlerData.get("eventName")).ifPresent(j -> condition.and(e -> e.getEventName().equals(j.getAsString())));
                Optional.ofNullable(eventHandlerData.get("eventType")).ifPresent(j -> condition.and(e -> e.getEventType().equals(j.getAsString())));
                Optional.ofNullable(eventHandlerData.get("eventData")).map(JsonElement::getAsJsonObject)
                        .ifPresent(jsonObject -> {
                            jsonObject.entrySet().forEach(entry -> condition.and(event ->
                                // IF the event field exists, check if the value equals the event data,
                                    // otherwise return false, because the event doesn't match
                                Optional.ofNullable(event.getEventField(entry.getKey()))
                                        .map(eventData -> eventData.equals(entry.getValue().getAsString()))
                                        .orElse(false)
                            ));
                        });
                return condition;
            }

            @Override
            public void handleEvent(Event theEvent) {
                Optional.ofNullable(eventHandlerData.get("httpEventReceiver"))
                        .map(JsonElement::getAsString)
                        .ifPresent(dest -> sendHttpEvent(dest, theEvent));

                Optional.ofNullable(eventHandlerData.get("socketEventReceiver"))
                        .map(JsonElement::getAsString)
                        .ifPresent(dest -> sendSocketEvent(dest, theEvent));
            }

            private void sendHttpEvent(String destination, Event theEvent) {
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

            private void sendSocketEvent(String destination, Event theEvent) {
                // TODO: Implement this
                throw new NotImplementedException();
            }
        };
    }
}
