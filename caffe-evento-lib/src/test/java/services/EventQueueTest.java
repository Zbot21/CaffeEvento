package services;

import com.google.common.collect.Lists;
import events.EventHandler;
import events.EventSource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.annotation.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import java.util.List;

import static junit.framework.TestCase.assertFalse;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.junit.Assert.assertTrue;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;

/**
 * Created by chris on 7/1/16.
 */
@RunWith( PowerMockRunner.class )
@PrepareForTest( { Service.class } )
public class EventQueueTest {
    EventQueue instance = new EventQueue();

    @Mock
    Service service;

    List<EventHandler> eventHandlers;
    List<EventSource> eventSources;

    @Before
    public void setUp() {
        eventHandlers = Lists.newArrayList(createMock(EventHandler.class), createMock(EventHandler.class));
        eventSources = Lists.newArrayList(createMock(EventSource.class), createMock(EventSource.class));
    }


    @Test
    public void testRegisterUnregisterService() {
        expect(service.getEventHandlers()).andReturn(eventHandlers).times(2);
        expect(service.getEventSources()).andReturn(eventSources).times(2);

        service.addServiceChangedListener(instance);
        expectLastCall().once();

        service.removeServiceChangedListener(instance);
        expectLastCall().once();

        replayAll();
        instance.registerService(service);
        List<Service> services = Whitebox.getInternalState(instance, "services");
        List<EventHandler> handlers = Whitebox.getInternalState(instance, "eventHandlers");
        List<EventSource> sources = Whitebox.getInternalState(instance, "eventSources");
        assertTrue(services.contains(service));
        assertTrue(handlers.containsAll(eventHandlers));
        assertTrue(sources.containsAll(eventSources));

        instance.unRegisterService(service);
        services = Whitebox.getInternalState(instance, "services");
        handlers = Whitebox.getInternalState(instance, "eventHandlers");
        sources = Whitebox.getInternalState(instance, "eventSources");
        assertFalse(services.contains(service));
        assertFalse(handlers.containsAll(eventHandlers));
        assertFalse(sources.containsAll(eventSources));

        verifyAll();
    }

}