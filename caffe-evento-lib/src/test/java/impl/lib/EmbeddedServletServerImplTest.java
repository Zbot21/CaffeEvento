package impl.lib;

import api.lib.EmbeddedServletServer;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/**
 * Created by chris on 7/16/16.
 */
public class EmbeddedServletServerImplTest {
    private EmbeddedServletServer instance;
    private int port = 2345;
    private Server server = new Server(port);

    @Before
    public void setUp() throws Exception {
        ServletContextHandler servletContextHandler = new ServletContextHandler();
        servletContextHandler.setContextPath("/");
        server.setHandler(servletContextHandler);
        instance = new EmbeddedServletServerImpl(servletContextHandler);
        server.start();
        Thread.sleep(50);
    }

    @After
    public void tearDown() throws Exception {
        Thread.sleep(50);
        server.stop();
    }

    @Test
    public void testAddEndpoint() throws Exception {
        instance.addServletConsumer("/testPoint", (req, res) -> {
            try{
                String val = req.getReader().lines().collect(Collectors.joining());
                assertEquals("test post data", val);

                res.getWriter().write("test response data");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        HttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost("http://localhost:"+port+"/testPoint");
        post.setEntity(new StringEntity("test post data"));
        HttpResponse res = client.execute(post);
        HttpEntity entity = res.getEntity();
        if (entity != null) {
            try (InputStream inputStream = entity.getContent()) {
                String response = IOUtils.toString(inputStream, "UTF-8");
                assertEquals("test response data", response);
            }
        } else {
            fail();
        }
    }

}