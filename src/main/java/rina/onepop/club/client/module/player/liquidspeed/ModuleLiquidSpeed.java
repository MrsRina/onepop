package rina.onepop.club.client.module.player.liquidspeed;

import rina.onepop.club.api.ISLClass;
import rina.onepop.club.api.module.Module;
import rina.onepop.club.api.module.impl.ModuleCategory;
import rina.onepop.club.api.module.registry.Registry;
import rina.onepop.club.api.setting.value.ValueEnum;
import rina.onepop.club.api.setting.value.ValueNumber;
import rina.onepop.club.api.util.client.NullUtil;
import rina.onepop.club.api.util.entity.PlayerUtil;
import rina.onepop.club.api.util.world.BlockUtil;
import rina.onepop.club.client.event.entity.PlayerMoveEvent;
import net.minecraft.block.BlockLiquid;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

/**
 * @author SrRina
 * @since 28/05/2021 at 00:36
 *
 * This module is a basic air strafe, I want to improve the strafe onepop with a good
 * control + speed (not much because, strafe != speed), at latest, I need understand
 * very well how works bypass/strafe/speed for make packet fly and phase.
 *
 * I'm talking here in liquid, because this module is hard as fuck to do,
 * I still didn't finish the module.
 *
 **/
@Registry(name = "Liquid Speed", tag = "LiquidSpeed", description = "Improves your movements in water/lava.", category = ModuleCategory.PLAYER)
public class ModuleLiquidSpeed extends Module {
    /* Misc. */
    public static ValueNumber settingSpeed = new ValueNumber("Speed", "Speed", "The speed of movement.", 10, 0, 200);
    public static ValueNumber settingSpeedY = new ValueNumber("Speed Y", "SpeedY", "Speed increase for y movement at liquid.", 0, 0, 200);
    public static ValueEnum settingMode = new ValueEnum("Mode", "Mode", "Modes for speed in liquid.", Mode.BOOST);

    @Listener
    public void onMove(PlayerMoveEvent event) {
        if (NullUtil.isPlayerWorld()) {
            return;
        }

        if (!(mc.player.isInWater() || mc.player.isInLava())) {
            return;
        }

        if (settingMode.getValue() == Mode.BOOST) {
            double sqrt = Math.sqrt(event.getX() * event.getX() + event.getZ() * event.getZ());
            double speed = sqrt >= 0.2f ? sqrt + (settingSpeed.getValue().intValue() / 1000f) : 0.2f;

            float f = mc.player.rotationYaw * 0.017453292F;

            //if (mc.gameSettings.keyBindJump.isKeyDown()) {
            //    event.setY(mc.player.motionY += 0.03999999910593033D + (settingSpeedY.getValue().intValue() / 1000f));
            //}

            mc.player.setSprinting(mc.player.moveForward != 0.0d);

            if (mc.player.moveForward > 0) {
                event.x -= MathHelper.sin(f) * (speed);
                event.z += MathHelper.cos(f) * (speed);
            }

            if (mc.player.moveForward < 0) {
                event.x -= -(MathHelper.sin(f) * (speed));
                event.z += -(MathHelper.cos(f) * (speed));
            }
        } else if (settingMode.getValue() == Mode.STATIC) {
            final BlockPos selfPosition = PlayerUtil.getBlockPos();

            if (!(BlockUtil.getBlock(selfPosition) instanceof BlockLiquid && !(BlockUtil.getBlock(selfPosition.up()) instanceof BlockLiquid))) {
                event.setY(mc.player.motionY = 0d);
            }

            if (mc.gameSettings.keyBindJump.isKeyDown()) {
                event.setY(mc.player.motionY += 0.03999999910593033D + (settingSpeedY.getValue().intValue() / 1000f));
            }

            if (mc.gameSettings.keyBindSneak.isKeyDown()) {
                event.setY(mc.player.motionY -= 0.03999999910593033D + (settingSpeedY.getValue().intValue() / 1000f));
            }

            double sqrt = Math.sqrt(event.getX() * event.getX() + event.getZ() * event.getZ());
            double speed = sqrt >= 0.2f ? sqrt + (settingSpeed.getValue().intValue() / 1000f) : 0.2f;

            float playerRotationYaw = ISLClass.mc.player.rotationYaw;
            float playerRotationPitch = ISLClass.mc.player.rotationPitch;

            float playerForward = ISLClass.mc.player.movementInput.moveForward;
            float playerStrafe = ISLClass.mc.player.movementInput.moveStrafe;

            if (playerForward == 0.0d && playerStrafe == 0.0d) {
                event.setX(0d);
                event.setZ(0d);
            } else {
                mc.player.setSprinting(mc.player.moveForward != 0.0d);

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

                    double x = Math.cos(Math.toRadians(playerRotationYaw + 90f));
                    double z = Math.sin(Math.toRadians(playerRotationYaw + 90f));

                    event.setX(playerForward * speed * x + playerStrafe * speed * z);
                    event.setZ(playerForward * speed * z - playerStrafe * speed * x);
                }
            }
        }
    }
}
