package event_queue.service.defaults.request_service;

import event_queue.*;
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

    /* Request Types */
    public static final String REQUEST_FUFILLED_EVENT = "REQUEST_FUFILLED";
    public static final String REQUEST_FAILED_EVENT = "REQUEST_FAILED";
    public static final String REQUEST_COMPLETED_EVENT = "REQUEST_COMPLETED";

    public static final int MAX_RETRIES = 5;

    private final EventGenerator eventGenerator = new EventGenerator();
    private final Map<UUID, Request> activeRequests = new HashMap<>();

    private static final Log log = LogFactory.getLog(RequestService.class);

    public RequestService() {

        addEventSource(eventGenerator);

        // Add the request event handler
        addEventHandler(EventHandlerBuilder.create()
                .eventType(REQUEST_EVENT_TYPE)
                .eventHandler(theEvent -> {
                    try {
                        Request theRequest = new Request(theEvent);
                        activeRequests.put(theRequest.getRequestId(), theRequest);
                    } catch (RequestException e) {
                        log.error("Could not generate a request for the event.", e);
                        e.printStackTrace();
                    }
        }).build());
    }

    public static Event generateRequestEvent(String eventName, Event fufillmentEvent){
        return EventBuilder.create()
                .name(eventName).type(REQUEST_EVENT_TYPE)
                .data(REQUEST_EVENT_FUFILLMENT, fufillmentEvent.encodeEvent())
                .data(REQUEST_ID_FIELD, UUID.randomUUID().toString())
                .build();
    }

    public static Event generateRequestSuccessEvent(String eventName, UUID requestId) {
        return EventBuilder.create()
                .name(eventName)
                .type(REQUEST_FUFILLED_EVENT)
                .data(REQUEST_ID_FIELD, requestId.toString()).build();
    }

    public static Event generateRequestFailedEvent(String eventName, UUID requestId) {
        return EventBuilder.create()
                .name(eventName)
                .type(REQUEST_FAILED_EVENT)
                .data(REQUEST_ID_FIELD, requestId.toString()).build();
    }

    public int numberOfActiveRequests() {
        return activeRequests.size();
    }

    private class Request {
        private final UUID requestId;
        private final AtomicInteger fufillmentAttempts = new AtomicInteger(0);
        private List<EventHandler> requestEventHandlers = new ArrayList<>();

        public Request(Event sourceEvent) throws RequestException {
            if (sourceEvent.getEventField(REQUEST_ID_FIELD) == null) {
                throw new RequestException("No request ID field.");
            }
            if (sourceEvent.getEventField(REQUEST_EVENT_FUFILLMENT) == null) {
                throw new RequestException("No request fufillment event");
            }

            requestId = UUID.fromString(sourceEvent.getEventField(REQUEST_ID_FIELD));
            Event fufillmentEvent = Event.decodeEvent(sourceEvent.getEventField(REQUEST_EVENT_FUFILLMENT));
            fufillmentEvent.setEventField(REQUEST_ID_FIELD, requestId.toString());

            // Event handler success
            EventHandler success = EventHandlerBuilder.create()
                    .eventType(REQUEST_FUFILLED_EVENT)
                    .eventData(REQUEST_ID_FIELD, requestId.toString())
                    .eventHandler(event -> {
                        eventGenerator.registerEvent(createRequestCompletedEvent());
                        requestEventHandlers.forEach(e -> removeEventHandler(e)); // Do not replace with method reference
                        activeRequests.remove(requestId);
                    }).build();

            EventHandler failure = EventHandlerBuilder.create()
                    .eventType(REQUEST_FAILED_EVENT)
                    .eventData(REQUEST_ID_FIELD, requestId.toString())
                    .eventHandler(event -> {
                        if(fufillmentAttempts.incrementAndGet() > MAX_RETRIES) {
                            log.error("Could not fufill request " + requestId + ".\n" +
                                    "Induced by " + sourceEvent.getEventName() + ": " + sourceEvent.getEventId());
                            activeRequests.remove(requestId);
                            requestEventHandlers.forEach(e -> removeEventHandler(e)); // Do not replace with method reference
                        } else {
                            eventGenerator.registerEvent(fufillmentEvent);
                        }
                    }).build();

            requestEventHandlers.add(success);
            requestEventHandlers.add(failure);
            addEventHandler(success);
            addEventHandler(failure);

            eventGenerator.registerEvent(fufillmentEvent);
        }

        private Event createRequestCompletedEvent() {
            Event completedEvent = new Event("Completed request " + requestId, REQUEST_COMPLETED_EVENT);
            completedEvent.setEventField(REQUEST_ID_FIELD, requestId.toString());
            return completedEvent;
        }

        public UUID getRequestId() {
            return requestId;
        }
    }
}
