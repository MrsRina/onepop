package rina.onepop.club.api.tracker;

import net.minecraft.network.Packet;

/**
 * @author SrRina
 * @since 05/02/2021 at 12:34
 **/
public class Tracker {
    private String name;
    private Packet<?> packet;

    private boolean isCanceled;

    public Tracker(String name, Packet packet) {
        this.name = name;
        this.packet = packet;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPacket(Packet<?> packet) {
        this.packet = packet;
    }

    public String getName() {
        return name;
    }

    public Packet<?> getPacket() {
        return packet;
    }

    public boolean isCanceled() {
        return isCanceled;
    }

    public void setCanceled(boolean canceled) {
        isCanceled = canceled;
    }

    /**
     * Pre event before send packet.
     */
    public void onPre() {}

    /**
     * Post event after send packet.
     */
    public void onPost() {}
}
