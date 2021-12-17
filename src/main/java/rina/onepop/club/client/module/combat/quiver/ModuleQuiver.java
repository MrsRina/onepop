package rina.onepop.club.client.module.combat.quiver;

import rina.onepop.club.api.ISLClass;
import rina.onepop.club.api.module.Module;
import rina.onepop.club.api.module.impl.ModuleCategory;
import rina.onepop.club.api.module.registry.Registry;
import rina.onepop.club.api.setting.value.ValueBoolean;
import rina.onepop.club.api.setting.value.ValueEnum;
import rina.onepop.club.api.util.chat.ChatUtil;
import rina.onepop.club.api.util.client.NullUtil;
import rina.onepop.club.client.event.client.RunTickEvent;
import rina.onepop.club.client.manager.network.Rotation;
import rina.onepop.club.client.manager.network.RotationManager;
import net.minecraft.item.ItemBow;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

/**
 * @author SrRina
 * @since 22/05/2021 at 02:00
 **/
@Registry(name = "Quiver", tag = "Quiver", description = "Automatically quivers arrow to your self.", category = ModuleCategory.COMBAT)
public class ModuleQuiver extends Module {
    /* Misc. */
    public static ValueBoolean settingManual = new ValueBoolean("Manual", "Manual", "Quivers manually.", false);
    public static ValueEnum settingMode = new ValueEnum("Mode", "Mode", "Modes for quiver.", Mode.SMART);

    @Override
    public void onSetting() {
    }

    @Listener
    public void onTick(RunTickEvent event) {
        if (NullUtil.isPlayerWorld()) {
            return;
        }

        final boolean withBowHandEquipped = mc.player.getHeldItemMainhand().getItem() instanceof ItemBow || mc.player.getHeldItemOffhand().getItem() instanceof ItemBow;

        switch ((Mode) settingMode.getValue()) {
            case TOGGLE: {
                if (withBowHandEquipped && mc.player.getItemInUseMaxCount() >= 4) {
                    this.doQuiver();
                    this.setDisabled();
                }

                break;
            }

            case SMART: {
                if (withBowHandEquipped && mc.player.isHandActive() && mc.player.getItemInUseMaxCount() >= 4) {
                    this.doQuiver();
                }

                break;
            }
        }
    }

    public void doQuiver() {
        float[] rotates = {
                mc.player.rotationYaw, -90
        };

        final Rotation rotate = settingManual.getValue() ? Rotation.LEGIT : Rotation.SEND;

        RotationManager.task(rotate, rotates);

        mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, ISLClass.mc.player.getHorizontalFacing()));
        mc.player.connection.sendPacket(new CPacketPlayerTryUseItem(mc.player.getActiveHand()));
        mc.player.stopActiveHand();

        // Post fix rotation.
        RotationManager.task(Rotation.SEND, rotates);
    }
}
