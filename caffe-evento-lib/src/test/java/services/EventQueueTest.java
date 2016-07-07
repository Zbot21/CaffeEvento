package services;

import com.google.common.collect.Lists;
import event_queue.Event;
import event_queue.EventHandler;
import event_queue.EventQueue;
import event_queue.EventSource;
import event_queue.service.Action;
import event_queue.service.ActuatorService;
import event_queue.service.Service;
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
    Event event;

    @Mock
    Service service;

    List<EventHandler> eventHandlers;
    List<EventSource> eventSources;

    @Before
    public void setUp() {
        eventHandlers = Lists.newArrayList(createMock(EventHandler.class), createMock(EventHandler.class));
        eventSources = Lists.newArrayList(createMock(EventSource.class), createMock(EventSource.class));
    }

    private void prepareService(Service theService) {
        expect(theService.getEventHandlers()).andReturn(eventHandlers).once();
        expect(theService.getEventSources()).andReturn(eventSources).once();

        theService.addServiceChangedListener(instance);
        expectLastCall().once();

        eventSources.forEach(source -> {
            source.addListener(instance);
            expectLastCall().once();
        });
    }

    @Test
    public void testReceiveEvent() {
        prepareService(service);

        eventHandlers.forEach(eventHandler -> {
            eventHandler.handleEvent(event);
            expectLastCall().once();

            expect(eventHandler.getHandlerCondition()).andReturn(event -> true);
        });

        replayAll();
        instance.registerService(service);
        instance.receiveEvent(event);
        verifyAll();
    }

    @Test
    public void testPredicateFalse() {
        prepareService(service);

        eventHandlers.forEach(eventHandler -> {
            expect(eventHandler.getHandlerCondition()).andReturn(event -> false);
        });

        replayAll();
        instance.registerService(service);
        instance.receiveEvent(event);
        verifyAll();
    }

    @Test
    public void testGetProvidedActions() {
        ActuatorService actuatorService1 = createMock(ActuatorService.class);
        ActuatorService actuatorService2 = createMock(ActuatorService.class);
        prepareService(actuatorService1);
        prepareService(actuatorService2);
        List<Action> actions1 = Lists.newArrayList(createMock(Action.class), createMock(Action.class));
        List<Action> actions2 = Lists.newArrayList(createMock(Action.class), createMock(Action.class));

        expect(actuatorService1.getProvidedActions()).andReturn(actions1).anyTimes();
        expect(actuatorService2.getProvidedActions()).andReturn(actions2).anyTimes();

        replayAll();
        instance.registerService(actuatorService1);
        instance.registerService(actuatorService2);
        assertTrue(instance.getProvidedActions().containsAll(actions1));
        assertTrue(instance.getProvidedActions().containsAll(actions2));

        verifyAll();
    }

    @Test
    public void testRegisterUnregisterService() {
        expect(service.getEventHandlers()).andReturn(eventHandlers).times(2);
        expect(service.getEventSources()).andReturn(eventSources).times(2);

        eventSources.forEach(source -> {
            source.addListener(instance);
            expectLastCall().once();

            source.removeListener(instance);
            expectLastCall().once();
        });

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