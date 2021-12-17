package rina.onepop.club.client.event.network;

import net.minecraft.entity.Entity;
import rina.onepop.club.api.event.Event;

/**
 * @author SrRina
 * @since 07/07/2021 at 01:12
 **/
public class ConfirmEntityPacketEvent extends Event {
    private final Entity entity;

    public ConfirmEntityPacketEvent(Entity entityIn) {
        super();

        this.entity = entityIn;
    }

    public Entity getEntity() {
        return entity;
    }
}
