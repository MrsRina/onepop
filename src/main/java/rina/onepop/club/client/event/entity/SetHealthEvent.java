package rina.onepop.club.client.event.entity;

import rina.onepop.club.api.event.Event;
import rina.onepop.club.api.event.impl.EventStage;
import net.minecraft.entity.player.EntityPlayer;

/**
 * @author SrRina
 * @since 04/05/2021 at 01:23
 **/
public class SetHealthEvent extends Event {
    private EntityPlayer player;
    private float health;

    public SetHealthEvent(float health) {
        super(EventStage.PRE);

        this.health = health;
    }

    public void setPlayer(EntityPlayer player) {
        this.player = player;
    }

    public EntityPlayer getPlayer() {
        return player;
    }

    public void setHealth(float health) {
        this.health = health;
    }

    public float getHealth() {
        return health;
    }
}
