package rina.onepop.club.client.module.combat.autocrystalrewrite;

import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumHand;

/**
 * @author SrRina
 * @since 07/05/2022 at 13:00
 **/
public class PacketThread extends Thread {
    private int entityId;

    public PacketThread(int entityId) {
        this.entityId = entityId;
    }

    @Override
    public void run() {
        CPacketUseEntity packet = new CPacketUseEntity();

        packet.entityId = this.entityId;
        packet.action = CPacketUseEntity.Action.ATTACK;

        Minecraft.getMinecraft().player.connection.sendPacket(packet);
    }
}
