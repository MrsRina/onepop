package rina.onepop.club.client.event.entity;

import rina.onepop.club.api.event.Event;
import rina.onepop.club.api.event.impl.EventStage;

/**
 * @author SrRina
 * @since 16/05/2021 at 19:51
 **/
public class AbstractHorseEvent extends Event {
    public AbstractHorseEvent(EventStage stage) {
        super(stage);
    }
}
