package rina.onepop.club.client.module.player.strafe;

import me.rina.turok.util.TurokMath;
import me.rina.turok.util.TurokTick;
import net.minecraft.init.MobEffects;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.util.math.BlockPos;
import rina.onepop.club.api.ISLClass;
import rina.onepop.club.api.module.Module;
import rina.onepop.club.api.module.impl.ModuleCategory;
import rina.onepop.club.api.module.registry.Registry;
import rina.onepop.club.api.setting.value.ValueBoolean;
import rina.onepop.club.api.setting.value.ValueEnum;
import rina.onepop.club.api.setting.value.ValueNumber;
import rina.onepop.club.api.util.client.KeyUtil;
import rina.onepop.club.api.util.client.NullUtil;
import rina.onepop.club.client.event.client.RunTickEvent;
import rina.onepop.club.client.event.entity.PlayerMoveEvent;
import rina.onepop.club.client.event.network.PacketEvent;
import rina.onepop.club.client.module.player.longjump.ModuleLongJump;
import rina.onepop.club.client.module.player.straferewrite.CalculateMode;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

/**
 * @author SrRina
 * @since 23/02/2021 at 23:29
 **/
@Registry(name = "Strafe", tag = "Strafe", description = "Allows you control air movement.", category = ModuleCategory.PLAYER)
public class ModuleStrafe extends Module {
    public static BlockPos[] MASK = {
            new BlockPos(1, -1, 0),
            new BlockPos(-1, -1, 0),
            new BlockPos(1, -1, 1),
            new BlockPos(-1, -1, 1),
            new BlockPos(-1, -1, -1),
            new BlockPos(0, -1, 1),
            new BlockPos(0, -1, -1)
    };

    /* Misc. */
    public static ValueBoolean settingLegit = new ValueBoolean("Legit", "Legit", "Legit strafe movement.", false);
    public static ValueBoolean settingOnGround = new ValueBoolean("On Ground", "OnGround", "Control strafe on ground!", true);
    public static ValueBoolean settingFastControl = new ValueBoolean("Fast Control", "FastControls", "Make your air control very fast!", true);
    public static ValueNumber settingMaximumCounter = new ValueNumber("Maximum Counter", "MaximumCounter", "Maximum sprint speed.", 27, 0, 32);
    public static ValueNumber settingFlatFactory = new ValueNumber("Flat Factory", "FlatFactory", "Factory value for detect flat area.", 3, 0, 7);
    public static ValueNumber settingStrafe = new ValueNumber("Strafe", "Strafe", "Strafe value controller, sets 0 for disable.", 0, 0, 10f);

    /* Lowhop. */
    public static ValueBoolean settingLowhop = new ValueBoolean("Lowhop", "Lowhop", "Makes you lowhop!", false);

    /* Boost jump. */
    public static ValueBoolean settingBoostJump = new ValueBoolean("Boost Jump", "BoostJump", "Client boost you on first jump.", false);
    public static ValueNumber settingBoostJumpCooldown = new ValueNumber("Cooldown", "BoostJumpCooldown", "Cooldown for you get boost again.", 500, 0, 1000);
    public static ValueBoolean settingBoostJumpOnlyOneTime = new ValueBoolean("Only One Time", "BoostJumpOnlyOneTime", "Boost only one time.", true);
    public static ValueNumber settingBoostJumpDelay = new ValueNumber("Boost Jump Delay", "BoostJumpDelay", "Delay for reset cooldown.", 4, 1, 10);

    /* Boost on ground potion. */
    public static ValueEnum settingBoostOnGround = new ValueEnum("Boost Pot. Gro.", "BoostPotionGround", "Increase speed boosting on ground in effect speed.", BoostOnGround.NONE);
    public static ValueNumber settingBoostOnGroundSpeedClamp = new ValueNumber("Increase Speed", "BoostPotionGroundIncreaseSpeed", "Increases speed on ground.", 4000, 2873, 5000);
    public static ValueNumber settingBoostOnGroundStamp = new ValueNumber("Stamp", "BoostPotionGroundStamp", "Stamp for boost potion on ground.", 2000, 1, 3000);

    /* Boost explosion. */
    public static ValueEnum settingBoostExplosion = new ValueEnum("Boost", "Boost", "Boost you!", BoostType.SPEED);

    /* Potion. */
    public static ValueBoolean settingSpeedEffectAmpl = new ValueBoolean("Speed Effect Ampl", "SpeedEffectAmpl", "Increase speed with effects!", true);

    /* Flag speed. */
    public static ValueNumber settingFlagSpeed = new ValueNumber("Flag Speed", "FlagSpeed", "Speed of strafe flag.", 25, 0, 200);
    public static ValueNumber settingFlagRelease = new ValueNumber("Flag Release", "FlagRelease", "The speed for release flag.", 2873, 2873, 3500);

    /* Speed mode. */
    public static ValueEnum settingSpeedMode = new ValueEnum("Speed Mode", "SpeedMode", "Speed mode.", SpeedMode.NONE);

