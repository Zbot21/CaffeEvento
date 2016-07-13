package event_queue.service.defaults.remote_service.server;

import event_queue.service.Service;
import event_queue.service.defaults.remote_service.ServiceServlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;
import java.util.UUID;

import static event_queue.service.defaults.remote_service.server.HttpServerConstants.EVENT_HANDLER_ID_PARAM;

/**
 * Created by chris on 7/11/16.
 */
public class RemoveEventHandlerServlet extends ServiceServlet {
    public RemoveEventHandlerServlet(Service service) {
        super(service);
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res) {
        Optional.ofNullable(req.getParameter(EVENT_HANDLER_ID_PARAM))
                .ifPresent(s -> service.removeEventHandler(UUID.fromString(s)));
    }
}
