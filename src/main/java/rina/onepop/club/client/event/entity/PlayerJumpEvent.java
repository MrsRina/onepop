package rina.onepop.club.client.event.entity;

import rina.onepop.club.api.event.Event;
import rina.onepop.club.api.event.impl.EventStage;

/**
 * @author SrRina
 * @since 09/11/2021 at 23:57
 **/
public class PlayerJumpEvent extends Event {
    public PlayerJumpEvent() {
        super(EventStage.PRE);
    }
}
