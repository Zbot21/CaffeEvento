package event_queue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Predicate;

/**
 * Created by chris on 7/1/16.
 */
public abstract class EventHandler {
    private UUID eventHandlerID = UUID.randomUUID();
    protected Log log;
    public abstract Predicate<Event> getHandlerCondition();
    public abstract void handleEvent(Event theEvent);
    public final UUID getEventHandlerId() {
        return eventHandlerID;
    }

    public EventHandler() {
        log = LogFactory.getLog(getClass());
    }

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
}
