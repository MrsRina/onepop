package rina.onepop.club.client.module.player;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
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
import rina.onepop.club.api.util.entity.PlayerUtil;
import rina.onepop.club.api.util.item.SlotUtil;
import rina.onepop.club.api.util.math.PositionUtil;
import rina.onepop.club.api.util.math.RotationUtil;
import rina.onepop.club.api.util.world.BlockUtil;
import rina.onepop.club.client.event.client.RunTickEvent;
import rina.onepop.club.client.event.network.PacketEvent;
import rina.onepop.club.client.manager.network.Rotation;
import rina.onepop.club.client.manager.network.RotationManager;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

/**
 * @author SrRina
 * @since 31/05/2021 at 01:23
 *
 * Good night kisses!
 **/
@Registry(name = "Scaffold", tag = "Scaffold", description = "Automatically places a block in your feet.", category = ModuleCategory.PLAYER)
public class ModuleScaffold extends Module {
    /* Misc. */
    public static ValueBoolean settingNoForceRotate = new ValueBoolean("No Force Rotate", "NoForceRotate", "Prevents server rotation.", false);
    public static ValueBoolean settingRenderSwing = new ValueBoolean("Render Swing", "RenderSwing", "Render swing place.", false);
    public static ValueBoolean settingBoost = new ValueBoolean("Boost", "Boost", "Boost you for places block.", true);
    public static ValueBoolean settingBoostAir = new ValueBoolean("Boost Air", "BoostAir", "Ignore on ground for boost.", false);
    public static ValueNumber settingBoostY = new ValueNumber("Boost Y", "BoostY", "The boost value.", 300, 0, 420);
    public static ValueEnum settingRotate = new ValueEnum("Rotate", "Rotate", "Modes for rotate.", Rotation.SEND);

    private boolean withOffhand;
    private int blockSlot;

    private final EnumFacing[] incompleteFaces = {
            EnumFacing.DOWN,
            EnumFacing.NORTH,
            EnumFacing.SOUTH,
            EnumFacing.WEST,
            EnumFacing.EAST
    };

    @Override
    public void onSetting() {
        settingBoostAir.setEnabled(settingBoost.getValue());
        settingBoostY.setEnabled(settingBoost.getValue());
    }