    /* Misc. */
    public static ValueEnum settingStrafingType = new ValueEnum("Strafing Type", "StrafingType", "Uses diff types of strafing calculation.", CalculateMode.LARGE);
    public static ValueEnum settingJumpMode = new ValueEnum("Jump Mode", "JumpMode", "Mode jump.", JumpMode.AUTO);

    private boolean isBoosting;

    private int motionX;
    private int motionZ;

    private double counterMovements;
    private int step;

    private double lastMovement;
    private double movement;

    private boolean unpress;
    private boolean underground;
    private boolean hasJump;
    private boolean hop;

    private final TurokTick cooldownHops = new TurokTick();
    private final TurokTick stampGround = new TurokTick();
    private final TurokTick stampPotionGround = new TurokTick();
    private final TurokTick waterToggle = new TurokTick();

    @Override
    public void onSetting() {
        settingFlagSpeed.setEnabled(settingSpeedMode.getValue() == SpeedMode.FLAG);
        settingBoostJumpCooldown.setEnabled(settingBoostJump.getValue());
        settingFlagRelease.setEnabled(settingSpeedMode.getValue() == SpeedMode.FLAG);
        settingBoostJumpOnlyOneTime.setEnabled(settingBoostJump.getValue());
        settingBoostJumpDelay.setEnabled(settingBoostJump.getValue() && !settingBoostJumpOnlyOneTime.getValue());
        settingBoostOnGroundSpeedClamp.setEnabled(settingBoostOnGround.getValue() != BoostOnGround.NONE);
        settingBoostOnGroundStamp.setEnabled(settingBoostOnGround.getValue() == BoostOnGround.STAMP);
        settingMaximumCounter.setEnabled(!settingLegit.getValue());
    }

    @Override
    public void onDisable() {
        mc.gameSettings.keyBindJump.pressed = false;
        KeyUtil.press(mc.gameSettings.keyBindJump, false);
    }

    @Listener
    public void onReceivePacket(PacketEvent.Receive event) {
        if (NullUtil.isPlayerWorld()) {
            return;
        }

        if (event.getPacket() instanceof SPacketEntityVelocity && ((SPacketEntityVelocity) event.getPacket()).entityID == mc.player.entityId && settingBoostExplosion.getValue() != BoostType.NONE) {
            SPacketEntityVelocity velocity = (SPacketEntityVelocity) event.getPacket();

            if (settingBoostExplosion.getValue() == BoostType.MOTION) {
                double velocityX = this.motionX / 8000.0D;
                double velocityZ = this.motionZ / 8000.0D;

                mc.player.setVelocity(velocityX, mc.player.motionY, velocityZ);
            }

            this.motionX = velocity.getMotionX();
            this.motionZ = velocity.getMotionZ();

            this.isBoosting = true;
        }
    }

    @Listener
    public void onListenClientTick(RunTickEvent event) {
        if (NullUtil.isPlayerWorld()) {
            return;
        }

        if (mc.player.onGround && this.hasJump) {
            this.movement = 0;
            this.hasJump = false;
        }
    }

