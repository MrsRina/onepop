package rina.onepop.club.client.module.combat.selfweb;

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
import rina.onepop.club.Onepop;
import rina.onepop.club.api.ISLClass;
import rina.onepop.club.api.module.Module;
import rina.onepop.club.api.module.impl.ModuleCategory;
import rina.onepop.club.api.module.registry.Registry;
import rina.onepop.club.api.setting.value.ValueBoolean;
import rina.onepop.club.api.setting.value.ValueEnum;
import rina.onepop.club.api.setting.value.ValueNumber;
import rina.onepop.club.api.util.client.NullUtil;
import rina.onepop.club.api.util.entity.EntityUtil;
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
 * @since 22/05/2021 at 02:44
 **/
@Registry(name = "Self Web", tag = "SelfWeb", description = "Self places web.", category = ModuleCategory.COMBAT)
public class ModuleSelfWeb extends Module {
    /* Misc. */
    public static ValueBoolean settingAntiNaked = new ValueBoolean("Anti-Naked", "AntiNaked", "Anti naked option to smart mode.", true);
    public static ValueBoolean settingRenderSwing = new ValueBoolean("Render Swing", "RenderSwing", "Render swing.", false);
    public static ValueBoolean settingDisableAfter = new ValueBoolean("Disable After", "DisableAfter", "Disable after places web.", true);
    public static ValueBoolean settingOffhand = new ValueBoolean("Offhand", "Offhand", "Checks offhand.", true);
    public static ValueBoolean settingNoForceRotate = new ValueBoolean("No Force Rotate", "NoForceRotate", "Prevents rotation from server.", false);
    public static ValueNumber settingMinimumRange = new ValueNumber("Minimum Range", "MinimumRange", "Minimum range for self webs.", 1f, 0.5f, 4f);
    public static ValueEnum settingMode = new ValueEnum("Mode", "Mode", "Modes for webs logic.", Mode.SMART);
    public static ValueEnum settingRotate = new ValueEnum("Rotate", "Rotate", "Modes for rotates.", Rotation.SEND);

    private static final Item WEB = Item.getItemFromBlock(Blocks.WEB);

    private EntityPlayer targetPlayer;
    private boolean placed;

    private boolean withOffhand;
    private int webSlot;

    @Override
    public void onSetting() {
        settingAntiNaked.setEnabled(settingMode.getValue() == Mode.SMART);
        settingMinimumRange.setEnabled(settingMode.getValue() == Mode.SMART);
    }

    @Override
    public void onEnable() {
        this.targetPlayer = null;
    }

    @Override
    public void onDisable() {
        this.targetPlayer = null;
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

        this.webSlot = SlotUtil.findItemSlotFromHotBar(WEB);
        this.withOffhand = ISLClass.mc.player.getHeldItemOffhand().getItem() == WEB && settingOffhand.getValue();

        if ((this.webSlot == -1 && !this.withOffhand)) {
            this.setDisabled();

            return;
        }

        if (!ISLClass.mc.player.onGround) {
            return;
        }

        if (settingMode.getValue() == Mode.SMART) {
            this.targetPlayer = EntityUtil.getTarget(4f, false, settingAntiNaked.getValue());
        }

        this.placed = false;

        switch ((Mode) settingMode.getValue()) {
            case SMART: {
                if (this.targetPlayer != null && Onepop.getBlockManager().getPlayerSurroundBlockList().isEmpty() && EntityUtil.getDistance(this.targetPlayer) < settingMinimumRange.getValue().floatValue()) {
                    this.placed = this.doPlace(PlayerUtil.getBlockPos());

                    if (this.placed && settingDisableAfter.getValue()) {
                        this.setDisabled();
                    }
                }

                break;
            }

            case TOGGLE: {
                this.placed = this.doPlace(PlayerUtil.getBlockPos());

                if (this.placed && settingDisableAfter.getValue()) {
                    this.setDisabled();
                }

                break;
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
                    ISLClass.mc.player.connection.sendPacket(new CPacketHeldItemChange(this.webSlot));
                }

                if (flagSneak) {
                    mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
                }

                float[] rotates = RotationUtil.getPlaceRotation(hit);

                // Send task!
                if (settingRotate.getValue() != Rotation.NONE) {
                    RotationManager.task(settingRotate.getValue(), rotates);
                }

                EnumHand hand = this.withOffhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND;

                ISLClass.mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(offset, facing, hand, facingX, facingY, facingZ));

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

                state = true;

                break;
            }
        }

        return state;
    }
}
