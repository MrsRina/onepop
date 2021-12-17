package rina.onepop.club.api.event;

import rina.onepop.club.api.event.impl.EventStage;

/**
 * @author SrRina
 * @since 15/11/20 at 7:45pm
 */
public class Event {
    private EventStage stage;

    private boolean isCanceled;

    public Event() {
        this.stage = EventStage.PRE;
    }

    public Event(EventStage stage) {
        this.stage = stage;
    }

    public void setStage(EventStage stage) {
        this.stage = stage;
    }

    public EventStage getStage() {
        return stage;
    }

    public void setCanceled(boolean canceled) {
        isCanceled = canceled;
    }

    public boolean isCanceled() {
        return isCanceled;
    }
}
