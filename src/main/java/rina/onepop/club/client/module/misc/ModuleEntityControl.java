package rina.onepop.club.client.module.misc;

import rina.onepop.club.api.ISLClass;
import rina.onepop.club.api.module.Module;
import rina.onepop.club.api.module.impl.ModuleCategory;
import rina.onepop.club.api.module.registry.Registry;
import rina.onepop.club.api.setting.value.ValueBoolean;
import rina.onepop.club.api.setting.value.ValueNumber;
import rina.onepop.club.api.util.client.NullUtil;
import rina.onepop.club.client.event.client.RunTickEvent;
import rina.onepop.club.client.event.entity.AbstractHorseEvent;
import rina.onepop.club.client.event.entity.PigEvent;
import net.minecraft.entity.Entity;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

/**
 * @author SrRina
 * @since 15/05/2021 at 17:05
 **/
@Registry(name = "Entity Control", tag = "EntityControl", description = "Allows you mount to control animals without a saddle or carrot.", category = ModuleCategory.MISC)
public class ModuleEntityControl extends Module {
    /* Misc. */
    public static ValueBoolean settingPIG = new ValueBoolean("Pig <3", "Pig", "Pigs!!!! oink oink!", true);
    public static ValueBoolean settingStrafeAirControl = new ValueBoolean("Strafe Air Control", "StrafeAirControl", "Control air with your entity!", true);
    public static ValueNumber settingSpeedIncrease = new ValueNumber("Speed Increase", "SpeedIncrease", "Increase speed!", 50, 0, 200);

    @Override
    public void onSetting() {
        settingSpeedIncrease.setEnabled(settingStrafeAirControl.getValue());
    }

    @Listener
    public void onTick(RunTickEvent event) {
        if (NullUtil.isPlayerWorld()) {
            return;
        }

        if (settingStrafeAirControl.getValue() && ISLClass.mc.player.getRidingEntity() != null) {
            Entity riding = ISLClass.mc.player.getRidingEntity();

            float playerRotationYaw = ISLClass.mc.player.rotationYaw;
            float playerRotationPitch = ISLClass.mc.player.rotationPitch;

            float playerForward = ISLClass.mc.player.movementInput.moveForward;
            float playerStrafe = ISLClass.mc.player.movementInput.moveStrafe;

            float speed = Math.sqrt(riding.motionX * riding.motionX + riding.motionZ * riding.motionZ) > 0.2873f ? (float) Math.sqrt(riding.motionX * riding.motionX + riding.motionZ * riding.motionZ) : 0.2873f;

            speed += settingSpeedIncrease.getValue().intValue() * 0.1f;

            if (playerForward == 0.0d && playerStrafe == 0.0d) {
                riding.motionX = 0;
                riding.motionZ = 0;
            } else {
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

                riding.motionX = ((playerForward * speed) * Math.cos(Math.toRadians((playerRotationYaw + 90.0f))) + (playerStrafe * speed) * Math.sin(Math.toRadians((playerRotationYaw + 90.0f))));
                riding.motionZ = ((playerForward * speed) * Math.sin(Math.toRadians((playerRotationYaw + 90.0f))) - (playerStrafe * speed) * Math.cos(Math.toRadians((playerRotationYaw + 90.0f))));
            }
        }
    }

    @Listener
    public void onPig(PigEvent event) {
        if (settingPIG.getValue()) {
            event.setCanceled(true);
        }
    }

    @Listener
    public void onAbstractHorse(AbstractHorseEvent event) {
        event.setCanceled(true);
    }
}
