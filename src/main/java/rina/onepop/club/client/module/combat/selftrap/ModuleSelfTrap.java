package rina.onepop.club.client.module.combat.selftrap;

import me.rina.turok.util.TurokTick;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import rina.onepop.club.api.ISLClass;
import rina.onepop.club.api.module.Module;
import rina.onepop.club.api.module.impl.ModuleCategory;
import rina.onepop.club.api.module.registry.Registry;
import rina.onepop.club.api.setting.value.ValueBoolean;
import rina.onepop.club.api.setting.value.ValueEnum;
import rina.onepop.club.api.setting.value.ValueNumber;
import rina.onepop.club.api.tool.CounterTool;
import rina.onepop.club.api.util.client.NullUtil;
import rina.onepop.club.api.util.item.SlotUtil;
import rina.onepop.club.api.util.math.PositionUtil;
import rina.onepop.club.api.util.math.RotationUtil;
import rina.onepop.club.api.util.world.BlockUtil;
import rina.onepop.club.client.event.client.RunTickEvent;
import rina.onepop.club.client.event.network.PacketEvent;
import rina.onepop.club.client.manager.network.Rotation;
import rina.onepop.club.client.manager.network.RotationManager;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

import java.util.ArrayList;
import java.util.List;

/**
 * @author SrRina
 * @since 22/05/2021 at 02:52
 **/
@Registry(name = "Self Trap", tag = "SelfTrap", description = "Self traps you.", category = ModuleCategory.COMBAT)
public class ModuleSelfTrap extends Module {
    /*
     * Fills all target round.
     */
    public static final BlockPos[] MASK_FULLY = {
            new BlockPos(-1, -1, 0),
            new BlockPos(1, -1, 0),
            new BlockPos(0, -1, -1),
            new BlockPos(0, -1, 1),
            new BlockPos(0, -1, 0),
            new BlockPos(-1, 0, 0),
            new BlockPos(1, 0, 0),
            new BlockPos(0, 0, -1),
            new BlockPos(0, 0, 1),
            new BlockPos(1, 1, 0),
            new BlockPos(-1, 1, 0),
            new BlockPos(0, 1, 1),
            new BlockPos(0, 1, -1),
            new BlockPos(1, 2, 0),
            new BlockPos(-1, 2, 0),
            new BlockPos(0, 2, 1),
            new BlockPos(0, 2, -1),
            new BlockPos(0, 2, 0)
    };

    /*
     * Fills not surrounding target...
     */
    public static final BlockPos[] MASK_CITY = {
            new BlockPos(-1, -1, 1),
            new BlockPos(1, -1, 1),
            new BlockPos(1, -1, -1),
            new BlockPos(-1, -1, -1),
            new BlockPos(0, -1, 0),
            new BlockPos(-1, 0, 1),
            new BlockPos(1, 0, 1),
            new BlockPos(1, 0, -1),
            new BlockPos(-1, 0, -1),
            new BlockPos(-1, 0, 1),
            new BlockPos(-1, 1, 1),
            new BlockPos(1, 1, 1),
            new BlockPos(1, 1, -1),
            new BlockPos(-1, 1, -1),
            new BlockPos(1, 1, 1),
            new BlockPos(1, 1, 0),
            new BlockPos(-1, 1, 0),
            new BlockPos(0, 1, 1),
            new BlockPos(0, 1, -1),
            new BlockPos(1, 2, 0),
            new BlockPos(-1, 2, 0),
            new BlockPos(0, 2, 1),
            new BlockPos(0, 2, -1),
            new BlockPos(0, 2, 0)
    };

    /* Misc. */
    public static ValueBoolean settingRenderSwing = new ValueBoolean("Render Swing", "RenderSwing", "Render swing place.", false);
    public static ValueBoolean settingNoForceRotate = new ValueBoolean("No Force Rotate", "NoForceRotate", "Prevents server rotation.", false);
    public static ValueBoolean settingRetrace = new ValueBoolean("Retrace", "Retrace", "Retrace packets.", false);

