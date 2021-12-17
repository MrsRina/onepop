package rina.onepop.club.client.module.combat;

import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import rina.onepop.club.api.module.Module;
import rina.onepop.club.api.module.impl.ModuleCategory;
import rina.onepop.club.api.module.registry.Registry;
import rina.onepop.club.api.setting.value.ValueBoolean;
import rina.onepop.club.api.setting.value.ValueEnum;
import rina.onepop.club.api.setting.value.ValueNumber;
import rina.onepop.club.api.util.client.NullUtil;
import rina.onepop.club.api.util.entity.EntityUtil;
import rina.onepop.club.api.util.item.SlotUtil;
import rina.onepop.club.api.util.math.PositionUtil;
import rina.onepop.club.api.util.math.RotationUtil;
import rina.onepop.club.api.util.render.RenderUtil;
import rina.onepop.club.api.util.world.BlockUtil;
import rina.onepop.club.client.event.client.RunTickEvent;
import rina.onepop.club.client.manager.network.Rotation;
import rina.onepop.club.client.manager.network.RotationManager;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

import java.awt.*;

/**
 * @author SrRina
 * @since 22/05/2021 at 02:54
 **/
@Registry(name = "Auto-Minecart Bomb", tag = "AutoMinecartBomb", description = "Bombs enemy using Minecart with TNT!", category = ModuleCategory.COMBAT)
public class ModuleAutoMinecartBomb extends Module {
    // Misc.
    public static ValueBoolean settingAntiNaked = new ValueBoolean("Anti-Naked", "AntiNaked", "Ignore naked players.", true);
    public static ValueBoolean settingRenderSwing = new ValueBoolean("Render Swing", "RenderSwing", "Render swing!", true);
    public static ValueBoolean settingRetrace = new ValueBoolean("Retrace", "Retrace", "Retrace confirm for packets!", true);
    public static ValueNumber settingPlaceRange = new ValueNumber("Place Range", "PlaceRange", "Sets place range.", 4f, 1f, 6f);
    public static ValueNumber settingAmountTNTsPackets = new ValueNumber("Packets", "Packets", "Amount TNTs packets by trail.", 64, 1, 64);
    public static ValueEnum settingRotates = new ValueEnum("Rotates", "Rotates", "Rotates modes!", Rotation.SEND);

    private boolean railOnFloor;

    private int railSlot;
    private int minecartWithTNTSlot;

    private EntityPlayer targetPlayer;

    @Override
    public void onRender3D() {
        if (NullUtil.isPlayerWorld() || this.minecartWithTNTSlot == -1 || this.railSlot == -1 || this.targetPlayer == null || mc.player.getDistance(this.targetPlayer) > settingPlaceRange.getValue().floatValue()) {
            return;
        }

        final BlockPos targetPosition = EntityUtil.getFlooredEntityPosition(this.targetPlayer);

        if (this.railOnFloor) {
            RenderUtil.drawSolidBlock(camera, targetPosition.x, targetPosition.y, targetPosition.z, 1, 0, 1, new Color(0, 255, 0, 100));
        }

        boolean render = false;

        for (Entity entities : mc.world.loadedEntityList) {
            if (entities instanceof EntityMinecart && PositionUtil.collideBlockPos(EntityUtil.getFlooredEntityPosition(entities), targetPosition)) {
                render = true;

                break;
            }
        }

        if (render) {
            RenderUtil.drawSolidBlock(camera, targetPosition.x, targetPosition.y + 0.2f, targetPosition.z, 1f, 0.6f, 1f, new Color(255, 0, 0, 100));
        }
    }

    @Listener
    public void onTick(RunTickEvent event) {
        if (NullUtil.isPlayerWorld()) {
            return;
        }

        this.minecartWithTNTSlot = SlotUtil.findItemSlotFromHotBar(Items.TNT_MINECART);
        this.railSlot = this.getFirstRail();

        this.targetPlayer = EntityUtil.getTarget(13f, false, settingAntiNaked.getValue());

        if (this.targetPlayer == null || this.minecartWithTNTSlot == -1 || this.railSlot == -1) {
            if (this.minecartWithTNTSlot == -1 || this.railSlot == -1) {
                this.print(ChatFormatting.YELLOW + "Sorry, there is no Minecarts/rail on your hot bar.");
                this.setDisabled();
            }

            return;
        }

        final BlockPos targetPosition = EntityUtil.getFlooredEntityPosition(this.targetPlayer);
        final Block targetBlock = BlockUtil.getBlock(targetPosition);

        this.railOnFloor = this.checkRail(Item.getItemFromBlock(targetBlock));

        if (this.railOnFloor) {
            for (int packets = 0; packets < settingAmountTNTsPackets.getValue().intValue(); packets++) {
                this.doPlace(targetPosition, this.minecartWithTNTSlot, null);
            }
        } else {
            this.doPlace(targetPosition, this.railSlot, Blocks.RAIL);
        }
    }

    public void doPlace(BlockPos place, int slot, Block type) {
        if (slot == -1 || mc.player.getDistance(place.x, place.y, place.z) > settingPlaceRange.getValue().floatValue()) {
            return;
        }

        final EnumFacing facing = EnumFacing.UP;
        final BlockPos offset = type == null ? place : place.offset(facing.getOpposite());

        if (!BlockUtil.BLACK_LIST.contains(BlockUtil.getBlock(offset)) && !BlockUtil.isAir(offset)) {
            final Vec3d hit = PositionUtil.calculateHitPlace(offset, facing);
            final float[] rotates = RotationUtil.getPlaceRotation(hit);

            RotationManager.task(settingRotates.getValue(), rotates);

            SlotUtil.setServerCurrentItem(slot);

            mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(offset, facing, EnumHand.MAIN_HAND, 0f, 0f, 0f));

            if (settingRetrace.getValue() && type != null) {
                mc.world.setBlockState(place, type.getDefaultState());
            }

            if (settingRenderSwing.getValue()) {
                mc.player.swingArm(EnumHand.MAIN_HAND);
            } else {
                mc.player.connection.sendPacket(new CPacketAnimation(EnumHand.MAIN_HAND));
            }

            SlotUtil.setServerCurrentItem(mc.player.inventory.currentItem);
        }
    }

    public int getFirstRail() {
        int slot = -1;

        for (int i = 0; i < 9; i++) {
            final Item item = SlotUtil.getItem(i);

            if (this.checkRail(item)) {
                slot = i;

                break;
            }
        }

        return slot;
    }

    public boolean checkRail(Item item) {
        return item == Item.getItemFromBlock(Blocks.RAIL) || item == Item.getItemFromBlock(Blocks.ACTIVATOR_RAIL) || item == Item.getItemFromBlock(Blocks.DETECTOR_RAIL) || item == Item.getItemFromBlock(Blocks.GOLDEN_RAIL);
    }
}
