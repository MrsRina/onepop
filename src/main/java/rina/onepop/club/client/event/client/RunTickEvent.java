package rina.onepop.club.client.event.client;

import rina.onepop.club.api.event.Event;
import rina.onepop.club.api.event.impl.EventStage;

/**
 * @author SrRina
 * @since 17/04/2021 at 15:40
 **/
public class RunTickEvent extends Event {
    public RunTickEvent() {
        super(EventStage.PRE);
    }
}
