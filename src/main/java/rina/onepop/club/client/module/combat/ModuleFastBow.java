package rina.onepop.club.client.module.combat;

import net.minecraft.item.ItemBow;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.util.math.BlockPos;
import rina.onepop.club.api.module.Module;
import rina.onepop.club.api.module.impl.ModuleCategory;
import rina.onepop.club.api.module.registry.Registry;
import rina.onepop.club.api.setting.value.ValueNumber;
import rina.onepop.club.api.util.client.NullUtil;
import rina.onepop.club.client.event.client.ClientTickEvent;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

/**
 * @author SrRina
 * @since 04/10/2021 at 22:19
 **/
@Registry(name = "Fast Bow", tag = "FastBow", description = "Fast bow.", category = ModuleCategory.COMBAT)
public class ModuleFastBow extends Module {
    public static ValueNumber settingStage = new ValueNumber("Stage", "Stage", "Stage for release arrow.", 3, 1, 6);

    @Listener
    public void onClientTickEvent(ClientTickEvent event) {
        if (NullUtil.isPlayerWorld()) {
            return;
        }

        if ((mc.player.inventory.getCurrentItem().getItem() instanceof ItemBow && mc.player.isHandActive() && mc.player.getItemInUseMaxCount() >= settingStage.getValue().intValue())) {
            mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, mc.player.getHorizontalFacing()));
            mc.player.connection.sendPacket(new CPacketPlayerTryUseItem(mc.player.getActiveHand()));
            mc.player.stopActiveHand();
        }
    }
}
