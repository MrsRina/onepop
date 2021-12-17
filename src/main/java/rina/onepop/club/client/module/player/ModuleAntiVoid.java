package rina.onepop.club.client.module.player;

/**
 * @author SrRina
 * @since 04/10/2021 at 22:35
 **/

import me.rina.turok.util.TurokTick;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import rina.onepop.club.api.module.Module;
import rina.onepop.club.api.module.impl.ModuleCategory;
import rina.onepop.club.api.module.registry.Registry;
import rina.onepop.club.api.setting.value.ValueBoolean;
import rina.onepop.club.api.setting.value.ValueNumber;
import rina.onepop.club.api.util.client.NullUtil;
import rina.onepop.club.api.util.entity.PlayerUtil;
import rina.onepop.club.api.util.math.PositionUtil;
import rina.onepop.club.api.util.math.RotationUtil;
import rina.onepop.club.api.util.world.BlockUtil;
import rina.onepop.club.client.event.client.RunTickEvent;
import rina.onepop.club.client.event.network.PacketEvent;
import rina.onepop.club.client.manager.network.Rotation;
import rina.onepop.club.client.manager.network.RotationManager;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

@Registry(name = "Anti-Void", tag = "AntiVoid", description = "Prevents fall from void.", category = ModuleCategory.PLAYER)
public class ModuleAntiVoid extends Module {
    // Misc.
    public static ValueBoolean settingPacket = new ValueBoolean("Packet", "Packet", "Packet mode for anti-void module.", false);
    public static ValueBoolean settingFill = new ValueBoolean("Fill", "Fill", "Fills the void.", false);
    public static ValueNumber settingDelay = new ValueNumber("Delay", "Delay", "Delay for fill.", 250, 0, 1000);

    private boolean wasInVoid;
    private final TurokTick stamp = new TurokTick();

    @Override
    public void onSetting() {
        settingDelay.setEnabled(settingFill.getValue());
    }

    @Listener
    public void onSendPacket(PacketEvent.Send event) {
        if (this.wasInVoid && settingPacket.getValue() && mc.player != null && event.getPacket() instanceof CPacketPlayer) {
            event.setCanceled(true);
        }
    }

    @Listener
    public void onTickEvent(RunTickEvent event) {
        if (NullUtil.isPlayerWorld()) {
            return;
        }

        final BlockPos position = PlayerUtil.getBlockPos();

        if (mc.player.posY <= 1f && BlockUtil.isAir(position.down())) {
            mc.player.setVelocity(0, 0, 0);
            mc.player.fallDistance = 0f;

            if (settingFill.getValue() && this.stamp.isPassedMS(settingDelay.getValue().floatValue())) {
                int uuv = mc.player.getHeldItemOffhand().getItem() instanceof ItemBlock ? -99 : this.findFirstBlock();

                if (uuv != -1 && BlockUtil.isPlaceableExcludingBlackList(position.down()) && !this.wasInVoid) {
                    final EnumHand hand = uuv == -99 ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND;
                    final EnumFacing facing = BlockUtil.getPlaceableFacing(position);

                    final BlockPos offset = position.offset(facing.getOpposite()).down();

                    if (hand == EnumHand.MAIN_HAND) {
                        mc.player.connection.sendPacket(new CPacketHeldItemChange(uuv));
                    }

                    boolean flag = false;

                    if (BlockUtil.BLACK_LIST.contains(BlockUtil.getBlock(offset))) {
                        mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));

                        flag = true;
                    }

                    RotationManager.task(Rotation.REL, RotationUtil.getPlaceRotation(PositionUtil.calculateHitPlace(offset, facing)));

                    mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(position.offset(facing.getOpposite()).down(), facing, hand, 0.5f, 0.5f, 0.5f));

                    if (flag) {
                        mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
                    }

                    if (hand == EnumHand.MAIN_HAND) {
                        mc.player.connection.sendPacket(new CPacketHeldItemChange(mc.player.inventory.currentItem));
                    }

                    this.wasInVoid = true;
                }

                if (!this.wasInVoid && !settingFill.getValue()) {
                    this.wasInVoid = true;
                }
            }
        } else {
            if (this.wasInVoid) {
                this.wasInVoid = false;
            }

            this.stamp.reset();
        }
    }

    public int findFirstBlock() {
        int slot = -1;

        for (int i = 0; i < 9; i++) {
            ItemStack itemStack = mc.player.inventory.getStackInSlot(i);

            if (itemStack.getItem() instanceof ItemBlock) {
                slot = i;

                break;
            }
        }

        return slot;
    }
}
