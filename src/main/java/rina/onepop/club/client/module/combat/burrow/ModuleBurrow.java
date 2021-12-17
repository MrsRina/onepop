package rina.onepop.club.client.module.combat.burrow;

import me.rina.turok.util.TurokTick;
import rina.onepop.club.api.module.Module;
import rina.onepop.club.api.module.impl.ModuleCategory;
import rina.onepop.club.api.module.registry.Registry;
import rina.onepop.club.api.setting.value.ValueBoolean;
import rina.onepop.club.api.setting.value.ValueEnum;
import rina.onepop.club.api.setting.value.ValueNumber;
import rina.onepop.club.api.util.client.NullUtil;
import rina.onepop.club.api.util.item.SlotUtil;
import rina.onepop.club.api.util.math.PositionUtil;
import rina.onepop.club.api.util.math.RotationUtil;
import rina.onepop.club.api.util.world.BlockUtil;
import rina.onepop.club.client.event.client.RunTickEvent;
import rina.onepop.club.client.manager.network.Rotation;
import rina.onepop.club.client.manager.network.RotationManager;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

import java.util.ArrayList;
import java.util.List;

/**
 * @author SrRina
 * @since 04/05/2021 at 18:13
 **/
@Registry(name = "Burrow", tag = "Burrow", description = "Self place block!", category = ModuleCategory.COMBAT)
public class ModuleBurrow extends Module {
    public static ModuleBurrow INSTANCE;

    /* Misc. */
    public static ValueEnum settingMode = new ValueEnum("Mode", "Mode", "Modes for burrow.", Mode.TP);
    public static ValueEnum settingRotation = new ValueEnum("Rotation", "Rotation", "Modes for rotations.", Rotation.REL);
    public static ValueBoolean settingEnchest = new ValueBoolean("Enchest", "Enchest", "Also uses enchest to place block.", false);
    public static ValueBoolean settingSneak = new ValueBoolean("Sneak", "Sneak", "Sneak...?", true);
    public static ValueNumber settingTimeOut = new ValueNumber("Time Out",  "TimeOut", "Time out for stop possibles packets problems.", 3, 1, 10);

    /* Strict. */
    public static ValueNumber settingTaskSize = new ValueNumber("Task Size", "TaskSize", "Size limit for spoof player.", 50, 10, 250);

    /* Rubberband. */
    public static ValueBoolean settingExtraRubberband = new ValueBoolean("Extra Rubberband", "ExtraRubberband", "Apply extra rubberband for preserve fall!", true);
    public static ValueNumber settingRubberbandAmount = new ValueNumber("Rubberband Amount", "RubberbandAmount", "Rubberband amount!", 5f, -5f, 5f);

    public static final double JUMP_INCREASE_1 = 0.41999998688698;
    public static final double JUMP_INCREASE_2 = 0.7531999805211997;
    public static final double JUMP_INCREASE_3 = 1.00133597911214;
    public static final double JUMP_INCREASE_4 = 1.16610926093821;

    public static final Item OBSIDIAN_BLOCK = Item.getItemFromBlock(Blocks.OBSIDIAN);
    public static final Item ENCHEST_BLOCK = Item.getItemFromBlock(Blocks.ENDER_CHEST);

    private final TurokTick timeOut = new TurokTick();

    private boolean withOffhand;
    private int blockSlot;

    private final List<BlockPos> blockList = new ArrayList<>();
    private boolean reachedTask;

    public ModuleBurrow() {
        INSTANCE = this;
    }

    @Override
    public void onSetting() {
        settingTaskSize.setEnabled(settingMode.getValue() == Mode.STRICT);
        settingRubberbandAmount.setEnabled(settingExtraRubberband.getValue());
    }

    @Override
    public void onEnable() {
        this.blockList.clear();
        this.reachedTask = false;
        this.timeOut.reset();
    }

    @Override
    public void onDisable() {
        this.blockList.clear();
        this.reachedTask = false;
        this.timeOut.reset();
    }

