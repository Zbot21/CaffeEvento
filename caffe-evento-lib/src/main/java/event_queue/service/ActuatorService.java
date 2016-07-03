package event_queue.service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chris on 7/2/16.
 */
public abstract class ActuatorService extends Service {
    private List<Action> providedActions = new ArrayList<>();

    public List<Action> getProvidedActions() {
        return new ArrayList<>(providedActions);
    }

    protected void addProvidedAction(Action theAction) {
        providedActions.add(theAction);
    }
}
