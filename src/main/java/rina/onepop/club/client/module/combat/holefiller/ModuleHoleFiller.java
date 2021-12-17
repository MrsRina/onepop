package rina.onepop.club.client.module.combat.holefiller;

import me.rina.turok.util.TurokTick;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
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
import rina.onepop.club.Onepop;
import rina.onepop.club.api.ISLClass;
import rina.onepop.club.api.module.Module;
import rina.onepop.club.api.module.impl.ModuleCategory;
import rina.onepop.club.api.module.registry.Registry;
import rina.onepop.club.api.setting.value.ValueBoolean;
import rina.onepop.club.api.setting.value.ValueEnum;
import rina.onepop.club.api.setting.value.ValueNumber;
import rina.onepop.club.api.tool.CounterTool;
import rina.onepop.club.api.util.client.NullUtil;
import rina.onepop.club.api.util.entity.EntityUtil;
import rina.onepop.club.api.util.item.SlotUtil;
import rina.onepop.club.api.util.math.PositionUtil;
import rina.onepop.club.api.util.math.RotationUtil;
import rina.onepop.club.api.util.world.BlockUtil;
import rina.onepop.club.client.event.client.RunTickEvent;
import rina.onepop.club.client.event.network.PacketEvent;
import rina.onepop.club.client.manager.network.Rotation;
import rina.onepop.club.client.manager.network.RotationManager;
import rina.onepop.club.client.manager.world.BlockManager;
import rina.onepop.club.client.manager.world.HoleManager;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

import java.util.ArrayList;
import java.util.List;

/**
 * @author SrRina
 * @since 06/03/2021 at 00:17
 **/
@Registry(name = "Hole Filler", tag = "HoleFiller", description = "Automatically places blocks in hole.", category = ModuleCategory.COMBAT)
public class ModuleHoleFiller extends Module {
    /* Misc. */
    public static ValueBoolean settingAntiNaked = new ValueBoolean("Anti-Naked", "AntiNaked", "Preserve naked players.", true);
    public static ValueBoolean settingRenderSwing = new ValueBoolean("Render Swing", "RenderSwing", "Render swing!", true);
    public static ValueBoolean settingOffhand = new ValueBoolean("Offhand", "Offhand", "Take block from offhand also!", true);
    public static ValueBoolean settingNoForceRotate = new ValueBoolean("No Force Rotate", "NoForceRotate", "Prevents server rotation.", false);
    public static ValueBoolean settingEntityVerify = new ValueBoolean("Entity Verify", "EntityVerify", "Verify for places.", false);
    public static ValueBoolean settingSmart = new ValueBoolean("Smart", "Smart", "Fill when is important.", false);
    public static ValueBoolean settingRetrace = new ValueBoolean("Retrace", "Retrace", "Make the place more fast.", false);
    public static ValueNumber settingRangeTrace = new ValueNumber("Range Trace", "RangeTrace", "Range trace for get target.", 6f, 0f, 13f);
    public static ValueNumber settingMinimumRange = new ValueNumber("Minimum Range", "MinimumRange", "Target minimum range for fill hole.", 2f, 0f, 4f);
    public static ValueNumber settingPlaceRange = new ValueNumber("Place Range", "PlaceRange", "Place range.", 4f, 1f, 5f);
    public static ValueNumber settingSafeRange = new ValueNumber("Safe Range", "SafeRange", "Prevent dies from self fill.", 2f, 0f, 4f);
    public static ValueNumber settingRotationsCooldown = new ValueNumber("Rotations Cooldown", "RotationsCooldown", "Cooldown for rotations.", 4, 0, 6);
    public static ValueEnum settingMode = new ValueEnum("Mode", "Mode", "Modes for get block from inventory.", Mode.ALL);
    public static ValueEnum settingRotate = new ValueEnum("Rotate", "Rotate", "Rotates for you!", Rotation.SEND);

    private final CounterTool<BlockPos> counter = new CounterTool<>();
    private final TurokTick timeOut = new TurokTick();

    private boolean withOffhand;
    private int blockSlot;

    private EntityPlayer targetPlayer;
    private final List<BlockPos> packetSentList = new ArrayList<>();

    @Override
    public void onSetting() {
        settingMinimumRange.setEnabled(settingSmart.getValue());
        settingSafeRange.setMinimum(0f);
    }

    @Override
    public void onDisable() {
        this.timeOut.reset();
    }

    @Override
    public void onEnable() {
        this.timeOut.reset();
        this.packetSentList.clear();
    }

