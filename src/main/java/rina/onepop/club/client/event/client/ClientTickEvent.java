package rina.onepop.club.client.event.client;

import rina.onepop.club.api.event.Event;
import rina.onepop.club.api.event.impl.EventStage;

/**
 * @author SrRina
 * @since 06/12/20 at 12:02am
 */
public class ClientTickEvent extends Event {
    public ClientTickEvent() {
        super(EventStage.PRE);
    }
}