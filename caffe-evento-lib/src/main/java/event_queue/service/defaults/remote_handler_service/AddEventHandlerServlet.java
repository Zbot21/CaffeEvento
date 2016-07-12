package event_queue.service.defaults.remote_handler_service;

import com.google.gson.GsonBuilder;
import event_queue.EventHandler;
import event_queue.EventHandlerBuilder;
import event_queue.service.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by chris on 7/11/16.
 */
public class AddEventHandlerServlet extends ServiceServlet {
    public AddEventHandlerServlet(Service service) {
        super(service);
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        EventHandler handler = new GsonBuilder().create().fromJson(req.getReader(), EventHandlerBuilder.class).build();
        service.addEventHandler(handler);
        res.getWriter().write(handler.getEventHandlerId().toString()); // Return the event handler id
    }
}
