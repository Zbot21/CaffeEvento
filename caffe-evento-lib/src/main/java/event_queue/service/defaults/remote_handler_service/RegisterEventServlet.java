package event_queue.service.defaults.remote_handler_service;

import event_queue.Event;
import event_queue.EventGenerator;
import event_queue.service.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by chris on 7/11/16.
 */
public class RegisterEventServlet extends ServiceServlet {
    private EventGenerator eventGenerator = new EventGenerator();

    public RegisterEventServlet(Service service) {
        super(service);
        service.addEventSource(eventGenerator);
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        eventGenerator.registerEvent(Event.decodeEvent(req.getReader()));
    }
}