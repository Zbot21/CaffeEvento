package impl.lib;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
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
public class EmbeddedServletServerTest {
    private EmbeddedServletServer instance;

    @Before
    public void setUp() throws Exception {
        instance = new EmbeddedServletServer();
        instance.start();
        Thread.sleep(50);
    }

    @After
    public void tearDown() throws Exception {
        Thread.sleep(50);
        instance.stop();
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
        int port = EmbeddedServletServer.DEFAULT_LISTEN_PORT;
        HttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost("http://localhost:"+port+"/testPoint");
        post.setEntity(new StringEntity("test post data"));
        HttpResponse res = client.execute(post);
        HttpEntity entity = res.getEntity();
        if (entity != null) {
            InputStream inputStream = entity.getContent();
            try {
                String response = IOUtils.toString(inputStream, "UTF-8");
                assertEquals("test response data", response);
            } finally {
                inputStream.close();
            }
        } else {
            fail();
        }
    }

}