    @Listener
    public void onTick(RunTickEvent event) {
        if (NullUtil.isPlayerWorld()) {
            return;
        }

        // :)
        final int uuv = this.findFirstBlock();

        this.blockSlot = uuv;
        this.withOffhand = uuv == 999;

        if (this.blockSlot == -1 || this.timeOut.isPassedSI(settingTimeOut.getValue().intValue())) {
            this.setDisabled();

            return;
        }

        final BlockPos offset = new BlockPos(Math.floor(mc.player.posX), Math.floor(mc.player.posY + 0.3f), Math.floor(mc.player.posZ));

        if (BlockUtil.getBlock(offset) != Blocks.AIR) {
            this.setDisabled();

            return;
        }

        if (settingMode.getValue() == Mode.TP) {
            this.setSneak();
            this.doPacket();
            this.doPlace(offset);
            this.doRubberband();
            this.unsetSneak();
        } else if (settingMode.getValue() == Mode.JUMP) {
            if (mc.player.onGround) {
                mc.player.jump();
            }

            if (mc.player.fallDistance > 0.0f) {
                this.setSneak();
                this.doPlace(offset.down());
                this.doRubberband();
                this.unsetSneak();
            }
        } else {
            if (!this.reachedTask) {
                this.reachedTask = true;
            } else {
                this.setSneak();
                this.doPlace(offset.down());

                if (mc.player.onGround) {
                    mc.player.jump();
                }

                if (mc.player.fallDistance > 0f) {
                    this.doRubberband();
                    this.unsetSneak();
                }
            }
        }
    }

    public void doPacket() {
        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + JUMP_INCREASE_1, mc.player.posZ, true));
        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + JUMP_INCREASE_2, mc.player.posZ, true));
        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + JUMP_INCREASE_3, mc.player.posZ, true));
        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + JUMP_INCREASE_4, mc.player.posZ, true));
    }

    public void doRubberband() {
        if (settingExtraRubberband.getValue()) {
            mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + settingRubberbandAmount.getValue().floatValue(), mc.player.posZ, true));
        }
    }

    public void setSneak() {
        if (settingSneak.getValue()) {
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
        }
    }

    public void unsetSneak() {
        if (settingSneak.getValue()) {
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
        }
    }

    public void doPlace(BlockPos place) {
        if (place == null) {
            return;
        }

        for (EnumFacing faces : EnumFacing.values()) {
            final BlockPos offset = place.offset(faces);
            final Block block = mc.world.getBlockState(offset).getBlock();

            if (block != Blocks.AIR) {
                EnumFacing facing = faces.getOpposite();
                Vec3d hit = PositionUtil.calculateHitPlace(offset, facing);

                float facingX = 0.5f;
                float facingY = 0.5f;
                float facingZ = 0.5f;

                boolean flagSneak = BlockUtil.BLACK_LIST.contains(block);

                float[] rotates = RotationUtil.getPlaceRotation(hit);

                if (!this.withOffhand) {
                    SlotUtil.setServerCurrentItem(this.blockSlot);
                }

                if (flagSneak) {
                    mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
                }

                // Send packet!
                RotationManager.task(settingRotation.getValue(), rotates);

                final EnumHand hand = this.withOffhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND;

                mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(offset, facing, hand, facingX, facingY, facingZ));
                mc.player.connection.sendPacket(new CPacketAnimation(hand));

                if (flagSneak) {
                    mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
                }

                SlotUtil.setServerCurrentItem(mc.player.inventory.currentItem);

                break;
            }
        }
    }

    public int findFirstBlock() {
        final Item itemOffhand = mc.player.getHeldItemOffhand().getItem();

        if (itemOffhand == OBSIDIAN_BLOCK || (itemOffhand == ENCHEST_BLOCK && settingEnchest.getValue())) {
            return 999;
        }

        for (int i = 0; i < 9; i++) {
            final Item items = mc.player.inventory.getStackInSlot(i).getItem();

            if (items == OBSIDIAN_BLOCK || (items == ENCHEST_BLOCK && settingEnchest.getValue())) {
                return i;
            }
        }

        return -1;
    }
}