    @Listener
    public void onListen(PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketPlayerPosLook && settingNoForceRotate.getValue()) {
            final SPacketPlayerPosLook packet = (SPacketPlayerPosLook) event.getPacket();

            packet.yaw = ISLClass.mc.player.rotationYaw;
            packet.pitch = ISLClass.mc.player.rotationPitch;
        }
    }

    @Listener
    public void onTick(RunTickEvent event) {
        if (NullUtil.isPlayerWorld()) {
            return;
        }

        // uuv
        final int uuv = this.findFirstBlock();

        this.blockSlot = uuv;
        this.withOffhand = uuv == 999 && settingOffhand.getValue();

        if (this.blockSlot == -1 && !this.withOffhand) {
            this.timeOut.reset();

            return;
        }

        if (this.timeOut.isPassedMS(3 * 1000) || !settingRetrace.getValue()) {
            this.packetSentList.clear();
            this.timeOut.reset();
        }

        this.targetPlayer = EntityUtil.getTarget(settingRangeTrace.getValue().floatValue(), false, settingAntiNaked.getValue());

        for (HoleManager.Hole holes : Onepop.getHoleManager().getHoleList()) {
            if (this.targetPlayer == null || this.packetSentList.contains(holes.getPosition())) {
                return;
            }

            final BlockPos position = holes.getPosition();
            final boolean flagSafe = mc.player.getDistance(position.x, position.y, position.z) >= settingSafeRange.getValue().floatValue() || BlockManager.getAirSurroundPlayer().isEmpty();

            if ((!BlockUtil.isPlaceable(position) && ((this.withOffhand && mc.player.getHeldItemOffhand().getItem() != Item.getItemFromBlock(Blocks.WEB) || SlotUtil.findItemSlotFromHotBar(Item.getItemFromBlock(Blocks.WEB)) != this.blockSlot)) && settingEntityVerify.getValue()) || mc.player.getDistance(position.x, position.y, position.z) > settingPlaceRange.getValue().floatValue()) {
                continue;
            }

            if (this.counter.getCount(position) != null && this.counter.getCount(position) > settingRotationsCooldown.getValue().intValue()) {
                this.counter.remove(position);
            }

            if ((!settingSmart.getValue() || settingSmart.getValue() && EntityUtil.getDistance(this.targetPlayer.getPositionVector(), PositionUtil.toVec(position)) <= settingMinimumRange.getValue().floatValue()) && flagSafe && this.doPlace(position)) {
                this.packetSentList.add(holes.getPosition());

                continue;
            }
        }
    }

    public int findFirstBlock() {
        final Item itemOffhand = mc.player.getHeldItemOffhand().getItem();

        if (this.isBlock(itemOffhand) && settingOffhand.getValue()) {
            return 999;
        }

        for (int i = 0; i < 9; i++) {
            final Item items = mc.player.inventory.getStackInSlot(i).getItem();

            if (this.isBlock(items)) {
                return i;
            }
        }

        return -1;
    }

    public boolean isBlock(final Item item) {
        final boolean flag = settingMode.getValue() == Mode.ALL;

        return (item == Item.getItemFromBlock(Blocks.WEB) && settingMode.getValue() == Mode.OBSIDIAN) || (item == Item.getItemFromBlock(Blocks.ENDER_CHEST) && settingMode.getValue() == Mode.ENCHEST) || (item == Item.getItemFromBlock(Blocks.WEB) && settingMode.getValue() == Mode.WEB) || (item instanceof ItemBlock && flag);
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
                    mc.player.connection.sendPacket(new CPacketHeldItemChange(this.blockSlot));
                }

                if (flagSneak) {
                    mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
                }

                float[] rotates = RotationUtil.getPlaceRotation(hit);

                // Send task!
                if ((settingRotate.getValue() == Rotation.REL || settingRotate.getValue() == Rotation.LEGIT) || (this.counter.getCount(place) == null && settingRotate.getValue() == Rotation.SEND)) {
                    RotationManager.task(settingRotate.getValue(), rotates);
                }

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
                    mc.player.connection.sendPacket(new CPacketHeldItemChange(ISLClass.mc.player.inventory.currentItem));

                    if (mc.player.isHandActive() && mc.player.getActiveHand() == EnumHand.MAIN_HAND) {
                        mc.player.resetActiveHand();
                    }
                }

                state = true;

                break;
            }
        }

        return state;
    }
}
