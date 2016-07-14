package impl.services.defaults.remote_service.server;

import api.event_queue.EventSource;
import impl.event_queue.EventImpl;
import api.event_queue.EventQueueInterface;
import impl.event_queue.EventSourceImpl;
import impl.services.defaults.remote_service.ServiceServlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by chris on 7/11/16.
 */
public class RegisterEventServlet extends ServiceServlet {
    private EventSource eventGenerator = new EventSourceImpl();

    public RegisterEventServlet(EventQueueInterface eventQueueInterface) {
        super(eventQueueInterface);
        eventQueueInterface.addEventSource(eventGenerator);
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        eventGenerator.registerEvent(EventImpl.decodeEvent(req.getReader()));
    }
}