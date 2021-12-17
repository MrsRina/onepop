package rina.onepop.club.client.module.combat.surround;

import net.minecraft.network.play.client.*;
import rina.onepop.club.api.module.Module;
import rina.onepop.club.api.module.impl.ModuleCategory;
import rina.onepop.club.api.module.registry.Registry;
import rina.onepop.club.api.setting.value.ValueBoolean;
import rina.onepop.club.api.setting.value.ValueEnum;
import rina.onepop.club.api.setting.value.ValueNumber;
import rina.onepop.club.api.tool.CounterTool;
import rina.onepop.club.api.util.client.NullUtil;
import rina.onepop.club.api.util.world.BlockUtil;
import rina.onepop.club.api.util.entity.PlayerUtil;
import rina.onepop.club.api.util.item.SlotUtil;
import rina.onepop.club.api.util.math.PositionUtil;
import rina.onepop.club.api.util.math.RotationUtil;
import rina.onepop.club.api.util.render.RenderUtil;
import rina.onepop.club.client.event.client.RunTickEvent;
import rina.onepop.club.client.event.network.PacketEvent;
import rina.onepop.club.client.manager.network.Rotation;
import rina.onepop.club.client.manager.network.RotationManager;
import rina.onepop.club.client.manager.world.BlockManager;
import me.rina.turok.util.TurokTick;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

import java.awt.*;
import java.util.List;

/**
 * @author SrRina
 * @since 11/02/2021 at 13:17
 **/
@Registry(name = "Surround", tag = "Surround", description = "Automatically places block around of you.", category = ModuleCategory.COMBAT)
public class ModuleSurround extends Module {
    /* Misc. */
    public static ValueBoolean settingGround = new ValueBoolean("Ground", "Ground", "Only ground places.", true);
    public static ValueBoolean settingAutoCenter = new ValueBoolean("Auto-Center", "AutoCenter", "Auto center players to correct place!", false);
    public static ValueBoolean settingOffhand = new ValueBoolean("Offhand", "Offhand", "Take obsidian from offhand also!", true);
    public static ValueBoolean settingNoForceRotate = new ValueBoolean("No Force Rotate", "NoForceRotate", "Prevents server rotation.", false);
    public static ValueBoolean settingRetrace = new ValueBoolean("Retrace", "Retrace", "Retrace for fast place.", true);
    public static ValueNumber settingRotationsCooldown = new ValueNumber("Rotations Cooldown", "RotationsCooldown", "Cooldown for rotations.", 4, 0, 6);
    public static ValueNumber settingTimeOut = new ValueNumber("Time Out", "TimeOut", "The time out delay for disable modules.", 3, 1, 5);
    public static ValueEnum settingRotate = new ValueEnum("Rotate", "Rotate", "Rotates for you!", Rotation.SEND);

    /* Render. */
    public static ValueBoolean settingRenderSwing = new ValueBoolean("Render Swing", "RenderSwing", "Render swing when places block!", true);
    public static ValueEnum settingRender = new ValueEnum("Render", "Render", "Render blocks around you before places.", Render.OUTLINE);
    public static ValueNumber settingRenderAlpha = new ValueNumber("Render Alpha", "RenderAlpha", "The alpha of render blocks.", 50, 0, 255);

    private final TurokTick delay = new TurokTick();
    private final TurokTick out = new TurokTick();

    private final CounterTool<BlockPos> counter = new CounterTool<>();
    private boolean centered;

    private boolean withOffhand;
    private int obsidianSlot;

    @Override
    public void onSetting() {
        settingRenderAlpha.setEnabled(settingRender.getValue() != Render.NONE);
    }

    @Override
    public void onRender3D() {
        if (NullUtil.isPlayerWorld() && settingRender.getValue() != Render.NONE) {
            return;
        }

        int alpha = settingRenderAlpha.getValue().intValue();

        for (BlockPos surround : BlockManager.getAirSurroundPlayer()) {
            switch ((Render) settingRender.getValue()) {
                case SOLID: {
                    RenderUtil.drawSolidBlock(camera, surround, new Color(0, 255, 0, alpha));

                    break;
                }

                case OUTLINE: {
                    RenderUtil.drawOutlineBlock(camera, surround, new Color(0, 255, 0, alpha));

                    break;
                }
            }
        }
    }

