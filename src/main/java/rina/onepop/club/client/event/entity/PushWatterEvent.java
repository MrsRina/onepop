package rina.onepop.club.client.event.entity;

import rina.onepop.club.api.event.Event;
import rina.onepop.club.api.event.impl.EventStage;

/**
 * @author SrRina
 * @since 19/05/2021 at 01:38
 **/
public class PushWatterEvent extends Event {
    public PushWatterEvent() {
        super(EventStage.PRE);
    }
}
