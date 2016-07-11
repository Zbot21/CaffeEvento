package event_queue.service.defaults.remote_handler_service;

import event_queue.service.Service;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by chris on 7/11/16.
 */
public class AddEventHandlerServlet extends ServiceServlet {
    public AddEventHandlerServlet(Service service) {
        super(service);
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res) {
        throw new NotImplementedException();
    }
}
