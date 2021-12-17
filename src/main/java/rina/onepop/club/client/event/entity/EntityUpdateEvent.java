package rina.onepop.club.client.event.entity;

import rina.onepop.club.api.event.Event;
import rina.onepop.club.api.event.impl.EventStage;

/**
 * @author SrRina
 * @since 01/10/2021 at 00:24
 **/
public class EntityUpdateEvent extends Event {
    public EntityUpdateEvent() {
        super(EventStage.PRE);
    }
}
