package rina.onepop.club.client.module.player;

import rina.onepop.club.Onepop;
import rina.onepop.club.api.ISLClass;
import rina.onepop.club.api.module.Module;
import rina.onepop.club.api.module.impl.ModuleCategory;
import rina.onepop.club.api.module.registry.Registry;
import rina.onepop.club.api.setting.value.ValueBoolean;
import rina.onepop.club.api.setting.value.ValueNumber;
import rina.onepop.club.api.util.client.NullUtil;
import rina.onepop.club.api.util.entity.PlayerUtil;
import rina.onepop.club.client.event.client.ClientTickEvent;
import rina.onepop.club.client.event.entity.PlayerMoveEvent;
import rina.onepop.club.client.event.network.PacketEvent;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.CPacketInput;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumHand;
import net.minecraftforge.client.event.PlayerSPPushOutOfBlocksEvent;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

/**
 * @author SrRina
 * @since 28/02/2021 at 21:54
 **/
@Registry(name = "Freecam", tag = "Freecam", description = "Cancel your server movements and fly client.", category = ModuleCategory.PLAYER)
public class ModuleFreecam extends Module {
    /* Misc. */
    public static ValueNumber settingSpeed = new ValueNumber("Speed", "Speed", "Speed fly.", 50, 0, 100);
    public static ValueBoolean settingRotate = new ValueBoolean("Rotate", "Rotate", "Enable rotation for you watch your player rotating at freecam.", true);
    public static ValueBoolean settingRotateHead = new ValueBoolean("Rotate Head", "RotateHead", "Enable head rotate to your main player at freecam.", true);
    public static ValueBoolean settingMoveDirection = new ValueBoolean("Move Direction", "MoveDirection", "Moves y by direction pitch/looking.", false);

    private EntityOtherPlayerMP customPlayer;
    private Entity ridingEntity;
    private boolean isRiding;

    private double[] lastPosition;
    private float[] lastRotation;

    @Override
    public void onShutdown() {
        this.setDisabled();
    }

    @Listener
    public void onListenClientTick(ClientTickEvent event) {
        if (NullUtil.isPlayerWorld()) {
            return;
        }

        ISLClass.mc.player.setVelocity(0, 0, 0);

        ISLClass.mc.player.capabilities.isFlying = true;
        ISLClass.mc.player.capabilities.setFlySpeed((settingSpeed.getValue().intValue() * 2) / 1000f);

        this.customPlayer.setRotationYawHead(settingRotateHead.getValue() ? ISLClass.mc.player.getRotationYawHead() : this.lastRotation[0]);

        if (settingRotate.getValue()) {
            this.customPlayer.rotationYaw = ISLClass.mc.player.rotationYaw;
            this.customPlayer.rotationPitch = ISLClass.mc.player.rotationPitch;
        }

        float playerRotationYaw = ISLClass.mc.player.rotationYaw;
        float playerRotationPitch = ISLClass.mc.player.rotationPitch;

        float playerForward = ISLClass.mc.player.movementInput.moveForward;
        float playerStrafe = ISLClass.mc.player.movementInput.moveStrafe;

        this.customPlayer.setHealth(mc.player.getHealth());
        this.customPlayer.inventory = mc.player.inventory;
        this.customPlayer.setHeldItem(EnumHand.MAIN_HAND, mc.player.getHeldItemMainhand());
        this.customPlayer.setHeldItem(EnumHand.OFF_HAND, mc.player.getHeldItemOffhand());

        float speed = (settingSpeed.getValue().intValue() * 6) / 1000f;

        if (settingMoveDirection.getValue() && playerForward != 0) {
            mc.player.motionY = ((playerForward > 0d ? playerForward - playerRotationPitch : (playerForward + playerRotationPitch))) / (1000f - (settingSpeed.getValue().intValue() * 10f));
        }

        if (playerForward == 0.0d && playerStrafe == 0.0d) {
            ISLClass.mc.player.motionX = (0d);
            ISLClass.mc.player.motionZ = (0d);
        } else {
            if (playerForward != 0.0d & playerStrafe != 0.0d) {
                if (playerForward != 0.0d) {
                    if (playerStrafe > 0.0d) {
                        playerRotationYaw += (playerForward > 0.0d ? -45 : 45);
                    } else if (playerStrafe < 0d) {
                        playerRotationYaw += (playerForward > 0.0d ? 45 : -45);
                    }

                    playerStrafe = 0f;

                    if (playerForward > 0.0d) {
                        playerForward = 1.0f;
                    } else if (playerForward < 0){
                        playerForward = -1.0f;
                    }
                }
            }
        }

        mc.player.motionX = ((playerForward * speed) * Math.cos(Math.toRadians(playerRotationYaw + 90f)) + (playerStrafe * speed) * Math.sin(Math.toRadians(playerRotationYaw + 90f)));
        mc.player.motionZ = ((playerForward * speed) * Math.sin(Math.toRadians(playerRotationYaw + 90f)) - (playerStrafe * speed) * Math.cos(Math.toRadians(playerRotationYaw + 90f)));
    }

    @Listener
    public void onListenPushPlayer(PlayerSPPushOutOfBlocksEvent event) {
        event.setCanceled(true);
    }

    @Listener
    public void onListenEvent(PacketEvent.Send event) {
        if (event.getPacket() instanceof CPacketPlayer || event.getPacket() instanceof CPacketInput) {
            event.setCanceled(true);
        }
    }

    @Listener
    public void onListenPlayerMove(PlayerMoveEvent event) {
        ISLClass.mc.player.noClip = true;
    }

    @Override
    public void onDisable() {
        if (NullUtil.isPlayerWorld()) {
            return;
        }

        if (this.lastPosition == null || this.lastRotation == null) {
            return;
        }

        ISLClass.mc.player.capabilities.isFlying = false;
        ISLClass.mc.player.noClip = false;

        Onepop.getEntityWorldManager().removeEntity(-100);
        ISLClass.mc.world.removeEntityFromWorld(-100);

        if (this.isRiding) {
            ISLClass.mc.player.startRiding(this.ridingEntity, true);
        } else {
            ISLClass.mc.player.setPositionAndRotation(this.lastPosition[0], this.lastPosition[1], this.lastPosition[2], this.lastRotation[0], this.lastRotation[1]);
        }
    }

    @Override
    public void onEnable() {
        if (NullUtil.isPlayerWorld()) {
            return;
        }

        this.lastPosition = PlayerUtil.getPos();
        this.lastRotation = PlayerUtil.getRotation();

        this.customPlayer = new EntityOtherPlayerMP(ISLClass.mc.world, ISLClass.mc.player.getGameProfile());
        this.customPlayer.copyLocationAndAnglesFrom(ISLClass.mc.player);

        this.isRiding = (ISLClass.mc.player.isRiding() && ISLClass.mc.player.getRidingEntity() != null);

        if (this.isRiding) {
            this.ridingEntity = ISLClass.mc.player.getRidingEntity();

            ISLClass.mc.player.dismountRidingEntity();
        }

        Onepop.getEntityWorldManager().saveEntity(-100, this.customPlayer);
        ISLClass.mc.world.addEntityToWorld(-100, this.customPlayer);
    }
}
