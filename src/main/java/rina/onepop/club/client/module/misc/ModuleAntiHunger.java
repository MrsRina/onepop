package rina.onepop.club.client.module.misc;

import rina.onepop.club.api.module.Module;
import rina.onepop.club.api.module.impl.ModuleCategory;
import rina.onepop.club.api.module.registry.Registry;
import rina.onepop.club.api.util.client.NullUtil;
import rina.onepop.club.client.event.client.RunTickEvent;
import rina.onepop.club.client.event.network.PacketEvent;
import net.minecraft.network.play.client.CPacketPlayer;
import rina.onepop.club.api.ISLClass;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

/**
 * @author SrRina
 * @since 15/05/2021 at 17:16
 **/
@Registry(name = "Anti-Hunger", tag = "AntiHunger", description = "Prevents hunger from player!", category = ModuleCategory.MISC)
public class ModuleAntiHunger extends Module {
    @Listener
    public void onPacketReceive(PacketEvent.Send event) {
        if (event.getPacket() instanceof CPacketPlayer) {
            CPacketPlayer packet = (CPacketPlayer) event.getPacket();

            packet.onGround = false;
        }
    }

    @Listener
    public void onListen(RunTickEvent event) {
        if (NullUtil.isPlayerWorld()) {
            return;
        }

        if (ISLClass.mc.player.isSprinting()) {
            ISLClass.mc.player.setSprinting(false);
        }
    }
}
