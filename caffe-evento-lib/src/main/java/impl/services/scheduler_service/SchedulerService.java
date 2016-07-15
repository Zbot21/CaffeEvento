package impl.services.scheduler_service;

import api.event_queue.Event;
import api.event_queue.EventHandler;
import api.event_queue.EventQueueInterface;
import api.event_queue.EventSource;
import api.utils.EventBuilder;
import impl.event_queue.EventImpl;
import impl.event_queue.EventSourceImpl;
import impl.services.AbstractService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * This service takes a schedule event and generates scheduled events
 * Scheduled events happen once at a specific time
 * if the specified execution time has already passed then the scheduled event is triggered immediately
 * Created by eric on 7/15/16.
 */

// TODO: Define DATE_FORMAT Somewhere more public
// TODO: Add ability for ScheduledEvent to occur after a specific delay

public class SchedulerService extends AbstractService {
    public static final String DATE_FORMAT = "dow mon dd hh:mm:ss zzz yyyy";
    public static final String SCHEDULE_EVENT_TYPE = "SCHEDULE";
    public static final String SCHEDULE_EVENT_CANCEL_TYPE = "UNSCHEDULE";
    public static final String SCHEDULED_EVENT_ACTION = "SCHEDULED_ACTION";
    public static final String SCHEDULE_ID_FIELD = "SCHEDULER_ID";
    public static final String SCHEDULER_TIME_FIELD = "SCHEDULED_TIME";

    private static final Log log = LogFactory.getLog(SchedulerService.class);

    private final EventSource eventGenerator = new EventSourceImpl();
    private final Map<UUID, Scheduler> activeSchedulers= new HashMap<>();

    public SchedulerService(EventQueueInterface eventQueueInterface)
    {
        super(eventQueueInterface);
        getEventQueueInterface().addEventSource(eventGenerator);

        // Add the Schedule event handler
        getEventQueueInterface().addEventHandler(EventHandler.create()
                .eventType(SCHEDULE_EVENT_TYPE)
                .eventHandler(theEvent -> {
                    try {
                        Scheduler theScheduler = new Scheduler(theEvent);
                        activeSchedulers.put(theScheduler.getSchedulerId(), theScheduler);
                    } catch (SchedulerException e) {
                        log.error("could not schedule a timer for the event.", e);
                        e.printStackTrace();
                    }
                }).build());
    }

    public static Event generateSchedulerEvent(String eventName, Event actionEvent) {
        return EventBuilder.create()
                .name(eventName).type(SCHEDULE_EVENT_TYPE)
                .data(SCHEDULED_EVENT_ACTION, actionEvent.encodeEvent())
                .data(SCHEDULE_ID_FIELD, UUID.randomUUID().toString())
                .build();
    }

    public static Event generateSchedulerCancelEvent(String eventName, UUID SchedulerId) {
        return EventBuilder.create()
                .name(eventName)
                .type(SCHEDULE_EVENT_CANCEL_TYPE)
                .data(SCHEDULE_ID_FIELD, SchedulerId.toString()).build();
    }

    public int numberOfActiveSchedulers() {
        return activeSchedulers.size();
    }

    private class Scheduler {
        private final UUID SchedulerId;
        private List<EventHandler> SchedulerEventHandlers =  new ArrayList<>();
        private Timer eventTimer = new Timer();
        private Date ScheduledTime;

        public Scheduler(Event sourceEvent) throws SchedulerException {
            if (sourceEvent.getEventField(SCHEDULE_ID_FIELD) == null) {
                throw new SchedulerException("No Scheduler ID field.");
            }
            if (sourceEvent.getEventField(SCHEDULER_TIME_FIELD) == null) {
                throw new SchedulerException("No time specified.");
            }
            try {
                ScheduledTime = new SimpleDateFormat(DATE_FORMAT).parse(sourceEvent.getEventField(SCHEDULER_TIME_FIELD));
            } catch(ParseException e) {
                throw new SchedulerException("Invalid DATE_FORMAT");
            }

            SchedulerId = UUID.fromString(sourceEvent.getEventField(SCHEDULE_ID_FIELD));
            Event scheduledEvent = Event.decodeEvent(sourceEvent.getEventField(SCHEDULED_EVENT_ACTION));
            scheduledEvent.setEventField(SCHEDULE_ID_FIELD, SchedulerId.toString());

            EventHandler canceled = EventHandler.create()
                    .eventType(SCHEDULE_EVENT_CANCEL_TYPE)
                    .eventData(SCHEDULE_ID_FIELD, SchedulerId.toString())
                    .eventHandler(event -> {
                        eventGenerator.registerEvent(createSchedulerCanceledEvent());
                        SchedulerEventHandlers.forEach(e -> getEventQueueInterface().removeEventHandler(e));
                        eventTimer.cancel();
                        activeSchedulers.remove(SchedulerId);
                    }).build();

            SchedulerEventHandlers.add(canceled);
            getEventQueueInterface().addEventHandler(canceled);

            eventTimer.schedule(new TimerTask() {
                public void run() {
                    eventGenerator.registerEvent(scheduledEvent);
                    // don't forget to remove the scheduler from active schedulers when it completes
                    activeSchedulers.remove(SchedulerId);
                }
            }, ScheduledTime);
        }

        private Event createSchedulerCanceledEvent() {
            Event canceledEvent =  new EventImpl("Canceled Scheduler " + SchedulerId, SCHEDULE_EVENT_CANCEL_TYPE);
            canceledEvent.setEventField(SCHEDULE_ID_FIELD, SchedulerId.toString());
            return canceledEvent;
        }

        public UUID getSchedulerId(){
            return SchedulerId;
        }
    }
}
