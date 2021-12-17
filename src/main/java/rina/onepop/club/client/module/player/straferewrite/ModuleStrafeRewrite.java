package rina.onepop.club.client.module.player.straferewrite;

import me.rina.turok.util.TurokTick;
import net.minecraft.init.MobEffects;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.input.Keyboard;
import rina.onepop.club.api.module.Module;
import rina.onepop.club.api.module.impl.ModuleCategory;
import rina.onepop.club.api.module.registry.Registry;
import rina.onepop.club.api.setting.value.ValueBind;
import rina.onepop.club.api.setting.value.ValueBoolean;
import rina.onepop.club.api.setting.value.ValueEnum;
import rina.onepop.club.api.setting.value.ValueNumber;
import rina.onepop.club.api.util.client.KeyUtil;
import rina.onepop.club.api.util.client.NullUtil;
import rina.onepop.club.api.util.entity.PlayerUtil;
import rina.onepop.club.api.util.world.BlockUtil;
import rina.onepop.club.client.event.client.RunTickEvent;
import rina.onepop.club.client.event.entity.PlayerJumpEvent;
import rina.onepop.club.client.event.entity.PlayerMoveEvent;
import rina.onepop.club.client.event.entity.TravelEvent;
import rina.onepop.club.client.event.network.PacketEvent;
import rina.onepop.club.client.module.player.ModuleStep;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

/**
 * @author SrRina
 * @since 02/10/2021 at 12:22
 **/
@Registry(name = "Strafe Rewrite", tag = "StrafeRewrite", description = "Make your strafing better.", category = ModuleCategory.PLAYER)
public class ModuleStrafeRewrite extends Module {
	// Misc.
	public static ValueNumber settingSpeedFactor = new ValueNumber("Speed Factor", "SpeedFactor", "Sets the speed by number.", 20, 0, 30);
	public static ValueNumber settingSpeedRange = new ValueNumber("Speed Range", "SpeedRange", "Sets the range for speed up. (2873 - 10k)", 3448, 3000, 5000);
	public static ValueNumber settingSpeedLimiter = new ValueNumber("Speed Limiter", "SpeedLimiter", "Speed of limiter.", 2, 0, 30);
	public static ValueEnum settingMode = new ValueEnum("Mode", "Mode", "Mode", Mode.HOP);
	public static ValueBind settingLargeJump = new ValueBind("PotionAmpl", "PotionAmpl", "Active potion ampl.", -1);
	public static ValueNumber settingSpeedI = new ValueNumber("Speed I", "SpeedI", "Set speed of strafe when speed I (potion).", 0.04f, 0f, 0.2f);
	public static ValueNumber settingSpeedII = new ValueNumber("Speed II", "SpeedII", "Set speed of strafe when speed II (potion).", 0.08f, 0f, 0.2f);
	public static ValueNumber settingStrafe = new ValueNumber("Strafe", "Strafe", "Strafe.", 0.49f, 0f, 2f);
	public static ValueNumber settingStrafeI = new ValueNumber("Str. Speed I", "StrafeSpeedI", "Set the strafe when speed I (potion).", 0.67f, 0f, 2f);
    public static ValueNumber settingStrafeII = new ValueNumber("Str. Speed II", "StrafeSpeedII", "Set the strafe when speed II (potion).", 0.78f, 0f, 2f);
	public static ValueBoolean settingAntiFlatRollback = new ValueBoolean("Anti-Flat-Rollback", "AntiFlatRollback", "Prevent rollbacks when you are running on flat.", true);
    public static ValueBoolean settingGround = new ValueBoolean("Ground", "Ground", "Jump lower.", true);
	public static ValueBoolean settingStep = new ValueBoolean("Step", "Step", "Steps help.", true);
	public static ValueBoolean settingAutoJump = new ValueBoolean("Auto-Jump", "AutoJump", "Make jumps automatically.", true);
	public static ValueBoolean settingStrict = new ValueBoolean("Horizontal Collision", "HorizontalCollision", "Verify horizontal collision.", false);
	public static ValueEnum settingBoost = new ValueEnum("Boost", "Boost", "Boost you.", Boost.HERMES);
	public static ValueEnum settingCalculateMode = new ValueEnum("Calc. Mode", "CalculateMode", "Set the strafing type.", CalculateMode.MINIMAL);

