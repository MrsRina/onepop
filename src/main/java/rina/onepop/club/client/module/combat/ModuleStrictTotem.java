package rina.onepop.club.client.module.combat;

import me.rina.turok.util.TurokTick;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.Item;
import net.minecraft.network.play.client.CPacketCloseWindow;
import net.minecraft.network.play.client.CPacketEntityAction;
import rina.onepop.club.api.module.Module;
import rina.onepop.club.api.module.impl.ModuleCategory;
import rina.onepop.club.api.module.registry.Registry;
import rina.onepop.club.api.setting.value.ValueNumber;
import rina.onepop.club.api.util.client.NullUtil;
import rina.onepop.club.client.event.client.RunTickEvent;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

/**
 * @author SrRina
 * @since 02/10/2021 at 11:34am
 **/
@Registry(name = "Strict Totem", tag = "StrictTotem", description = "Auto-Totem strict.", category = ModuleCategory.COMBAT)
public class ModuleStrictTotem extends Module {
    public static ValueNumber settingDelay = new ValueNumber("Delay", "Delay", "Delay.", 2f, 0f, 10f);

    private final TurokTick delay = new TurokTick();
    private boolean isReplaced;

    private double lastMotionX;
    private double lastMotionZ;

    @Override
    public void onSetting() {
        if (!this.isEnabled()) {
            this.isReplaced = false;
        }
    }

    @Listener
    public void onRunTickEvent(RunTickEvent event) {
        if (NullUtil.isPlayerWorld()) {
            return;
        }

        boolean flag = mc.player.getHeldItemOffhand().getItem() == Items.TOTEM_OF_UNDYING;

        if (flag) {
            if (this.isReplaced) {
                mc.player.connection.sendPacket(new CPacketCloseWindow(mc.player.inventoryContainer.windowId));
                mc.player.setVelocity(this.lastMotionX, mc.player.motionY, this.lastMotionZ);

                this.isReplaced = false;
            }

            this.delay.reset();
        }

        if (!flag && this.delay.isPassedMS(settingDelay.getValue().floatValue() * 10)) {
            int slot = this.findTotemSlot();

            if (this.delay.isPassedMS(2000)) {
                this.isReplaced = false;
            }

            if (slot != -1 && !this.isReplaced) {
                this.lastMotionX = mc.player.motionX;
                this.lastMotionZ = mc.player.motionZ;

                mc.player.setVelocity(0, mc.player.motionY, 0);
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.OPEN_INVENTORY));

                mc.player.setVelocity(0, mc.player.motionY, 0);

                mc.playerController.windowClick(0, slot, 0, ClickType.PICKUP, mc.player);
                mc.playerController.windowClick(0, 45, 0, ClickType.PICKUP, mc.player);
                mc.playerController.windowClick(0, slot, 0, ClickType.PICKUP, mc.player);
                mc.playerController.updateController();

                this.isReplaced = true;
            }
        }
    }

    public int findTotemSlot() {
        int slot = -1;

        for (int i = 0; i < 36; i++) {
            Item item = mc.player.inventory.getStackInSlot(i).getItem();

            if (item == Items.TOTEM_OF_UNDYING) {
                if (i < 9) {
                    i += 36;
                }

                slot = i;

                break;
            }
        }

        return slot;
    }
}
