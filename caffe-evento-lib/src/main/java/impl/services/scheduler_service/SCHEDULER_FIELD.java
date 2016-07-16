package impl.services.scheduler_service;

/**
 * Optional fields for SchedulerService
 * Created by eric on 7/16/16.
 */
public enum SCHEDULER_FIELD {
    //sets the time of the first occurence of ScheduledEvent, if not present then ScheduledEvent is Scheduled to occur immediately
    START_TIME("SCHEDULED_TIME"),
    //sets minimum time to first instance from when SchedulerService recieves the Event, overrides ScheduledTime if later.
    DELAY("DELAY"),
    //sets the period between event recurrences, if not specified the ScheduledEvent does not repeat
    REPEAT_PERIOD("PERIOD"),
    //sets time at which the last ScheduledEvent may occur. If this is after the current time then the ScheduledEvent never happens.
    END_TIME("SCHEDULED_END_TIME"),
    //sets maximum time during which ScheduledEvent can repeat after first occurence, overrides Scheduled end time if shorter
    MAXDURATION("MAX_DURATION"),
    //gives the maximum number of times which the event can repeat. If maximum repetitions are reached before time limits then ScheduledEvent stops occuring.
    MAXREPEATS("MAX_REPEATS");

    private final String field;

    private SCHEDULER_FIELD(String field) {
        this.field = field;
    }

    public String toString(){
        return this.field;
    }
}
