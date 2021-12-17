package rina.onepop.club.client.module.player;

import rina.onepop.club.api.module.Module;
import rina.onepop.club.api.module.impl.ModuleCategory;
import rina.onepop.club.api.module.registry.Registry;
import rina.onepop.club.client.event.client.RunTickEvent;
import rina.onepop.club.client.event.network.PacketEvent;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

/**
 * @author SrRina
 * @since 31/05/2021 at 00:21
 *
 * I don't think its work, possibly no, I need make a new.
 **/
@Registry(name = "Portal God Mode", tag = "PortalGodMode", description = "Cancel confirm teleport from client side.", category = ModuleCategory.PLAYER)
public class ModulePortalGodMode extends Module {
    @Listener
    public void onReceivePacket(PacketEvent.Receive event) {
        if (event.getPacket() instanceof CPacketConfirmTeleport) {
            event.setCanceled(true);
        }
    }
}