	private final TurokTick waterCooldown = new TurokTick();
	private BlockPos lastPlayerPosition;

	protected int movementInput;
	protected int lastMovementInput;

	protected double speedParsed;
	protected double speedLimiterParsed;
	protected double lastSpeedUpdateTick;

	protected boolean walking;
	protected boolean updating;

	protected boolean wasJump;
	protected boolean requiredAddition;

	protected int nonOnGroundTicks;
	protected boolean nonPotentiallyRollback;

	protected double additionX;
	protected double additionZ;

	protected double speed;

	protected int lastTickInfo;
	protected int tickInfo;

	protected int intentionalHighSpeedDetectionTicks;
	protected int rollbackCounter;

	@Override
	public void onSetting() {
		settingStrafe.setEnabled(settingMode.getValue() != Mode.LEGIT);
		settingStrafeI.setEnabled(settingMode.getValue() != Mode.LEGIT);
		settingStrafeII.setEnabled(settingMode.getValue() != Mode.LEGIT);

		if (this.lastTickInfo > 1000) {
			this.print("AUTO-JUMP Minecraft option is enabled, please disable.");
			this.tickInfo = 0;
		}

		if (mc.gameSettings.autoJump) {
			this.lastTickInfo++;
		}
	}

	@Override
	public void onDisable() {
		mc.gameSettings.keyBindJump.pressed = false;

		KeyUtil.press(mc.gameSettings.keyBindJump, false);
	}

	@Listener
	public void onReceivePacket(PacketEvent.Receive event) {
		if (event.getPacket() instanceof SPacketEntityVelocity) {
			final SPacketEntityVelocity packet = (SPacketEntityVelocity) event.getPacket();

			if (packet.getEntityID() == mc.player.entityId) {
				this.additionX = packet.getMotionX();
				this.additionZ = packet.getMotionZ();

				this.setRequiredAddition();
			}
		}
	}

	@Listener
    public void onJumpEvent(PlayerJumpEvent event) {
	    if (mc.player.onGround && this.waterCooldown.isPassedMS(750) && (mc.player.moveForward != 0f || mc.player.moveStrafing != 0f)) {
	        event.setCanceled(true);
        }
    }

	@Listener
	public void onTravel(TravelEvent event) {
		if (NullUtil.isPlayerWorld()) {
			return;
		}

		if (this.waterCooldown.isPassedMS(750)) {
			KeyUtil.press(mc.gameSettings.keyBindJump, false);
			mc.gameSettings.keyBindJump.pressed = false;
		}

		if (!this.waterCooldown.isPassedMS(750) || (mc.player.collidedHorizontally && settingStrict.getValue()) || (mc.player.moveForward == 0f && mc.player.moveStrafing == 0f) || mc.player.isInWater() || mc.player.isInLava() || mc.player.isInWeb || mc.player.isElytraFlying() || mc.player.isOnLadder() || mc.player.isSneaking() || mc.player.capabilities.isFlying) {
			return;
		}

		if (!this.wasJump && (settingAutoJump.getValue() || Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode())) && mc.player.onGround && this.movementInput <= 1) {
			this.wasJump = true;

			if (mc.player.isSprinting() && (!this.requiredAddition || settingBoost.getValue() == Boost.OFF)) {
				float f = mc.player.rotationYaw * 0.017453292F;
				float s = (float) this.speedParsed;

				double x = s;

				if (mc.player.isPotionActive(MobEffects.SPEED)) {
					int i = mc.player.getActivePotionEffect(MobEffects.SPEED).getAmplifier();

					x = s * (1.0 + 0.1 * (i + 1));
				}

				mc.player.motionX -= MathHelper.sin(f) * x;
				mc.player.motionZ += MathHelper.cos(f) * x;
			}

			mc.player.motionY = this.getMotionY(settingGround.getValue() ? 0.3999f : 0.41f);

			this.nonOnGroundTicks = 1;
			this.lastMovementInput = 5;

			this.updating = true;
			this.lastPlayerPosition = PlayerUtil.getBlockPos();
		}

