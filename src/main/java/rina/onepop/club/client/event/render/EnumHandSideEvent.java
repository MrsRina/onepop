package rina.onepop.club.client.event.render;

import rina.onepop.club.api.event.Event;
import net.minecraft.util.EnumHandSide;

public class EnumHandSideEvent extends Event {

    private final EnumHandSide handSide;

    public EnumHandSideEvent(EnumHandSide handSide) {
        this.handSide = handSide;
    }

    public EnumHandSide getHandSide() {
        return handSide;
    }
}
