package event_queue.service.defaults.remote_handler_service;

import event_queue.service.Service;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpServlet;

/**
 * Created by chris on 7/11/16.
 */
public abstract class ServiceServlet extends HttpServlet {
    protected Log log;
    protected Service service;
    public ServiceServlet(Service service) {
        log = LogFactory.getLog(getClass());
        this.service = service;
    }
}
