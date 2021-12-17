package rina.onepop.club.client.event.entity;

import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.world.World;
import rina.onepop.club.api.event.Event;
import rina.onepop.club.api.event.impl.EventStage;

/**
 * @author SrRina
 * @since 30/06/2021 at 15:46
 **/
public class UseEntityWorldProcessEvent extends Event {
    private CPacketUseEntity packet;
    private World world;

    public UseEntityWorldProcessEvent(World world, CPacketUseEntity packet) {
        super(EventStage.PRE);

        this.packet = packet;
        this.world = world;
    }

    public World getWorld() {
        return world;
    }

    public CPacketUseEntity getPacket() {
        return packet;
    }
}
