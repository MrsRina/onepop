package rina.onepop.club.api.util.entity;

import rina.onepop.club.Onepop;
import rina.onepop.club.api.util.math.RotationUtil;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.Vec3d;

/**
 * @author SrRina
 * @since 14/02/2021 at 11:40
 **/
public class PlayerRotationUtil {
    public static void spamPacketRotation(Vec3d vec) {
        float[] rotate = RotationUtil.getPlaceRotation(vec);

        float yaw = rotate[0];
        float pitch = rotate[1];

        boolean flag = Onepop.MC.player.onGround;

        Onepop.MC.player.connection.sendPacket(new CPacketPlayer.Rotation(yaw, pitch, flag));
    }

    public static void spamPacketRotation(float yaw, float pitch) {
        boolean flag = Onepop.MC.player.onGround;

        Onepop.MC.player.connection.sendPacket(new CPacketPlayer.Rotation(yaw, pitch, flag));
    }

    public static void manual(Vec3d vec) {
        float[] rotate = RotationUtil.getPlaceRotation(vec);

        float yaw = rotate[0];
        float pitch = rotate[1];

        PlayerUtil.setYaw(yaw);
        PlayerUtil.setPitch(pitch);
    }

    public static void manual(float yaw, float pitch) {
        PlayerUtil.setYaw(yaw);
        PlayerUtil.setPitch(pitch);
    }
}
