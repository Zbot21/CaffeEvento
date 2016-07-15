package impl.services.remote_service;

import api.event_queue.Event;
import api.event_queue.EventHandler;
import api.event_queue.EventQueueInterface;
import api.event_queue.EventSource;
import api.utils.EventBuilder;
import impl.event_queue.EventQueueInterfaceImpl;
import impl.event_queue.EventSourceImpl;
import impl.lib.EmbeddedServletServer;
import impl.services.AbstractService;

import java.io.IOException;
import java.util.Optional;

/**
 * Created by chris on 7/14/16.
 */
public class RemoteServerService extends AbstractService {
    private EmbeddedServletServer server;
    private String serverId;
    private EventSource eventGenerator;

    public RemoteServerService() {
        this(new EventQueueInterfaceImpl(), EmbeddedServletServer.DEFAULT_LISTEN_PORT);
    }

    public RemoteServerService(EventQueueInterface eventQueueInterface, int port) {
        super(eventQueueInterface);
        eventGenerator = new EventSourceImpl();
        server = new EmbeddedServletServer(port);
        getEventQueueInterface().addEventSource(eventGenerator);

        getEventQueueInterface().addEventHandler(EventHandler.create()
                .eventType("CREATE_EVENT_HANDLER")
                .eventData("serverId", serverId)
                .eventHandler(event -> Optional.ofNullable(event.getEventField("EVENT_HANDLER_DETAILS"))
                        .ifPresent(data -> getEventQueueInterface().addEventHandler(EventHandler.fromJson(data))))
                .build());

        // TODO: assign server id

        // Add a servlet for getting the server id
        server.addServletConsumer("/getServerId", (req, res) -> {
            try {
                res.getWriter().write(serverId);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        // Add a servlet for receiving an event
        server.addServletConsumer("/receiveEvent", (req, res) -> {
            try {
                eventGenerator.registerEvent(Event.decodeEvent(req.getReader()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

    }

    public void startServer() {
        server.start();
    }

    public void stopServer() {
        server.stop();
    }
}