    @Listener
    public void onPacketReceive(PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketPlayerPosLook && settingNoForceRotate.getValue()) {
            final SPacketPlayerPosLook packet = (SPacketPlayerPosLook) event.getPacket();

            packet.yaw = mc.player.rotationYaw;
            packet.pitch = mc.player.rotationPitch;
        }
    }

    @Listener
    public void onTick(RunTickEvent event) {
        if (NullUtil.isPlayerWorld()) {
            return;
        }

        this.withOffhand = mc.player.getHeldItemOffhand().getItem() instanceof ItemBlock;
        this.blockSlot = this.findFirstBlock();

        final BlockPos downBlock = PlayerUtil.getBlockPos().down();
        final BlockPos horizontalBlock = downBlock.offset(mc.player.getHorizontalFacing());

        if ((!this.withOffhand && this.blockSlot == -1) || mc.gameSettings.keyBindSneak.isKeyDown()) {
            return;
        }

        if (mc.gameSettings.keyBindJump.isKeyDown()) {
            if (this.checkPlace(downBlock, EnumFacing.WEST)) {
                this.doPlace(downBlock, EnumFacing.WEST);
            } else if (this.checkPlace(downBlock, EnumFacing.EAST)) {
                this.doPlace(downBlock, EnumFacing.EAST);
            } else if (this.checkPlace(downBlock, EnumFacing.SOUTH)) {
                this.doPlace(downBlock, EnumFacing.SOUTH);
            } else if (this.checkPlace(downBlock, EnumFacing.NORTH)) {
                this.doPlace(downBlock, EnumFacing.NORTH);
            } else if (this.checkPlace(downBlock, EnumFacing.DOWN)) {
                this.doPlace(downBlock, EnumFacing.DOWN);
            } else if (this.checkPlace(downBlock, EnumFacing.UP)) {
                this.doPlace(downBlock, EnumFacing.UP);
            }
        } else {
            if (this.checkPlace(downBlock, EnumFacing.WEST)) {
                this.doPlace(downBlock, EnumFacing.WEST);
            } else if (this.checkPlace(downBlock, EnumFacing.EAST)) {
                this.doPlace(downBlock, EnumFacing.EAST);
            } else if (this.checkPlace(downBlock, EnumFacing.SOUTH)) {
                this.doPlace(downBlock, EnumFacing.SOUTH);
            } else if (this.checkPlace(downBlock, EnumFacing.NORTH)) {
                this.doPlace(downBlock, EnumFacing.NORTH);
            } else if (this.checkPlace(downBlock, EnumFacing.DOWN)) {
                this.doPlace(downBlock, EnumFacing.DOWN);
            } else if (this.checkPlace(downBlock, EnumFacing.UP)) {
                this.doPlace(downBlock, EnumFacing.UP);
            }

            if (this.checkPlace(horizontalBlock, EnumFacing.WEST)) {
                this.doPlace(horizontalBlock, EnumFacing.WEST);
            } else if (this.checkPlace(horizontalBlock, EnumFacing.EAST)) {
                this.doPlace(horizontalBlock, EnumFacing.EAST);
            } else if (this.checkPlace(horizontalBlock, EnumFacing.SOUTH)) {
                this.doPlace(horizontalBlock, EnumFacing.SOUTH);
            } else if (this.checkPlace(horizontalBlock, EnumFacing.NORTH)) {
                this.doPlace(horizontalBlock, EnumFacing.NORTH);
            } else if (this.checkPlace(horizontalBlock, EnumFacing.DOWN)) {
                this.doPlace(horizontalBlock, EnumFacing.DOWN);
            } else if (this.checkPlace(horizontalBlock, EnumFacing.UP)) {
                this.doPlace(horizontalBlock, EnumFacing.UP);
            }
        }
    }

    public int findFirstBlock() {
        for (int i = 0; i < 9; i++) {
            final Item items = mc.player.inventory.getStackInSlot(i).getItem();

            if (items instanceof ItemBlock) {
                return i;
            }
        }

        return -1;
    }

    public boolean checkPlace(BlockPos place, EnumFacing facing) {
        final BlockPos offset = place.offset(facing);

        return BlockUtil.isAir(place) && !BlockUtil.isAir(offset);
    }

    public void doPlace(BlockPos place, EnumFacing facing) {
        final BlockPos offset = place.offset(facing);
        Vec3d hit = PositionUtil.calculateHitPlace(offset, facing.getOpposite());

        // I removed the facing, maybe it makes the place slow...?
        float facingX = 0.5f;
        float facingY = 0.5f;
        float facingZ = 0.5f;

        boolean flagSneak = BlockUtil.BLACK_LIST.contains(BlockUtil.getBlock(offset));

        float[] rotates = RotationUtil.getPlaceRotation(hit);

        // Send task!
        if (settingRotate.getValue() != Rotation.NONE /* || (this.counter.getCount(place) == null && settingRotate.getValue() == RotationManager.Rotation.SEND) */) {
            RotationManager.task(settingRotate.getValue(), rotates);
        }

        EnumHand hand = this.withOffhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND;

        if (!this.withOffhand) {
            mc.player.connection.sendPacket(new CPacketHeldItemChange(this.blockSlot));
        }

        if (flagSneak) {
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
        }

        mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(offset, facing.getOpposite(), hand, facingX, facingY, facingZ));
        mc.world.setBlockState(place, Block.getBlockFromItem(SlotUtil.getItem(this.blockSlot)).getBlockState().getBaseState());

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
    }
}
