package rina.onepop.club.api.util.math;

import rina.onepop.club.api.util.entity.EntityUtil;
import rina.onepop.club.Onepop;
import me.rina.turok.util.TurokMath;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

/**
 * @author SrRina
 * @since 15/02/2021 at 18:10
 **/
public class RotationUtil {
    public static float[] getPlaceRotation(Vec3d pos) {
        Vec3d eye = EntityUtil.eye(Onepop.MC.player);

        double x = pos.x - eye.x;
        double y = pos.y - eye.y;
        double z = pos.z - eye.z;

        double diff = TurokMath.sqrt(x * x + z * z);

        float yaw = (float) Math.toDegrees(Math.atan2(z, x)) - 90f;
        float pitch = (float) -Math.toDegrees(Math.atan2(y, diff));

        return new float[] {
                Onepop.MC.player.rotationYaw + MathHelper.wrapDegrees(yaw - Onepop.MC.player.rotationYaw),
                Onepop.MC.player.rotationPitch + MathHelper.wrapDegrees(pitch - Onepop.MC.player.rotationPitch)
        };
    }

    public static float[] getBreakRotation(Vec3d vec) {
        double dirX = Onepop.MC.player.posX - vec.x;
        double dirY = Onepop.MC.player.posY - vec.y;
        double dirZ = Onepop.MC.player.posZ - vec.z;

        double len = TurokMath.sqrt(dirX * dirX + dirY * dirY + dirZ * dirZ);

        dirX /= len;
        dirY /= len;
        dirZ /= len;

        float pitch = (float) Math.asin(dirY);
        float yaw = (float) Math.atan2(dirZ, dirX);

        pitch = (float) (pitch * 180.0d / Math.PI);
        yaw = (float) (yaw * 180.0d / Math.PI);

        yaw += 90f;

        return new float[] {
            yaw, pitch
        };
    }
}
