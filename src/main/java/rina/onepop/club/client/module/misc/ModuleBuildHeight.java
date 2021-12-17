package rina.onepop.club.client.module.misc;

import rina.onepop.club.api.module.Module;
import rina.onepop.club.api.module.impl.ModuleCategory;
import rina.onepop.club.api.module.registry.Registry;
import rina.onepop.club.client.event.network.PacketEvent;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

/**
 * @author SrRina
 * @since 18/05/2021 at 11:56
 **/
@Registry(name = "Build Height", tag = "BuildHeight", description = "Extend build height at 255.", category = ModuleCategory.MISC)
public class ModuleBuildHeight extends Module {
    @Listener
    public void onPacketReceive(PacketEvent.Send event) {
        if (event.getPacket() instanceof CPacketPlayerTryUseItemOnBlock) {
            CPacketPlayerTryUseItemOnBlock packet = (CPacketPlayerTryUseItemOnBlock) event.getPacket();

            if (packet.getPos().getY() >= 255 && packet.getDirection() == EnumFacing.UP) {
                packet.placedBlockDirection = EnumFacing.DOWN;
            }
        }
    }
}
