package rina.onepop.club.client.module.client.developer;

import me.rina.turok.util.TurokTick;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.BlockPos;
import rina.onepop.club.api.module.Module;
import rina.onepop.club.api.module.impl.ModuleCategory;
import rina.onepop.club.api.module.registry.Registry;
import rina.onepop.club.api.setting.value.ValueBoolean;
import rina.onepop.club.api.setting.value.ValueEnum;
import rina.onepop.club.api.setting.value.ValueString;
import rina.onepop.club.api.util.client.NullUtil;
import rina.onepop.club.api.util.client.StringUtil;
import rina.onepop.club.api.util.render.RenderUtil;
import rina.onepop.club.client.event.client.ClientTickEvent;
import rina.onepop.club.client.event.entity.EntityUpdateEvent;
import rina.onepop.club.client.event.network.PacketEvent;
import rina.onepop.club.client.manager.world.BlockManager;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

import java.awt.*;

/**
 * @author SrRina
 * @since 20/02/2021 at 00:18
 **/
@Registry(name = "Developer", tag = "Developer", description = "Test stuff!", category = ModuleCategory.CLIENT)
public class ModuleDeveloper extends Module {
    public static ValueEnum settingSector = new ValueEnum("|", "Sector", "Sector tests.", Sector.PLAYER);

    // Player.
    public static ValueBoolean settingPrintVelocity = new ValueBoolean("Print Velocity", "PrintVelocity", "Velocity player.", false);
    public static ValueString settingPrintSlot = new ValueString("Print Slot", "PrintSlot", "Debug specified slot from your all player inventory.", "int.");
    public static ValueBoolean settingSurround = new ValueBoolean("Surround", "Surround", "Render surround player.", false);

    // Rotations.
    public static ValueString settingDelay = new ValueString("Delay Rotations", "DelayRotations", "Delay of rotations.", "2000");
    public static ValueBoolean settingInterrupt = new ValueBoolean("Interrupt Rotations", "InterruptRotations", "Cancel non rotations.", false);
    public static ValueBoolean settingSend = new ValueBoolean("Send Rotations", "SendRotations", "Send rotations.", false);
    public static ValueBoolean settingRel = new ValueBoolean("Rel", "Rel", "Modify tick by tick the rotation.", false);
    public static ValueBoolean settingFix = new ValueBoolean("Fix Rotations", "FixRotations", "Fix the rotations.", false);

    private final CPacketPlayer onGroundPacket = new CPacketPlayer();
    private final CPacketPlayer.Position positionPacket = new CPacketPlayer.Position();
    private final CPacketPlayer.Rotation rotationPacket = new CPacketPlayer.Rotation();
    private final CPacketPlayer.PositionRotation positionRotationPacket = new CPacketPlayer.PositionRotation();

    private int lastUpdateTime = 2000;
    private double lastReportedPosX;
    private double lastReportedPosY;
    private double lastReportedPosZ;
    private double lastReportedYaw;
    private double lastReportedPitch;
    private boolean prevOnGround;
    private boolean autoJumpEnabled;
    private int positionUpdateTicks;

    private float yaw;
    private float pitch;

    private final TurokTick delayRotations = new TurokTick();
    private final TurokTick cooldown = new TurokTick();

