package event_queue.service.defaults.request_service;

import event_queue.Event;
import event_queue.EventGenerator;
import event_queue.EventHandler;
import event_queue.service.Service;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

/**
 * Created by chris on 7/2/16.
 */
public class RequestService extends Service {
    public static final String REQUEST_EVENT_TYPE = "REQUEST";
    public static final String REQUEST_EVENT_FUFILLMENT = "REQUEST_FUFILLMENT";

    /* Request Fields */
    public static final String REQUEST_ID_FIELD = "REQUEST_ID";

    public static final String REQUEST_FUFILLED_EVENT = "REQUEST_FUFILLED";
    public static final String REQUEST_FAILED_EVENT = "REQUEST_FAILED";
    public static final String REQUEST_COMPLETED_EVENT = "REQUEST_COMPLETED";

    private final EventGenerator eventGenerator = new EventGenerator();
    private final Map<String, Request> activeRequests = new HashMap<>();

    private static final Log log = LogFactory.getLog(RequestService.class);

    public RequestService() {

        addEventSource(eventGenerator);

        // Add the request event handler
        addEventHandler(new RequestEventHandler());
    }

    public static Event generateRequestEvent(String eventName, Event fufillmentEvent){
        Event requestEvent = new Event(eventName, REQUEST_EVENT_TYPE);
        requestEvent.setEventField(REQUEST_EVENT_FUFILLMENT, fufillmentEvent.encodeEvent());
        requestEvent.setEventField(REQUEST_ID_FIELD, UUID.randomUUID().toString());
        return requestEvent;
    }

    private class RequestEventHandler implements EventHandler {

        @Override
        public Predicate<Event> getHandlerCondition() {
            return e -> e.getEventType().equals(REQUEST_EVENT_TYPE);
        }

        @Override
        public void handleEvent(Event theEvent) {
            try {
                Request theRequest = new Request(theEvent);
                activeRequests.put(theRequest.getRequestId(), theRequest);
            } catch (RequestException e) {
                log.error("Could not generate a request for the event.", e);
                e.printStackTrace();
            }
        }
    }

    private class Request {
        private final String requestId;
        private final AtomicInteger fufillmentAttempts = new AtomicInteger(0);

        public static final int MAX_RETRIES = 5;


        public Request(Event sourceEvent) throws RequestException {
            if (sourceEvent.getEventField(REQUEST_ID_FIELD) == null) {
                throw new RequestException("No request ID field.");
            }
            if (sourceEvent.getEventField(REQUEST_EVENT_FUFILLMENT) == null) {
                throw new RequestException("No request fufillment event");
            }
            requestId = sourceEvent.getEventField(REQUEST_ID_FIELD);
            Event fufillmentEvent = Event.decodeEvent(sourceEvent.getEventField(REQUEST_EVENT_FUFILLMENT));
            fufillmentEvent.setEventField(REQUEST_ID_FIELD, requestId);

            List<EventHandler> requestEventHandlers = new ArrayList<>();

            // Event handler success
            EventHandler success = new EventHandler() {
                @Override
                public Predicate<Event> getHandlerCondition() {
                    return e -> e.getEventType().equals(REQUEST_FUFILLED_EVENT) &&
                            e.getEventField(REQUEST_ID_FIELD) != null && e.getEventField(REQUEST_ID_FIELD).equals(requestId);
                }

                @Override
                public void handleEvent(Event theEvent) {
                    eventGenerator.registerEvent(createRequestCompletedEvent());
                    requestEventHandlers.forEach(RequestService.this::removeEventHandler);
                    activeRequests.remove(requestId);
                }

                private Event createRequestCompletedEvent() {
                    Event completedEvent = new Event("Completed request " + requestId, REQUEST_COMPLETED_EVENT);
                    completedEvent.setEventField(REQUEST_ID_FIELD, requestId);
                    return completedEvent;
                }
            };

            EventHandler failure = new EventHandler() {
                @Override
                public Predicate<Event> getHandlerCondition() {
                    return e -> e.getEventType().equals(REQUEST_FAILED_EVENT) &&
                            e.getEventField(REQUEST_ID_FIELD) != null && e.getEventField(REQUEST_ID_FIELD).equals(requestId);
                }

                @Override
                public void handleEvent(Event theEvent) {
                    if(fufillmentAttempts.get() > MAX_RETRIES) {
                        log.error("Could not fufill request" + requestId + ". Induced by " + sourceEvent.getEventName());
                        activeRequests.remove(requestId);
                        requestEventHandlers.forEach(RequestService.this::removeEventHandler);
                    } else {
                        fufillmentAttempts.incrementAndGet();
                        eventGenerator.registerEvent(fufillmentEvent);
                    }

                }
            };

            requestEventHandlers.add(success);
            requestEventHandlers.add(failure);
            addEventHandler(success);
            addEventHandler(failure);

            eventGenerator.registerEvent(fufillmentEvent);
        }

        public String getRequestId() {
            return requestId;
        }
    }
}
