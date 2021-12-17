package rina.onepop.club.client.module.misc;

import net.minecraft.network.play.server.SPacketAnimation;
import rina.onepop.club.api.module.Module;
import rina.onepop.club.api.module.impl.ModuleCategory;
import rina.onepop.club.api.module.registry.Registry;
import rina.onepop.club.client.event.network.PacketEvent;
import net.minecraft.network.play.client.CPacketAnimation;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

/**
 * @author SrRina
 * @since 31/05/2021 at 00:25
 **/
@Registry(name = "No Server Swing", tag = "NoServerSwing", description = "Cancel all swing from server.", category = ModuleCategory.MISC)
public class ModuleNoServerSwing extends Module {
    @Listener
    public void onPacketReceive(PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketAnimation) {
            event.setCanceled(true);
        }
    }
}
