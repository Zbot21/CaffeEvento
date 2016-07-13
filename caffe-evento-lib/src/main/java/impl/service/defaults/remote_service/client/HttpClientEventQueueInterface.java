package impl.service.defaults.remote_service.client;

import com.google.common.collect.ImmutableMap;
import api.event_queue.Event;
import api.event_queue.EventHandler;
import api.event_queue.EventSink;
import api.event_queue.EventSource;
import impl.event_queue.EventQueueInterfaceImpl;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Map;

import static impl.service.defaults.remote_service.server.HttpServerConstants.*;

/**
 * Created by chris on 7/12/16.
 */
public abstract class HttpClientEventQueueInterface extends EventQueueInterfaceImpl implements EventSink {
    private HttpClient client;
    private String host;
    private int port;

    public HttpClientEventQueueInterface(String host, int port) {
        this(HttpClientBuilder.create().build(), host, port);
    }

    public HttpClientEventQueueInterface(HttpClient client, String host, int port) {
        this.client = client;
        this.host = host;
        this.port = port;
    }

    private URI createUriForEndpoint(String endpoint, Map<String, String> params) throws URISyntaxException {
        URIBuilder builder =  new URIBuilder().setHost(host).setPort(port).setPath(endpoint);
        params.entrySet().forEach(pair -> builder.addParameter(pair.getKey(), pair.getValue()));
        return builder.build();
    }

    private URI createUriForEndpoint(String endpoint) throws URISyntaxException {
        return createUriForEndpoint(endpoint, Collections.EMPTY_MAP);
    }

    @Override
    public void addEventHandler(EventHandler theEventHandler) {
        try {
            HttpPost post = new HttpPost(createUriForEndpoint(ADD_EVENT_HANDLER_ENDPOINT));
            post.setEntity(new StringEntity(theEventHandler.encodeEventHandler()));
            client.execute(post);
        } catch (URISyntaxException | IOException e) {
            log.error("There was an error adding the event handler with id:" + theEventHandler.getEventHandlerId(), e);
            return;
        }
        super.addEventHandler(theEventHandler);
    }

    @Override
    public void removeEventHandler(EventHandler theEventHandler) {
        try {
            HttpPost post = new HttpPost(createUriForEndpoint(REMOVE_EVENT_HANDLER_ENDPOINT,
                    ImmutableMap.of(EVENT_HANDLER_ID_PARAM, theEventHandler.getEventHandlerId().toString())));
            client.execute(post);
        } catch (URISyntaxException | IOException e) {
            log.error("There was an error removing event handler withe.printStackTrace(); id: " + theEventHandler.getEventHandlerId(), e);
        }
        super.removeEventHandler(theEventHandler);
    }

    @Override
    public void addEventSource(EventSource theEventSource) {
        theEventSource.addListener(this);
        super.addEventSource(theEventSource);
    }

    @Override
    public void removeEventSource(EventSource theEventSource) {
        theEventSource.removeListener(this);
        super.removeEventSource(theEventSource);
    }

    @Override
    public void receiveEvent(Event e) {
        try {
            HttpPost post = new HttpPost(createUriForEndpoint(REGISTER_EVENT_ENDPOINT));
            post.setEntity(new StringEntity(e.encodeEvent()));
            client.execute(post);
        } catch (URISyntaxException | IOException e1) {
            e1.printStackTrace();
        }
    }
}
