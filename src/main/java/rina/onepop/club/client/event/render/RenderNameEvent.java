package rina.onepop.club.client.event.render;

import rina.onepop.club.api.event.Event;
import rina.onepop.club.api.event.impl.EventStage;

/**
 * @author SrRina
 * @since 25/02/2021 at 16:26
 **/
public class RenderNameEvent extends Event {
    public RenderNameEvent(EventStage stage) {
        super(stage);
    }
}