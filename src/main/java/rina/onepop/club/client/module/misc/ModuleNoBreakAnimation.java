package rina.onepop.club.client.module.misc;

import net.minecraft.network.play.client.CPacketPlayerDigging;
import rina.onepop.club.api.module.Module;
import rina.onepop.club.api.module.impl.ModuleCategory;
import rina.onepop.club.api.module.registry.Registry;
import rina.onepop.club.client.event.network.PacketEvent;
import net.minecraft.network.play.server.SPacketBlockBreakAnim;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

/**
 * @author SrRina
 * @since 31/05/2021 at 01:11
 **/
@Registry(name = "No Break Animation", tag = "NoBreakAnimation", description = "No break animation for server.", category = ModuleCategory.MISC)
public class ModuleNoBreakAnimation extends Module {
    @Listener
    public void onReceivePacket(PacketEvent.Send event) {
        if (event.getPacket() instanceof CPacketPlayerDigging) {
            final CPacketPlayerDigging packet = (CPacketPlayerDigging) event.getPacket();

            if (packet.getAction() == CPacketPlayerDigging.Action.START_DESTROY_BLOCK || packet.getAction() == CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK || packet.getAction() == CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK) {
                event.setCanceled(true);
            }
        }
    }
}
