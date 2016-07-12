package event_queue.service.defaults.remote_handler_service;

import event_queue.service.Service;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.stream.Collectors;

/**
 * Created by chris on 7/11/16.
 */
public class RegisterEventServlet extends ServiceServlet {
    public RegisterEventServlet(Service service) {
        super(service);
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String reqContent = req.getReader().lines().collect(Collectors.joining());

    }
}