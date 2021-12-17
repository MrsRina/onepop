package rina.onepop.club.client.event.render;

import rina.onepop.club.api.event.Event;
import rina.onepop.club.api.event.impl.EventStage;

/**
 * @author SrRina
 * @since 25/02/2021 at 22:36
 **/
public class RenderPortalOverlayEvent extends Event {
    public RenderPortalOverlayEvent(EventStage stage) {
        super(stage);
    }
}
