package rina.onepop.club.client.module.player;

import me.rina.turok.util.TurokTick;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSkull;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketHeldItemChange;
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
import rina.onepop.club.api.util.client.NullUtil;
import rina.onepop.club.api.util.item.SlotUtil;
import rina.onepop.club.api.util.math.PositionUtil;
import rina.onepop.club.api.util.math.RotationUtil;
import rina.onepop.club.api.util.world.BlockUtil;
import rina.onepop.club.client.event.client.RunTickEvent;
import rina.onepop.club.client.manager.network.Rotation;
import rina.onepop.club.client.manager.network.RotationManager;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

/**
 * @author SrRina
 * @since 04/05/2021 at 18:07
 **/
@Registry(name = "Burrow Vanilla", tag = "BurrowVanilla", description = "Vanilla burrow using anvil, sand...", category = ModuleCategory.PLAYER)
public class ModuleBurrowVanilla extends Module {
    // Misc.
    public static ValueBoolean settingRenderSwing = new ValueBoolean("Render Swing", "RenderSwing", "Render player swing.", true);
    public static ValueBoolean settingAirPlace = new ValueBoolean("Air Place", "AirPlace", "1.16 moment.", false);
    public static ValueBoolean settingGravel = new ValueBoolean("Gravel", "Gravel", "Uses gravel.", true);
    public static ValueBoolean settingSand = new ValueBoolean("Sand", "Sand", "I love sands!", true);
    public static ValueBoolean settingAnvil = new ValueBoolean("Anvil","Anvil", "Uses anvil!", true);
    public static ValueBoolean settingHead = new ValueBoolean("Head", "Head", "Heads!", true);
    public static ValueEnum settingRotateMode = new ValueEnum("Rotate", "Rotate", "Rotates mode!", Rotation.SEND);

    private final Item itemAnvil = Item.getItemFromBlock(Blocks.ANVIL);
    private final Item itemSand = Item.getItemFromBlock(Blocks.SAND);
    private final Item itemGravel = Item.getItemFromBlock(Blocks.GRAVEL);

    private boolean withOffhand;
    private int blockSlot;

    private boolean isPlaced;
    private final TurokTick timeOut = new TurokTick();

    @Override
    public void onDisable() {
        this.timeOut.reset();
        this.isPlaced = false;
    }

    @Override
    public void onEnable() {
        this.timeOut.reset();
        this.isPlaced = false;
    }

    @Listener
    public void onTick(RunTickEvent event) {
        if (NullUtil.isPlayerWorld()) {
            return;
        }

        this.blockSlot = this.findFirstBlock();
        this.withOffhand = this.blockSlot == 999;

        final BlockPos offsetSelfPosition = new BlockPos(Math.floor(mc.player.posX), Math.floor(mc.player.posY + 0.5f), Math.floor(mc.player.posZ));

        if (!this.isPlaced) {
            this.timeOut.reset();
        }

        if (this.blockSlot == -1 || !BlockUtil.isAir(offsetSelfPosition) || this.timeOut.isPassedSI(1)) {
            this.setDisabled();

            return;
        }

        final boolean flagSkull = SlotUtil.getItem(this.blockSlot) instanceof ItemSkull;
        final BlockPos extendOffset = offsetSelfPosition.up(flagSkull ? 0 : 2);

        if (!this.isPlaced) {
            this.isPlaced = this.doPlace(extendOffset);
        }
    }

    public int findFirstBlock() {
        final Item offhandItem = mc.player.getHeldItemOffhand().getItem();

        if (this.checkItem(offhandItem)) {
            return 999;
        }

        for (int i = 0; i < 9; i++) {
            final Item items = SlotUtil.getItem(i);

            if (this.checkItem(items)) {
                return i;
            }
        }

        return -1;
    }

    public boolean checkItem(final Item item) {
        return item == itemAnvil || item == itemSand || item == itemGravel || item == Items.SKULL;
    }

    public boolean doPlace(BlockPos place) {
        if (!BlockUtil.isAir(place)) {
            return false;
        }

        boolean state = false;

        for (EnumFacing faces : EnumFacing.values()) {
            final BlockPos offset = place.offset(faces);
            final Block block = mc.world.getBlockState(offset).getBlock();

            if (block != Blocks.AIR || settingAirPlace.getValue()) {
                final EnumFacing facing = faces.getOpposite();
                final Vec3d hit = PositionUtil.calculateHitPlace(offset, facing);

                // I removed the facing, maybe it makes the place slow...?
                float facingX = 0.5f;
                float facingY = 0.5f;
                float facingZ = 0.5f;

                boolean flagSneak = BlockUtil.BLACK_LIST.contains(block);

                if (!this.withOffhand) {
                    mc.player.connection.sendPacket(new CPacketHeldItemChange(this.blockSlot));
                }

                if (flagSneak) {
                    mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
                }

                float[] rotates = RotationUtil.getPlaceRotation(hit);

                // Send task!
                RotationManager.task(settingRotateMode.getValue(), rotates);

                EnumHand hand = this.withOffhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND;

                mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(offset, facing, hand, facingX, facingY, facingZ));

                if (settingRenderSwing.getValue()) {
                    mc.player.swingArm(hand);
                } else {
                    mc.player.connection.sendPacket(new CPacketAnimation(hand));
                }

                if (flagSneak) {
                    mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
                }

                if (!this.withOffhand) {
                    mc.player.connection.sendPacket(new CPacketHeldItemChange(mc.player.inventory.currentItem));
                }

                state = true;

                break;
            }
        }

        return state;
    }
}
