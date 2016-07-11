package event_queue.service.defaults.remote_handler_service;

import event_queue.service.Service;

import javax.servlet.http.HttpServlet;

/**
 * Created by chris on 7/11/16.
 */
public abstract class ServiceServlet extends HttpServlet {
    protected Service service;
    public ServiceServlet(Service service) {
        this.service = service;
    }
}
