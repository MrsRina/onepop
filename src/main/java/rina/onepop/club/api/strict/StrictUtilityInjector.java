package rina.onepop.club.api.strict;

import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;

/**
 * @author Rina
 * @since 01/10/2021 at 10:07am
 **/
public class StrictUtilityInjector {
    public static void rotation(Packet<?> packet, float yaw, float pitch) {
        if (packet instanceof CPacketPlayer.Position) {
            CPacketPlayer.Position thePacket = (CPacketPlayer.Position) packet;

            thePacket.yaw = yaw;
            thePacket.pitch = pitch;
        } else if (packet instanceof CPacketPlayer.Rotation) {
            CPacketPlayer.Rotation thePacket = (CPacketPlayer.Rotation) packet;

            thePacket.yaw = yaw;
            thePacket.pitch = pitch;
        } else if (packet instanceof CPacketPlayer.PositionRotation) {
            final CPacketPlayer.PositionRotation thePacket = (CPacketPlayer.PositionRotation) packet;

            thePacket.yaw = yaw;
            thePacket.pitch = pitch;
        } else if (packet instanceof CPacketPlayer) {
            CPacketPlayer thePacket = (CPacketPlayer) packet;

            thePacket.yaw = yaw;
            thePacket.pitch = pitch;
        }
    }

    public static void position(Packet<?> packet, double x, double y, double z) {
        if (packet instanceof CPacketPlayer.Position) {
            final CPacketPlayer.Position thePacket = (CPacketPlayer.Position) packet;

            thePacket.x = x != 28000000 ? x : thePacket.x;
            thePacket.y = y != 28000000 ? y : thePacket.y;
            thePacket.z = z != 28000000 ? z : thePacket.z;
        } else if (packet instanceof CPacketPlayer.Rotation) {
            final CPacketPlayer.Rotation thePacket = (CPacketPlayer.Rotation) packet;

            thePacket.x = x != 28000000 ? x : thePacket.x;
            thePacket.y = y != 28000000 ? y : thePacket.y;
            thePacket.z = z != 28000000 ? z : thePacket.z;
        } else if (packet instanceof CPacketPlayer.PositionRotation) {
            final CPacketPlayer.PositionRotation thePacket = (CPacketPlayer.PositionRotation) packet;

            thePacket.x = x != 28000000 ? x : thePacket.x;
            thePacket.y = y != 28000000 ? y : thePacket.y;
            thePacket.z = z != 28000000 ? z : thePacket.z;
        } else if (packet instanceof CPacketPlayer) {
            final CPacketPlayer thePacket = (CPacketPlayer) packet;

            thePacket.x = x != 28000000 ? x : thePacket.x;
            thePacket.y = y != 28000000 ? y : thePacket.y;
            thePacket.z = z != 28000000 ? z : thePacket.z;
        }
    }
}