    @Listener
    public void onListenPlayerMove(PlayerMoveEvent event) {
        if (NullUtil.isPlayerWorld()) {
            return;
        }

        if (mc.player.isInLava() || mc.player.isInWater()) {
            this.waterToggle.reset();
        }

        if (!this.waterToggle.isPassedMS(750) || mc.player.isSneaking() || mc.player.isElytraFlying() || mc.player.isOnLadder() || mc.player.isInWeb || mc.player.capabilities.isFlying || (ModuleLongJump.INSTANCE.isEnabled() && ModuleLongJump.INSTANCE.isAbleJump())) {
            return;
        }

        if (mc.player.onGround) {
            if (!settingOnGround.getValue()) {
                return;
            }
        } else {
            this.stampPotionGround.reset();
        }

        double sqrt = (Math.sqrt(event.getX() * event.getX() + event.getZ() * event.getZ()));
        double unlegit = 0.2873f;

        double value = (settingLegit.getValue() ? sqrt : unlegit);
        double speedHandler = value > 0.2873f ? value : 0.2873f;

        boolean speedEffect = false;
        double speedAmpl = 0f;

        if (mc.player.isPotionActive(MobEffects.SPEED) && settingSpeedEffectAmpl.getValue()) {
            final int amplifier = mc.player.getActivePotionEffect(MobEffects.SPEED).getAmplifier();

            speedAmpl = (1.0 + 0.1 * (amplifier + 1));
            speedEffect = true;
        }

        double speed = this.getSpeedPotion(speedEffect, speedAmpl, speedHandler);

        if (this.isBoosting) {
            double velocityX = this.motionX / 8000.0D;
            double velocityZ = this.motionZ / 8000.0D;

            speed += Math.sqrt(velocityX * velocityX + velocityZ * velocityZ);
        }

        float playerRotationYaw = ISLClass.mc.player.rotationYaw;
        float playerRotationPitch = ISLClass.mc.player.rotationPitch;

        float playerForward = ISLClass.mc.player.movementInput.moveForward;
        float playerStrafe = ISLClass.mc.player.movementInput.moveStrafe;

        // Now the strafe auto jump runs normally with speeds increases and sprint!
        if (mc.gameSettings.keyBindJump.pressed && settingJumpMode.getValue() == JumpMode.AUTO && !this.unpress) {
            mc.gameSettings.keyBindJump.pressed = false;

            this.unpress = true;
        }

        if (playerForward == 0.0d && playerStrafe == 0.0d) {
            event.setX(0d);
            event.setZ(0d);

            this.cooldownHops.reset();
            this.stampGround.reset();
            this.stampPotionGround.reset();
        } else {
            if (settingBoostJump.getValue()) {
                if (!this.cooldownHops.isPassedMS(settingBoostJumpCooldown.getValue().intValue())) {
                    this.hop = true;
                }

                if (!settingBoostJumpOnlyOneTime.getValue() && this.stampGround.isPassedSI(settingBoostJumpDelay.getValue().intValue())) {
                    this.cooldownHops.reset();
                }
            }

            if (playerForward != 0.0d && playerStrafe != 0.0d) {
                if (playerForward != 0.0d) {
                    if (playerStrafe > 0.0d) {
                        playerRotationYaw += (playerForward > 0.0d ? -45 : 45);
                    } else if (playerStrafe < 0d) {
                        playerRotationYaw += (playerForward > 0.0d ? 45 : -45);
                    }

                    playerStrafe = 0f;

                    if (playerForward > 0.0d) {
                        playerForward = 1.0f;
                    } else if (playerForward < 0) {
                        playerForward = -1.0f;
                    }
                }
            }
            
            if (mc.gameSettings.keyBindJump.isKeyDown() || settingJumpMode.getValue() == JumpMode.AUTO) {
                if (settingJumpMode.getValue() == JumpMode.AUTO && !mc.player.inWater) {
                    mc.gameSettings.keyBindJump.pressed = true;

                    this.unpress = false;
                }

                if (mc.player.onGround) {
                    if (settingFastControl.getValue() && !this.isBoosting) {
                        speed = 0.6174077f;
                    }

                    event.setY(mc.player.motionY = getMotionJumpY(0.40123128f));

                    this.hasJump = true;
                }
            }
        }

        switch ((CalculateMode) settingStrafingType.getValue()) {
            case MINIMAL: {
                event.setX((playerForward * speed) * Math.cos(Math.toRadians((playerRotationYaw + 90.0f))) + (playerStrafe * speed) * Math.sin(Math.toRadians((playerRotationYaw + 90.0f))));
                event.setZ((playerForward * speed) * Math.sin(Math.toRadians((playerRotationYaw + 90.0f))) - (playerStrafe * speed) * Math.cos(Math.toRadians((playerRotationYaw + 90.0f))));

                break;
            }

            case LARGE: {
                double x = Math.cos(Math.toRadians(playerRotationYaw + 90f));
                double z = Math.sin(Math.toRadians(playerRotationYaw + 90f));

                event.setX(playerForward * speed * x + playerStrafe * speed * z);
                event.setZ(playerForward * speed * z - playerStrafe * speed * x);

                break;
            }
        }

        this.isBoosting = false;
    }

    public double getSpeedPotion(boolean flag, double ampl, double speedbase) {
        final double policyValue = mc.player.isPotionActive(MobEffects.SPEED) ? (mc.player.getActivePotionEffect(MobEffects.SPEED).getAmplifier() + 1 >= 2 ? 0.3913 : 0.3393) : 1;

        double speed = flag ? speedbase * ampl : speedbase;
        double limiter = mc.player.onGround ? policyValue : 50f;

        if (settingBoostOnGround.getValue() != BoostOnGround.NONE && mc.player.onGround && flag) {
            if (settingBoostOnGround.getValue() == BoostOnGround.STAMP) {
                limiter = this.stampPotionGround.isPassedMS(settingBoostOnGroundStamp.getValue().intValue()) ? policyValue : Double.parseDouble("0." + settingBoostOnGroundSpeedClamp.getValue().intValue());
            }

            if (settingBoostOnGround.getValue() == BoostOnGround.STATIC) {
                limiter = Double.parseDouble("0." + settingBoostOnGroundSpeedClamp.getValue().intValue());
            }
        }

        return TurokMath.clamp(speed, 0.2873, limiter);
    }

    public float getMotionJumpY(float size) {
        float y = size;

        if (ISLClass.mc.player.isPotionActive(MobEffects.JUMP_BOOST)) {
            final int amplify = ISLClass.mc.player.getActivePotionEffect(MobEffects.JUMP_BOOST).getAmplifier();

            y += (amplify + 1) * 0.1f;
        }

        return y;
    }
}