		this.movementInput--;
	}

	@Listener
	public void onTickEvent(RunTickEvent event) {
		if (NullUtil.isPlayerWorld()) {
			return;
		}

		if (this.lastSpeedUpdateTick >= 2) {
			try {
				this.speedParsed = Double.parseDouble("0." + settingSpeedFactor.getValue().intValue());
			} catch (NumberFormatException exc) {
			}

			try {
				this.speedLimiterParsed = Double.parseDouble("0." + settingSpeedRange.getValue().intValue());
			} catch (NumberFormatException exc) {
			}

			this.lastSpeedUpdateTick = 0;
		}

		this.lastSpeedUpdateTick++;

		if (this.wasJump) {
			if (mc.player.fallDistance != 0f && this.nonOnGroundTicks == 1) {
				if (settingMode.getValue() == Mode.HOP) {
                    float s = !mc.player.isPotionActive(MobEffects.SPEED) ? settingStrafe.getValue().floatValue() : (mc.player.getActivePotionEffect(MobEffects.SPEED).getAmplifier() + 1 >= 2 ? settingStrafeII.getValue().floatValue() : settingStrafeI.getValue().floatValue());

                    mc.player.motionY -= (s * 0.1);

                    this.nonPotentiallyRollback = this.isSafeFromRollback();

					if (this.nonPotentiallyRollback && (!this.requiredAddition || settingBoost.getValue() == Boost.OFF) && mc.player.isPotionActive(MobEffects.SPEED) && mc.player.isSprinting() && this.updating && settingLargeJump.getValue()) {
						float f = mc.player.rotationYaw * 0.017453292F;
						s = !mc.player.isPotionActive(MobEffects.SPEED) ? 0f : (mc.player.getActivePotionEffect(MobEffects.SPEED).getAmplifier() + 1 >= 2 ? settingSpeedII.getValue().floatValue() : settingSpeedI.getValue().floatValue());

						mc.player.motionX -= (double) (MathHelper.sin(f) * s);
						mc.player.motionZ += (double) (MathHelper.cos(f) * s);

						this.updating = false;
					}

					this.nonOnGroundTicks++;
				} else if (settingMode.getValue() == Mode.CROUCH) {
                    float s = !mc.player.isPotionActive(MobEffects.SPEED) ? settingStrafe.getValue().floatValue() : (mc.player.getActivePotionEffect(MobEffects.SPEED).getAmplifier() + 1 >= 2 ? settingStrafeII.getValue().floatValue() : settingStrafeI.getValue().floatValue());

                    mc.player.motionY -= (s * 0.1);

					this.nonPotentiallyRollback = this.isSafeFromRollback();

					if (this.nonPotentiallyRollback && (!this.requiredAddition || settingBoost.getValue() == Boost.OFF) && mc.player.isPotionActive(MobEffects.SPEED) && mc.player.isSprinting() && this.updating && settingLargeJump.getValue()) {
						float f = mc.player.rotationYaw * 0.017453292F;
                        s = !mc.player.isPotionActive(MobEffects.SPEED) ? settingSpeedLimiter.getValue().floatValue() : (mc.player.getActivePotionEffect(MobEffects.SPEED).getAmplifier() + 1 >= 2 ? settingSpeedII.getValue().floatValue() : settingSpeedI.getValue().floatValue());

                        mc.player.motionX -= (double) (MathHelper.sin(f) * s);
                        mc.player.motionZ += (double) (MathHelper.cos(f) * s);

						this.updating = false;
					}
				}
			}
		}

		if (mc.player.onGround) {
			this.wasJump = false;
		}
	}

	@Listener
	public void onPlayerMoveEvent(PlayerMoveEvent event) {
		this.onUpdateMovement(event);
	}

	public void onUpdateMovement(PlayerMoveEvent event) {
		if (mc.player.isInWater() || mc.player.isInLava()) {
			this.waterCooldown.reset();
		}

		if (!this.waterCooldown.isPassedMS(750) || mc.player.isInWater() || mc.player.isInLava() || mc.player.isInWeb || mc.player.isElytraFlying() || mc.player.isOnLadder() || (mc.player.isSneaking() || (mc.player.isSneaking() && settingMode.getValue() == Mode.CROUCH && mc.player.onGround)) || mc.player.capabilities.isFlying) {
			return;
		}

		if (settingStep.getValue() && !ModuleStep.INSTANCE.isEnabled()) {
			mc.player.stepHeight = 0.6f;
		}

		// x^2 + z^2
		double sqrt = Math.sqrt(event.getX() * event.getX() + event.getZ() * event.getZ());
		double theSpeed = 0.2873;

		if (mc.player.isPotionActive(MobEffects.SPEED)) {
			int i = mc.player.getActivePotionEffect(MobEffects.SPEED).getAmplifier();
			double k = i + 1 >= 2 ? 0.3913 : 0.3393;

			theSpeed = k;
		}

		boolean flag = !mc.player.isPotionActive(MobEffects.SPEED) && (!this.requiredAddition || settingBoost.getValue() == Boost.OFF) && sqrt > this.speedLimiterParsed;
		this.speed = sqrt > theSpeed ? (flag ? sqrt + (settingSpeedLimiter.getValue().doubleValue() / 1000) : sqrt) : theSpeed;

		this.applyAirControl(event);
		this.unsetRequiredAddition();
	}

	public void applyAirControl(PlayerMoveEvent event) {
		float forward = mc.player.movementInput.moveForward;
		float strafe = mc.player.movementInput.moveStrafe;

		float yaw = mc.player.rotationYaw;

		boolean cancelSet = false;

		if (forward == 0.0f && strafe == 0.0f) {
			event.setX(0.0d);
			event.setZ(0.0d);

			cancelSet = true;
			this.walking = false;
		} else {
			this.walking = true;

			if (forward != 0.0f && strafe != 0.0f) {
				if (forward != 0.0f) {
					if (strafe > 0.0f) {
						yaw += (forward > 0.0d ? -45 : 45);
					} else if (strafe < 0.0f) {
						yaw += (forward > 0.0d ? 45 : -45);
					}

					strafe = 0;

					if (forward > 0.0f) {
						forward = 1.0f;
					} else if (forward < 0.0f) {
						forward = -1.0f;
					}
				}
			}
		}

		if (this.requiredAddition && settingBoost.getValue() == Boost.HERMES) {
			double mx = this.additionX / 8000.0D;
			double mz = this.additionZ / 8000.0D;

			this.speed += Math.sqrt(mx * mx + mz * mz);
		}

		switch ((CalculateMode) settingCalculateMode.getValue()) {
			case MINIMAL: {
				event.setX((forward * this.speed) * Math.cos(Math.toRadians((yaw + 90.0f))) + (strafe * this.speed) * Math.sin(Math.toRadians((yaw + 90.0f))));
				event.setZ((forward * this.speed) * Math.sin(Math.toRadians((yaw + 90.0f))) - (strafe * this.speed) * Math.cos(Math.toRadians((yaw + 90.0f))));

				mc.player.motionX = event.x;
				mc.player.motionZ = event.z;

				break;
			}

			case LARGE: {
				double x = Math.cos(Math.toRadians(yaw + 90f));
				double z = Math.sin(Math.toRadians(yaw + 90f));

				event.setX(forward * this.speed * x + strafe * this.speed * z);
				event.setZ(forward * this.speed * z - strafe * this.speed * x);

				mc.player.motionX = event.x;
				mc.player.motionZ = event.z;

				break;
			}
		}
	}

	public void setRequiredAddition() {
		this.requiredAddition = true;
	}

	public void unsetRequiredAddition() {
		this.requiredAddition = false;
	}

	public float getMotionY(float motion) {
		if (mc.player.isPotionActive(MobEffects.JUMP_BOOST)) {
			motion += (mc.player.getActivePotionEffect(MobEffects.JUMP_BOOST).getAmplifier() + 1) * 0.1f;
		}

		return motion;
	}

	public boolean isSafeFromRollback() {
		if (!settingAntiFlatRollback.getValue() || this.lastPlayerPosition != null) {
			return true;
		}

		BlockPos playerPosition = PlayerUtil.getBlockPos().down();

		return playerPosition.y - 1 < this.lastPlayerPosition.y - 1 || !BlockUtil.isAir(new BlockPos(playerPosition.x, this.lastPlayerPosition.y - 1, playerPosition.z));
	}
}