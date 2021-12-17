package rina.onepop.club.api.util.network;

import rina.onepop.club.Onepop;
import net.minecraft.network.Packet;

/**
 * @author SrRina
 * @since 05/02/2021 at 12:53
 **/
public class PacketUtil {
    /**
     * Its packet tracker technology, for we control the packet!
     */
    public static class PacketTracker {
        private String name;
        private Packet<?> packet;

        private boolean isCanceled;

        public PacketTracker(String name, Packet packet) {
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

    public static void send(Packet<?> packet) {
        Onepop.MC.player.connection.sendPacket(packet);
    }
}
