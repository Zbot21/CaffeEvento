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
public class RemoteServerService extends AbstractService {
    private UUID serverId = UUID.randomUUID();
    private EventSource eventGenerator;

    public RemoteServerService(ServletContextHandler handler) {
        this(new EventQueueInterfaceImpl(), handler);
    }

    public UUID getServerId() {
        return serverId;
    }

    public RemoteServerService(EventQueueInterface eventQueueInterface, ServletContextHandler handler) {
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

//        // Add a servlet for getting the server id
//        server.addServletConsumer("/getServerId", (req, res) -> {
//            try {
//                res.getWriter().write(serverId.toString());
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        });
//
//        // Add a servlet for receiving an event
//        server.addServletConsumer("/serverReceiveEvent", (req, res) -> {
//            try {
//                eventGenerator.registerEvent(Event.decodeEvent(req.getReader()));
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        });
    }
}
