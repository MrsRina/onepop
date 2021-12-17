package rina.onepop.club.client.manager.network;

import net.minecraft.network.play.client.CPacketPlayer;
import rina.onepop.club.api.manager.Manager;
import rina.onepop.club.api.strict.StrictUtilityInjector;
import rina.onepop.club.api.util.entity.PlayerRotationUtil;
import rina.onepop.club.client.event.network.PacketEvent;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

/**
 * @author SrRina
 * @since 13/04/2021 at 22:12
 **/
public class RotationManager extends Manager {
    public static RotationManager INSTANCE;

    private float yaw;
    private float pitch;

    private boolean rotating;

    public RotationManager() {
        super("Rotation Manager", "Good rotations need good managers!");

        INSTANCE = this;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public float getPitch() {
        return pitch;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public float getYaw() {
        return yaw;
    }

    public void setRotating(boolean rotating) {
        this.rotating = rotating;
    }

    public boolean isRotating() {
        return rotating;
    }

    @Listener
    public void onSendPacket(PacketEvent.Send event) {
        if ((event.getPacket() instanceof CPacketPlayer || event.getPacket() instanceof CPacketPlayer.Position || event.getPacket() instanceof CPacketPlayer.Rotation || event.getPacket() instanceof CPacketPlayer.PositionRotation) && this.isRotating()) {
            StrictUtilityInjector.rotation(event.getPacket(), this.yaw, this.pitch);

            this.doClearTask();
        }
    }

    public static RotationManager task(Enum<?> rotation, float[] rotates) {
        switch ((Rotation) rotation) {
            case LEGIT: {
                PlayerRotationUtil.manual(rotates[0], rotates[1]);

                break;
            }

            case REL: {
                INSTANCE.setYaw(rotates[0]);
                INSTANCE.setPitch(rotates[1]);
                INSTANCE.setRotating(true);

                break;
            }

            case SEND: {
                mc.player.connection.sendPacket(new CPacketPlayer.Rotation(rotates[0], rotates[1], mc.player.onGround));

                break;
            }
        }

        return INSTANCE;
    }

    public void doClearTask() {
        this.rotating = false;
    }

    @Override
    public void onUpdateAll() {

    }
}