    @Override
    public void onEnable() {
        this.out.reset();
        this.delay.reset();
        this.centered = false;
        this.counter.clear();
    }

    @Override
    public void onDisable() {
        this.out.reset();
        this.delay.reset();
        this.counter.clear();
    }

    @Listener
    public void onListen(PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketPlayerPosLook && settingNoForceRotate.getValue()) {
            final SPacketPlayerPosLook packet = (SPacketPlayerPosLook) event.getPacket();

            packet.yaw = mc.player.rotationYaw;
            packet.pitch = mc.player.rotationPitch;
        }
    }

    @Listener
    public void onListen(RunTickEvent event) {
        if (NullUtil.isPlayerWorld()) {
            return;
        }

        this.obsidianSlot = SlotUtil.findItemSlotFromHotBar(Item.getItemFromBlock(Blocks.OBSIDIAN));
        this.withOffhand = mc.player.getHeldItemOffhand().getItem() == Item.getItemFromBlock(Blocks.OBSIDIAN) && settingOffhand.getValue();

        if ((this.obsidianSlot == -1 && !this.withOffhand) || BlockManager.getAirSurroundPlayer().isEmpty() || this.out.isPassedSI(settingTimeOut.getValue().intValue())) {
            this.setDisabled();

            return;
        }

        if (!this.centered && mc.player.onGround && settingAutoCenter.getValue()) {
            final BlockPos selfPos = PlayerUtil.getBlockPos();

            mc.player.connection.sendPacket(new CPacketPlayer.Position(selfPos.x + 0.5f, mc.player.posY, selfPos.z + 0.5f, mc.player.onGround));
            mc.player.setPosition(selfPos.x + 0.5f, mc.player.posY, selfPos.z + 0.5f);

            this.centered = true;
        }

        final List<BlockPos> maskSurround = BlockManager.getAirSurroundPlayer();

        if (!mc.player.onGround && settingGround.getValue()) {
            return;
        }

        for (BlockPos places : maskSurround) {
            if (this.counter.getCount(places) != null && this.counter.getCount(places) > settingRotationsCooldown.getValue().intValue()) {
                this.counter.remove(places);
            }

            if (this.doPlace(places)) {
                continue;
            }
        }
    }

    public boolean doPlace(BlockPos place) {
        boolean state = false;

        for (EnumFacing faces : EnumFacing.values()) {
            final BlockPos offset = place.offset(faces);
            final Block block = mc.world.getBlockState(offset).getBlock();

            if (block != Blocks.AIR) {
                final EnumFacing facing = faces.getOpposite();
                final Vec3d hit = PositionUtil.calculateHitPlace(offset, facing);

                // I removed the facing, maybe it makes the place slow...?
                float facingX = 0.5f;
                float facingY = 0.5f;
                float facingZ = 0.5f;

                boolean flagSneak = BlockUtil.BLACK_LIST.contains(block);

                if (!this.withOffhand) {
                    mc.player.connection.sendPacket(new CPacketHeldItemChange(this.obsidianSlot));
                }

                float[] rotates = RotationUtil.getPlaceRotation(hit);

                // Send task!
                if ((settingRotate.getValue() == Rotation.REL || settingRotate.getValue() == Rotation.LEGIT) || (this.counter.getCount(place) == null && settingRotate.getValue() == Rotation.SEND)) {
                    RotationManager.task(settingRotate.getValue(), rotates);
                }

                EnumHand hand = this.withOffhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND;

                if (flagSneak) {
                    mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
                }

                mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(offset, facing, hand, facingX, facingY, facingZ));

                if (settingRetrace.getValue()) {
                    mc.world.setBlockState(place, Blocks.OBSIDIAN.getBlockState().getBaseState());
                }

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

                this.counter.dispatch(place);

                state = true;

                break;
            }
        }

        return state;
    }
}