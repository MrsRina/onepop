package rina.onepop.club.client.module.misc;

import rina.onepop.club.api.module.Module;
import rina.onepop.club.api.module.impl.ModuleCategory;
import rina.onepop.club.api.module.registry.Registry;
import rina.onepop.club.client.event.network.PacketEvent;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.util.EnumHand;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

/**
 * @author SrRina
 * @since 26/05/2021 at 00:11
 **/
@Registry(name = "Switch", tag = "Switch", description = "Switch hands to server.", category = ModuleCategory.MISC)
public class ModuleSwitch extends Module {
    @Listener
    public void onReceive(PacketEvent.Send event) {
        if (event.getPacket() instanceof CPacketAnimation) {
            final CPacketAnimation packet = (CPacketAnimation) event.getPacket();
            final EnumHand hand = packet.hand;

            packet.hand = hand == EnumHand.MAIN_HAND ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND;
        }
    }
}
