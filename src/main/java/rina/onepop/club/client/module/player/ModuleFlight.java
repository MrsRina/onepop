package rina.onepop.club.client.module.player;

import rina.onepop.club.api.ISLClass;
import rina.onepop.club.api.module.Module;
import rina.onepop.club.api.module.impl.ModuleCategory;
import rina.onepop.club.api.module.registry.Registry;
import rina.onepop.club.api.setting.value.ValueBoolean;
import rina.onepop.club.api.setting.value.ValueNumber;
import rina.onepop.club.api.util.client.NullUtil;
import rina.onepop.club.client.event.client.RunTickEvent;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

/**
 * @author SrRina
 * @since 02/06/2021 at 02:05
 **/
@Registry(name = "Flight", tag = "Flight", description = "Make you fly diff.", category = ModuleCategory.PLAYER)
public class ModuleFlight extends Module {
    /* Misc. */
    public static ValueBoolean settingMovesDirection = new ValueBoolean("Move Direction", "MoveDirection", "Moves by direction.", true);
    public static ValueNumber settingSpeed = new ValueNumber("Speed", "Speed", "Speed fly.", 0, 0, 1000);
    public static ValueNumber settingSpeedY = new ValueNumber("Speed Y", "SpeedY", "Speed for increase y!", 200, 0, 1000);

    @Override
    public void onDisable() {
        if (NullUtil.isPlayerWorld()) {
            return;
        }

        mc.player.capabilities.isFlying = false;
    }

    @Listener
    public void onTick(RunTickEvent event) {
        if (NullUtil.isPlayerWorld()) {
            return;
        }

        float speed = (settingSpeed.getValue().intValue() / 100f);

        float playerRotationYaw = ISLClass.mc.player.rotationYaw;
        float playerRotationPitch = ISLClass.mc.player.rotationPitch;

        float playerForward = ISLClass.mc.player.movementInput.moveForward;
        float playerStrafe = ISLClass.mc.player.movementInput.moveStrafe;

        if (mc.gameSettings.keyBindJump.isKeyDown()) {
            mc.player.motionY += settingSpeedY.getValue().intValue() / 100f;
        } else if (mc.gameSettings.keyBindSneak.isKeyDown()) {
            mc.player.setSneaking(false);
            mc.player.motionY -= settingSpeedY.getValue().intValue() / 100f;
        } else {
            if (playerForward != 0 && settingMovesDirection.getValue()) {
                mc.player.motionY = (((playerForward + (settingSpeedY.getValue().intValue() / 1000f)) - playerRotationPitch) / 150f);
            } else {
                mc.player.motionY = 0;
            }
        }

        if (playerForward == 0.0d && playerStrafe == 0.0d) {
            mc.player.motionX = (0);
            mc.player.motionZ = (0);
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

            mc.player.motionX = ((playerForward * speed) * Math.cos(Math.toRadians((playerRotationYaw + 90.0f))) + (playerStrafe * speed) * Math.sin(Math.toRadians((playerRotationYaw + 90.0f))));
            mc.player.motionZ = ((playerForward * speed) * Math.sin(Math.toRadians((playerRotationYaw + 90.0f))) - (playerStrafe * speed) * Math.cos(Math.toRadians((playerRotationYaw + 90.0f))));
        }
    }
}
