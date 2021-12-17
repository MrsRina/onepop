package rina.onepop.club.client.module.player.longjump;

import rina.onepop.club.api.ISLClass;
import rina.onepop.club.api.module.Module;
import rina.onepop.club.api.module.impl.ModuleCategory;
import rina.onepop.club.api.module.registry.Registry;
import rina.onepop.club.api.setting.value.ValueBoolean;
import rina.onepop.club.api.setting.value.ValueEnum;
import rina.onepop.club.api.setting.value.ValueNumber;
import rina.onepop.club.api.util.client.NullUtil;
import rina.onepop.club.client.event.entity.PlayerMoveEvent;
import rina.onepop.club.client.event.network.PacketEvent;
import net.minecraft.init.MobEffects;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.util.math.MathHelper;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

/**
 * @author SrRina
 * @since 15/05/2021 at 17:16
 *
 * TODO: Make this work... Still rollback as fuck, no sense for the rollback.
 **/
@Registry(name = "Long Jump", tag = "LongJump", description = "Uses damage to long jump.", category = ModuleCategory.PLAYER)
public class ModuleLongJump extends Module {
    public static ModuleLongJump INSTANCE;

    /* Misc. */
    public static ValueBoolean settingInstaDisable = new ValueBoolean("Insta Disable", "InstaDisable", "Insta disables when you jump.", false);
    public static ValueNumber settingIncreaseLength = new ValueNumber("Increase Length", "IncreaseLength", "Increase length for jump speed.", 0, 0, 1000);
    public static ValueEnum settingMode = new ValueEnum("Mode", "Mode", "Modes for jump.", Mode.INSTANT);
    public static ValueEnum settingCalculateMode = new ValueEnum("Calc. Mode", "CalculateMode", "Modes for calcule and apply force of long jump.", CalculateMode.BASE);

    private boolean ableJump;

    private int lastMotionX;
    private int lastMotionZ;

    public ModuleLongJump() {
        INSTANCE = this;
    }

    public boolean isAbleJump() {
        return ableJump;
    }

    @Override
    public void onSetting() {
        if (!this.isEnabled()) {
            this.ableJump = false;
        }

        settingIncreaseLength.setEnabled(settingMode.getValue() == Mode.INSTANT);
        settingCalculateMode.setEnabled(settingMode.getValue() == Mode.INSTANT);
    }

    @Listener
    public void onPacketReceive(PacketEvent.Receive event) {
        if (NullUtil.isPlayerWorld()) {
            return;
        }

        if (!(event.getPacket() instanceof SPacketEntityVelocity)) {
            return;
        }

        final SPacketEntityVelocity packet = (SPacketEntityVelocity) event.getPacket();

        if (packet.getEntityID() != mc.player.entityId) {
            return;
        }

        this.ableJump = settingMode.getValue() == Mode.INSTANT;

        this.lastMotionX = packet.getMotionX();
        this.lastMotionZ = packet.getMotionZ();

        double motionX = packet.getMotionX() / 8000.0D;
        double motionZ = packet.getMotionZ() / 8000.0D;

        if (settingMode.getValue() == Mode.MOTION) {
            mc.player.setVelocity(motionX, mc.player.motionY, motionZ);
        }

        if (settingInstaDisable.getValue() && settingMode.getValue() == Mode.MOTION) {
            this.setDisabled();
        }
    }

    @Listener
    public void onMove(PlayerMoveEvent event) {
        if (NullUtil.isPlayerWorld()) {
            return;
        }

        if (mc.player.isElytraFlying() || mc.player.capabilities.isFlying) {
            this.setDisabled();

            return;
        }

        if (settingCalculateMode.getValue() == CalculateMode.AIR) {
            if (mc.player.onGround) {
                return;
            }
        }

        if (!this.ableJump) {
            return;
        }

        if (settingMode.getValue() != Mode.INSTANT) {
            return;
        }

        double motionX = this.lastMotionX / 8000.0D;
        double motionZ = this.lastMotionZ / 8000.0D;

        double speed = 1.704780062794025d + (settingIncreaseLength.getValue().floatValue() / 100);

        if (mc.player.isPotionActive(MobEffects.SPEED)) {
            final int amplifier = mc.player.getActivePotionEffect(MobEffects.SPEED).getAmplifier();

            speed *= (1.0d + 0.1d * (amplifier + 1));
        }

        if (settingCalculateMode.getValue() == CalculateMode.AIR) {
            float playerRotationYaw = ISLClass.mc.player.rotationYaw;

            float playerForward = 1f;
            float playerStrafe = ISLClass.mc.player.movementInput.moveStrafe;

            event.setX((playerForward * speed) * Math.cos(Math.toRadians((playerRotationYaw + 90.0f))) + (playerStrafe * speed) * Math.sin(Math.toRadians((playerRotationYaw + 90.0f))));
            event.setZ((playerForward * speed) * Math.sin(Math.toRadians((playerRotationYaw + 90.0f))) - (playerStrafe * speed) * Math.cos(Math.toRadians((playerRotationYaw + 90.0f))));
        } else {
            if (mc.player.onGround) {
                float f = mc.player.rotationYaw * 0.017453292F;

                event.x -= MathHelper.sin(f) * (speed);
                event.z += MathHelper.cos(f) * (speed);
            }
        }

        this.ableJump = false;

        if (settingInstaDisable.getValue()) {
            this.setDisabled();
        }
    }
}
