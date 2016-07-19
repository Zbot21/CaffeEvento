package impl.services.remote_service;

import api.event_queue.Event;
import api.event_queue.EventHandler;
import api.event_queue.EventQueueInterface;
import api.event_queue.EventSource;
import impl.event_queue.EventQueueInterfaceImpl;
import impl.event_queue.EventSourceImpl;
import api.lib.EmbeddedServletServer;
import impl.lib.servlet_server.EmbeddedServletServerImpl;
import impl.services.AbstractService;
import org.eclipse.jetty.servlet.ServletContextHandler;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by chris on 7/14/16.
 */
public final class RemoteServerService extends AbstractService {
    private UUID serverId = UUID.randomUUID();
    private EventSource eventGenerator;

    public RemoteServerService(String name, ServletContextHandler handler) {
        this(name, new EventQueueInterfaceImpl(), handler);
    }

    public UUID getServerId() {
        return serverId;
    }

    public RemoteServerService(String name, EventQueueInterface eventQueueInterface, ServletContextHandler handler) {
        super(eventQueueInterface);
        eventGenerator = new EventSourceImpl();
        EmbeddedServletServer server = new EmbeddedServletServerImpl(handler);
        getEventQueueInterface().addEventSource(eventGenerator);

        // Create event handler event
        getEventQueueInterface().addEventHandler(EventHandler.create()
                .eventType("CREATE_EVENT_HANDLER")
                .eventData("serverId", serverId.toString())
                .hasDataKey("eventHandlerDetails")
                .eventHandler(event -> getEventQueueInterface()
                        .addEventHandler(EventHandler.fromJson(event.getEventField("eventHandlerDetails"))))
                .build());

        // Remove event handler event
        getEventQueueInterface().addEventHandler(EventHandler.create()
                .eventType("REMOVE_EVENT_HANDLER")
                .eventData("serverId", serverId.toString())
                .hasDataKey("eventHandlerId")
                .eventHandler(event -> getEventQueueInterface()
                        .removeEventHandler(UUID.fromString("eventHandlerId")))
                .build());

        server.addService(name, serverId, "/receiveEvent", (req, res) -> eventGenerator.registerEvent(Event.decodeEvent(req.getReader())));
    }
}
