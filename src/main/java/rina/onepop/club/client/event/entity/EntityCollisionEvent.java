package rina.onepop.club.client.event.entity;

import net.minecraft.entity.Entity;
import rina.onepop.club.api.event.Event;

/**
 * @author SrRina
 * @since 04/07/2021 at 16:46
 **/
public class EntityCollisionEvent extends Event {
    private final Entity entity;

    public EntityCollisionEvent(Entity player) {
        super();

        this.entity = player;
    }

    public Entity getEntity() {
        return entity;
    }
}