    public static ValueNumber settingTimeOut = new ValueNumber("Time Out", "TimeOut", "Time out for anti stuck.", 3, 1, 5);
    public static ValueNumber settingRotationsCooldown = new ValueNumber("Rotations Cooldown", "RotationsCooldown", "Cooldown for rotations .", 4, 0, 6);
    public static ValueNumber settingPlaceRange = new ValueNumber("Place Range", "PlaceRange", "Maximum place range.", 4f, 1f, 6f);

    public static ValueEnum settingMode = new ValueEnum("Mode", "Mode", "Mode of auto trap.", Mode.FULLY);
    public static ValueEnum settingRotate = new ValueEnum("Rotate", "Rotate", "Modes for rotate.", Rotation.REL);

    private final Item obsidian = Item.getItemFromBlock(Blocks.OBSIDIAN);
    private final TurokTick out = new TurokTick();
    private final TurokTick delay = new TurokTick();

    private boolean withOffhand;
    private int obsidianSlot;

    private final CounterTool<BlockPos> counter = new CounterTool<>();
    private EntityPlayer targetPlayer;

    private final List<BlockPos> packetSentList = new ArrayList<>();

    @Override
    public void onDisable() {
        this.out.reset();
        this.delay.reset();
    }

    @Override
    public void onEnable() {
        this.out.reset();
        this.delay.reset();
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

        this.withOffhand = ISLClass.mc.player.getHeldItemOffhand().getItem() == obsidian;
        this.obsidianSlot = SlotUtil.findItemSlotFromHotBar(obsidian);

        final BlockPos[] mask = ((Mode) settingMode.getValue()).getMask();

        if ((!this.withOffhand && this.obsidianSlot == -1) || this.out.isPassedSI(settingTimeOut.getValue().intValue())) {
            this.setDisabled();

            return;
        }

        this.targetPlayer = ISLClass.mc.player;

        if (this.targetPlayer == null) {
            return;
        }

        if (this.delay.isPassedMS(2 * 1000) || !settingRetrace.getValue()) {
            this.packetSentList.clear();
            this.delay.reset();
        }

        final BlockPos targetPosition = new BlockPos(Math.floor(this.targetPlayer.posX), Math.floor(this.targetPlayer.posY), Math.floor(this.targetPlayer.posZ));

        for (BlockPos adds : mask) {
            if (this.packetSentList.contains(adds)) {
                continue;
            }

            final BlockPos added = targetPosition.add(adds);

            if (ISLClass.mc.player.getDistance(added.x, added.y, added.z) < settingPlaceRange.getValue().floatValue() && BlockUtil.isAir(added) && BlockUtil.isPlaceableExcludingEntity(added)) {
                if (this.counter.getCount(added) != null && this.counter.getCount(added) > settingRotationsCooldown.getValue().intValue()) {
                    this.counter.remove(added);
                }

                if (this.doPlace(added)) {
                    this.packetSentList.add(adds);

                    continue;
                };
            }
        }
    }

    public boolean doPlace(BlockPos place) {
        boolean state = false;

        for (EnumFacing faces : EnumFacing.values()) {
            final BlockPos offset = place.offset(faces);
            final Block block = ISLClass.mc.world.getBlockState(offset).getBlock();

            if (block != Blocks.AIR) {
                final EnumFacing facing = faces.getOpposite();
                final Vec3d hit = PositionUtil.calculateHitPlace(offset, facing);

                // I removed the facing, maybe it makes the place slow...?
                float facingX = 0.5f;
                float facingY = 0.5f;
                float facingZ = 0.5f;

                boolean flagSneak = BlockUtil.BLACK_LIST.contains(block);

                if (!this.withOffhand) {
                    ISLClass.mc.player.connection.sendPacket(new CPacketHeldItemChange(this.obsidianSlot));
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
                    ISLClass.mc.player.swingArm(hand);
                } else {
                    ISLClass.mc.player.connection.sendPacket(new CPacketAnimation(hand));
                }

                if (flagSneak) {
                    mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
                }

                if (!this.withOffhand) {
                    ISLClass.mc.player.connection.sendPacket(new CPacketHeldItemChange(ISLClass.mc.player.inventory.currentItem));
                }

                this.counter.dispatch(place);

                state = true;

                break;
            }
        }

        return state;
    }
}
