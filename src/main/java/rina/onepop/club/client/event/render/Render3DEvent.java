package rina.onepop.club.client.event.render;

import rina.onepop.club.api.event.Event;
import rina.onepop.club.api.event.impl.EventStage;

/**
 * @author SrRina
 * @since 06/12/20 at 12:25am
 */
public class Render3DEvent extends Event {
    private float partialTicks;

    public Render3DEvent(float partialTicks) {
        super(EventStage.PRE);

        this.partialTicks = partialTicks;
    }

    protected void setPartialTicks(float partialTicks) {
        this.partialTicks = partialTicks;
    }

    public float getPartialTicks() {
        return partialTicks;
    }
}
