package impl.service.defaults.sensors;

import api.event_queue.EventQueueInterface;
import impl.service.AbstractService;

/**
 * Created by chris on 7/4/16.
 */
public class ClockService extends AbstractService{
    public ClockService(EventQueueInterface eventQueueInterface)
    {
        super(eventQueueInterface);
        //Todo: Add handlers to eventQueueInterface
        //Todo: Add EventSources to the eventQueueInterface
    }
}
