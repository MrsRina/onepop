package rina.onepop.club.client.module.player;

import rina.onepop.club.api.module.Module;
import rina.onepop.club.api.module.impl.ModuleCategory;
import rina.onepop.club.api.module.registry.Registry;
import rina.onepop.club.api.util.client.NullUtil;
import rina.onepop.club.client.event.network.PacketEvent;
import net.minecraft.network.play.client.CPacketPlayer;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

/**
 * @author SrRina
 * @since 15/05/2021 at 17:08
 **/
@Registry(name = "No Fall", tag = "NoFall", description = "Prevent fall.", category = ModuleCategory.PLAYER)
public class ModuleNoFall extends Module {
    @Listener
    public void onPacketSend(PacketEvent.Send event) {
        if (NullUtil.isPlayerWorld()) {
            return;
        }

        if (event.getPacket() instanceof CPacketPlayer && mc.player.fallDistance > 0f && !mc.player.isElytraFlying()) {
            CPacketPlayer packet = (CPacketPlayer) event.getPacket();

            packet.onGround = true;
        }
    }
}
