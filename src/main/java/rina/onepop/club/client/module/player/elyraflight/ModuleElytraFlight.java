package rina.onepop.club.client.module.player.elyraflight;

import rina.onepop.club.api.ISLClass;
import rina.onepop.club.api.module.Module;
import rina.onepop.club.api.module.impl.ModuleCategory;
import rina.onepop.club.api.module.registry.Registry;
import rina.onepop.club.api.setting.value.ValueBoolean;
import rina.onepop.club.api.setting.value.ValueEnum;
import rina.onepop.club.api.setting.value.ValueNumber;
import rina.onepop.club.api.util.client.KeyUtil;
import rina.onepop.club.api.util.client.NullUtil;
import rina.onepop.club.api.util.item.SlotUtil;
import rina.onepop.club.client.event.client.RunTickEvent;
import rina.onepop.club.client.event.entity.PlayerMoveEvent;
import me.rina.turok.util.TurokMath;
import net.minecraft.entity.MoverType;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketEntityAction;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

/**
 * @author SrRina
 * @since 15/05/2021 at 17:10
 **/
@Registry(name = "Elytra Flight", tag = "ElytraFlight", description = "A better fly to elytra.", category = ModuleCategory.PLAYER)
public class ModuleElytraFlight extends Module {
    /* Misc. */
    public static ValueBoolean settingMovesDirection = new ValueBoolean("Move Direction", "MoveDirection", "Moves by direction.", true);
    public static ValueNumber settingSpeedIncrease = new ValueNumber("Speed Increase", "SpeedIncrease", "Increase speed.", 0, -100, 200);
    public static ValueNumber settingSpeedIncreaseY = new ValueNumber("Speed Increase Y", "SpeedIncreaseY", "Speed for increase y!", 0, -100, 200);
    public static ValueEnum settingStartFly = new ValueEnum("Start Fly", "StartFly", "Modes for start fly.", Fly.LEGIT);
    public static ValueEnum settingMode = new ValueEnum("Mode", "Mode",  "Modes for elytra.", Mode.NORMAL);

    private boolean isElytraEquipped;
    private boolean isPlayerFlying;

    private boolean preJumpPressed;

    @Override
    public void onSetting() {
        settingSpeedIncreaseY.setEnabled(settingMode.getValue() == Mode.STATIC);
    }

    @Listener
    public void onRunTick(RunTickEvent event) {
        if (NullUtil.isPlayerWorld()) {
            return;
        }

        this.isElytraEquipped = SlotUtil.getArmourItem(2) == Items.ELYTRA;

        if (!this.isElytraEquipped) {
            this.isPlayerFlying = false;

            return;
        }

        // The best way to verify if you are really falling is only comparing fallDistance, so we can capture much better the falls.
        if (mc.player.fallDistance > 0.0f && !mc.player.onGround && !mc.player.isElytraFlying() && settingStartFly.getValue() != Fly.NONE) {
            if (settingStartFly.getValue() == Fly.LEGIT) {
                KeyUtil.press(mc.gameSettings.keyBindJump, true);
            } else {
                if (!this.preJumpPressed) {
                    mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_FALL_FLYING));
                }
            }

            this.preJumpPressed = true;
        } else {
            if (this.preJumpPressed) {
                KeyUtil.press(mc.gameSettings.keyBindJump, false);

                this.preJumpPressed = false;
            }
        }

        this.isPlayerFlying = mc.player.isElytraFlying();
    }

    @Listener
    public void onMove(PlayerMoveEvent event) {
        if (event.getType() != MoverType.SELF) {
            return;
        }

        if (!this.isElytraEquipped) {
            return;
        }

        if (!this.isPlayerFlying) {
            return;
        }

        // The best speed sqrt that I found, works on 9b9t and somes servers.
        float speed = (1.19025899797973633f) + (settingSpeedIncrease.getValue().intValue() / 100f);

        float playerRotationYaw = ISLClass.mc.player.rotationYaw;
        float playerRotationPitch = ISLClass.mc.player.rotationPitch;

        float playerForward = ISLClass.mc.player.movementInput.moveForward;
        float playerStrafe = ISLClass.mc.player.movementInput.moveStrafe;

        // The 0.49099985 is the maximum Y motion I can move... and we need clamp it in direction also.
        if (mc.gameSettings.keyBindJump.isKeyDown()) {
            event.y += settingMode.getValue() == Mode.NORMAL ? 0.5f : (1.19025899797973633f) + (settingSpeedIncreaseY.getValue().intValue() / 100f);
        } else if (mc.gameSettings.keyBindSneak.isKeyDown()) {
            mc.player.setSneaking(false);
            event.y -= settingMode.getValue() == Mode.NORMAL ? 0.49099985f : (1.19025899797973633f) + (settingSpeedIncreaseY.getValue().intValue() / 100f);
        } else {
            if (playerForward != 0 && settingMovesDirection.getValue()) {
                event.setY(TurokMath.clamp((playerForward > 0d ? playerForward - playerRotationPitch : (playerForward + playerRotationPitch)) / 150f, -(settingMode.getValue() == Mode.NORMAL ? 0.49099985f : (1.19025899797973633f) + (settingSpeedIncreaseY.getValue().intValue() / 100f)), settingMode.getValue() == Mode.NORMAL ? 0.5f : (1.19025899797973633f) + (settingSpeedIncreaseY.getValue().intValue() / 100f)));
            } else {
                event.setY(0);
            }
        }

        //this.print("" + (playerForward > 0d ? playerForward - playerRotationPitch : (playerForward + playerRotationPitch)) / 150f);

        if (playerForward == 0.0d && playerStrafe == 0.0d) {
            event.setX(0);
            event.setZ(0);
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

            event.setX((playerForward * speed) * Math.cos(Math.toRadians((playerRotationYaw + 90.0f))) + (playerStrafe * speed) * Math.sin(Math.toRadians((playerRotationYaw + 90.0f))));
            event.setZ((playerForward * speed) * Math.sin(Math.toRadians((playerRotationYaw + 90.0f))) - (playerStrafe * speed) * Math.cos(Math.toRadians((playerRotationYaw + 90.0f))));
        }
    }
}
