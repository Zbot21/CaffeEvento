package impl.service.defaults.remote_service.server;

import api.event_queue.EventHandler;
import api.event_queue.EventQueueInterface;
import impl.service.defaults.remote_service.ServiceServlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by chris on 7/11/16.
 */
public class AddEventHandlerServlet extends ServiceServlet {
    public AddEventHandlerServlet(EventQueueInterface eventQueueInterface) {
        super(eventQueueInterface);
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        EventHandler handler = EventHandler.fromJson(req.getReader());
        eventQueueInterface.addEventHandler(handler);
        res.getWriter().write(handler.getEventHandlerId().toString()); // Return the event handler id
    }
}
