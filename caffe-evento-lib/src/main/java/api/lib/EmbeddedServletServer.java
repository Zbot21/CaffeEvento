package api.lib;

import org.mortbay.jetty.Handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.function.BiConsumer;

/**
 * Created by chris on 7/16/16.
 */
public interface EmbeddedServletServer {
    void addServletConsumer(String endpoint,
                            BiConsumer<HttpServletRequest, HttpServletResponse> consumer);

    void addAdditionalHandler(Handler handler);

    void asyncStart();

    void syncStart();

    void stop();
}