    @Override
    public void onSetting() {
        settingPrintSlot.setEnabled(settingSector.getValue() == Sector.PLAYER);
        settingPrintVelocity.setEnabled(settingSector.getValue() == Sector.PLAYER);
        settingSurround.setEnabled(settingSector.getValue() == Sector.PLAYER);

        settingDelay.setEnabled(settingSector.getValue() == Sector.ROTATIONS);
        settingInterrupt.setEnabled(settingSector.getValue() == Sector.ROTATIONS);
        settingSend.setEnabled(settingSector.getValue() == Sector.ROTATIONS);
        settingRel.setEnabled(settingSector.getValue() == Sector.ROTATIONS);
        settingFix.setEnabled(settingSector.getValue() == Sector.ROTATIONS);

        this.lastUpdateTime = StringUtil.entryBoxNumber(settingDelay, this.lastUpdateTime).intValue();
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {
    }

    @Listener
    public void onSendPacket(PacketEvent.Send event) {
        if (event.getPacket() instanceof CPacketPlayer.Position) {
            CPacketPlayer.Position packet = (CPacketPlayer.Position) event.getPacket();

            packet.yaw = this.yaw;
            packet.pitch = this.pitch;

        } else if (event.getPacket() instanceof CPacketPlayer.Rotation) {
            CPacketPlayer.Rotation packet = (CPacketPlayer.Rotation) event.getPacket();

            packet.yaw = this.yaw;
            packet.pitch = this.pitch;

        } else if (event.getPacket() instanceof CPacketPlayer) {
            CPacketPlayer packet = (CPacketPlayer) event.getPacket();

            packet.yaw = this.yaw;
            packet.pitch = this.pitch;

        } else if (event.getPacket() instanceof CPacketPlayer.PositionRotation) {
            CPacketPlayer.PositionRotation packet = (CPacketPlayer.PositionRotation) event.getPacket();

            packet.yaw = this.yaw;
            packet.pitch = this.pitch;
        }
    }

    @Listener
    public void onListenClientTickEvent(ClientTickEvent event) {
        if (NullUtil.isPlayerWorld()) {
            return;
        }

        switch ((Sector) settingSector.getValue()) {
            case PLAYER: {
                if (settingPrintVelocity.getValue()) {
                    this.print("" + mc.player.motionX + " " + mc.player.motionY + " " + mc.player.motionZ);
                }

                try {
                    int value = Integer.parseInt(settingPrintSlot.getValue());
                } catch (NumberFormatException exc) {}

                break;
            }

            case ROTATIONS: {
                if (this.delayRotations.isPassedMS(this.lastUpdateTime)) {
                    this.yaw = 90;
                    this.pitch = 0;

                    this.delayRotations.reset();
                    this.cooldown.reset();
                }

                if (this.cooldown.isPassedMS(250)) {
                    this.yaw = mc.player.rotationYaw;
                    this.pitch = mc.player.rotationPitch;
                }

                break;
            }
        }
    }

    @Override
    public void onRender3D() {
        if (NullUtil.isPlayerWorld()) {
            return;
        }

        switch ((Sector) settingSector.getValue()) {
            case PLAYER: {
                for (BlockPos surround : BlockManager.getAirSurroundPlayer()) {
                    RenderUtil.drawSolidBlock(camera, surround, new Color(0, 255, 0, 100));
                }

                break;
            }
        }
    }

    @Listener
    public void onUpdateEvent(EntityUpdateEvent event) {
        if (settingSector.getValue() != Sector.ROTATIONS) {
            return;
        }

//        AxisAlignedBB axisalignedbb = mc.player.getEntityBoundingBox();
//        double d0 = this.yaw - this.lastReportedPosX;
//        double d1 = axisalignedbb.minY - this.lastReportedPosY;
//        double d2 = mc.player.posZ - this.lastReportedPosZ;
//        double d3 = (double)(this.yaw - this.lastReportedYaw);
//        double d4 = (double)(this.pitch - this.lastReportedPitch);
//
//        boolean flag2 = d0 * d0 + d1 * d1 + d2 * d2 > 9.0E-4D || this.positionUpdateTicks >= 20;
//        boolean flag3 = d3 != 0.0D || d4 != 0.0D;
//        if (mc.player.isRiding()) {
//        } else if (flag2 && flag3) {
//            this.positionRotationPacket.x = mc.player.posX;
//            this.positionRotationPacket.y = axisalignedbb.minY;
//            this.positionRotationPacket.z = mc.player.posZ;
//            this.positionRotationPacket.yaw = this.yaw;
//            this.positionRotationPacket.pitch = this.pitch;
//            this.positionRotationPacket.onGround = mc.player.onGround;
//
//            mc.player.connection.sendPacket(this.positionRotationPacket);
//        } else if (flag2) {
//            this.positionPacket.x = mc.player.posX;
//            this.positionPacket.y = axisalignedbb.minY;
//            this.positionPacket.z = mc.player.posZ;
//            this.positionPacket.onGround = mc.player.onGround;
//
//            mc.player.connection.sendPacket(positionPacket);
//        } else if (flag3) {
//            this.rotationPacket.yaw = this.yaw;
//            this.rotationPacket.pitch = this.pitch;
//            this.rotationPacket.onGround = mc.player.onGround;
//
//            mc.player.connection.sendPacket(this.rotationPacket);
//        } else if (this.prevOnGround != mc.player.onGround) {
//            this.onGroundPacket.onGround = mc.player.onGround;
//
//            mc.player.connection.sendPacket(this.onGroundPacket);
//        }
//
//        if (flag2) {
//            this.lastReportedPosX = mc.player.posX;
//            this.lastReportedPosY = axisalignedbb.minY;
//            this.lastReportedPosZ = mc.player.posZ;
//            this.positionUpdateTicks = 0;
//        }
//
//        if (flag3) {
//            this.lastReportedYaw = this.yaw;
//            this.lastReportedPitch = this.pitch;
//        }
//
//        this.prevOnGround = mc.player.onGround;
//        this.autoJumpEnabled = this.mc.gameSettings.autoJump;
    }
}
