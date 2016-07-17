package impl.services.remote_service;

import api.event_queue.*;
import api.lib.EmbeddedServletServer;
import api.utils.EventBuilder;
import impl.event_queue.EventQueueInterfaceImpl;
import impl.event_queue.EventSourceImpl;
import impl.event_queue.SynchronousEventQueue;
import impl.lib.EmbeddedServletServerImpl;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import test_util.EventCollector;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.*;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;

/**
 * Created by chris on 7/16/16.
 */
public class RemoteServerServiceTest {
    private static final int port = 2345;
    private static final int rxPort = 2346;
    private EventQueue eventQueue = new SynchronousEventQueue();
    private EventQueueInterface eventQueueInterface = new EventQueueInterfaceImpl();
    private RemoteServerService instance =
            new RemoteServerService(eventQueueInterface, new EmbeddedServletServerImpl(port));
    private HttpClient client;
    private EventCollector eventCollector = new EventCollector();
    private EventSource eventInjector = new EventSourceImpl();
    private EmbeddedServletServer receivingService = new EmbeddedServletServerImpl(rxPort);
    private final List<Event> receivedEvents = new ArrayList<>();

    @Before
    public void setUp() throws Exception {
        client = HttpClients.createDefault();
        eventQueue.registerService(instance);
        eventQueue.addEventSource(eventInjector);
        eventQueue.addEventHandler(eventCollector.getHandler());
        instance.startServer();
        receivingService.addServletConsumer("/receiveEvent", (req, res) -> {
            try {
                receivedEvents.add(Event.decodeEvent(req.getReader()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        receivingService.asyncStart();
        Thread.sleep(50);
    }

    @After
    public void tearDown() throws Exception {
        receivingService.stop();
        instance.stopServer();
    }

    @Test
    public void testReceieveEvent() throws Exception{
        Event event = EventBuilder.create().name("funny message")
                .type("funny reference").build();

        HttpPost post = new HttpPost("http://localhost:"+port+"/serverReceiveEvent");
        post.setEntity(new StringEntity(event.encodeEvent()));
        client.execute(post);

        List<Event> collectedEvents = eventCollector.findEventsWithId(event.getEventId());
        assertEquals(1, collectedEvents.size());
        assertEquals("funny message", collectedEvents.get(0).getEventName());
        assertEquals("funny reference", collectedEvents.get(0).getEventType());
    }

    @Test
    public void testGetServerId() throws Exception {
        HttpGet request = new HttpGet("http://localhost:"+port+"/getServerId");

        HttpResponse res = client.execute(request);
        HttpEntity entity = res.getEntity();
        if(entity != null) {
            try (InputStream inputStream = entity.getContent()) {
                String response = IOUtils.toString(inputStream, "UTF-8");
                assertEquals(instance.getServerId().toString(), response);
            }
        }
    }

    @Test
    public void testAddEventHandler() throws Exception {
        EventHandler handler = EventHandler.create().hasDataKey("TEST").build();
        Event createEvent = EventBuilder.create().name("Create Event Handler Event")
                .type("CREATE_EVENT_HANDLER")
                .data("serverId", instance.getServerId().toString())
                .data("eventHandlerDetails", handler.encodeToJson())
                .build();

        eventInjector.registerEvent(createEvent);
        // 2 default event handlers and one that we create
        assertEquals(3, eventQueueInterface.getEventHandlers().size());
        Optional<EventHandler> rxHandlerOpt = eventQueueInterface.getEventHandlers().stream()
                .filter(h -> h.getEventHandlerId().equals(handler.getEventHandlerId())).findFirst();
        assertTrue(rxHandlerOpt.isPresent());
        EventHandler rxHandler = rxHandlerOpt.get();

        Event testEvent = createMock(Event.class);
        expect(testEvent.getEventField("TEST")).andReturn("Some data").anyTimes();
        replayAll();
        assertEquals(true, handler.getHandlerCondition().test(testEvent));
        assertEquals(true, rxHandler.getHandlerCondition().test(testEvent));
        assertEquals(false, handler.getHandlerCondition().test(createEvent));
        assertEquals(false, rxHandler.getHandlerCondition().test(createEvent));
        verifyAll();
    }

    @Test
    public void testAddEventHandlerNetwork() throws Exception {
        EventHandler handler = EventHandler.create().hasDataKey("TEST")
                .httpEventReceiver("http://localhost:"+rxPort+"/receiveEvent")
                .build();

        Event createEvent = EventBuilder.create().name("Create Event Handler Event")
                .type("CREATE_EVENT_HANDLER")
                .data("serverId", instance.getServerId().toString())
                .data("eventHandlerDetails", handler.encodeToJson())
                .build();

        HttpPost post = new HttpPost("http://localhost:"+port+"/serverReceiveEvent");
        post.setEntity(new StringEntity(createEvent.encodeEvent()));
        client.execute(post);

        // 2 default event handlers and one that we create
        assertEquals(3, eventQueueInterface.getEventHandlers().size());
        Optional<EventHandler> rxHandlerOpt = eventQueueInterface.getEventHandlers().stream()
                .filter(h -> h.getEventHandlerId().equals(handler.getEventHandlerId())).findFirst();
        assertTrue(rxHandlerOpt.isPresent());
        EventHandler rxHandler = rxHandlerOpt.get();

        Event testEvent = createMock(Event.class);
        expect(testEvent.getEventField("TEST")).andReturn("Some data").anyTimes();

        Event testSentEvent = EventBuilder.create().name("funny reference").type("funny things")
                .data("TEST", "Some data").build();
        eventInjector.registerEvent(testSentEvent);

        replayAll();
        assertEquals(true, handler.getHandlerCondition().test(testEvent));
        assertEquals(true, rxHandler.getHandlerCondition().test(testEvent));
        assertEquals(false, handler.getHandlerCondition().test(createEvent));
        assertEquals(false, rxHandler.getHandlerCondition().test(createEvent));
        assertEquals(1, receivedEvents.size());
        assertEquals(testSentEvent.getEventId(), receivedEvents.get(0).getEventId());
        assertEquals("funny reference", receivedEvents.get(0).getEventName());
        assertEquals("funny things", receivedEvents.get(0).getEventType());
        verifyAll();
    }
}