package rina.onepop.club.client.event.network;

import rina.onepop.club.api.event.Event;
import rina.onepop.club.api.event.impl.EventStage;
import net.minecraft.network.Packet;

/**
 * @author SrRina
 * @since 23/02/2021 at 00:24
 **/
public class PacketEvent extends Event {
    public static class Send extends PacketEvent {
        public Send(Packet<?> packet) {
            super(packet, EventStage.PRE);
        }
    }

    public static class Receive extends PacketEvent {
        public Receive(Packet<?> packet) {
            super(packet, EventStage.POST);
        }
    }

    private Packet<?> packet;

    public PacketEvent(Packet<?> packet, EventStage stage) {
        super(stage);

        this.packet = packet;
    }

    public void setPacket(Packet<?> packet) {
        this.packet = packet;
    }

    public Packet<?> getPacket() {
        return packet;
    }
}
