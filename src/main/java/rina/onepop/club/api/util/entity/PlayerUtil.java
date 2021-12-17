package rina.onepop.club.api.util.entity;

import rina.onepop.club.Onepop;
import me.rina.turok.util.TurokMath;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

/**
 * @author SrRina
 * @since 28/01/2021 at 16:54
 **/
public class PlayerUtil {
    public static BlockPos getBlockPos() {
        return new BlockPos(Math.floor(Onepop.MC.player.posX), Onepop.MC.player.posY, Math.floor(Onepop.MC.player.posZ));
    }

    public static Vec3d getVec() {
        return new Vec3d(Onepop.MC.player.posX, Onepop.MC.player.posY, Onepop.MC.player.posZ);
    }

    /**
     * 0, 1, 2 are position x, y & z;
     *
     * @return position
     */
    public static double[] getPos() {
        return new double[] {
                Onepop.MC.player.posX, Onepop.MC.player.posY, Onepop.MC.player.posZ
        };
    }

    /**
     * 0, 1, 2 are last tick pos x, y & z;
     *
     * @return last tick pos.
     */
    public static double[] getLastTickPos() {
        return new double[] {
                Onepop.MC.player.lastTickPosX, Onepop.MC.player.lastTickPosY, Onepop.MC.player.lastTickPosZ
        };
    }

    /**
     * 0, 1, 2 are prev position x, y & z;
     *
     * @return prev position
     */
    public static double[] getPrevPos() {
        return new double[] {
                Onepop.MC.player.prevPosX, Onepop.MC.player.prevPosY, Onepop.MC.player.prevPosZ
        };
    }

    /**
     * 0, 1, 2 are motion x, y & z;
     *
     * @return motions movement
     */
    public static double[] getMotion() {
        return new double[] {
                Onepop.MC.player.motionX, Onepop.MC.player.motionY, Onepop.MC.player.motionZ
        };
    }

    /**
     * 0 is head yaw, 1 yaw player & 2 pitch.
     *
     * @return rotations player
     */
    public static float[] getRotation() {
        return new float[] {Onepop.MC.player.rotationYaw, Onepop.MC.player.rotationPitch};
    }

    /**
     * Calculate blocks per second.
     *
     * @return blocks per tick
     */
    public static double getBPS() {
        double[] prevPosition = getPrevPos();
        double[] position = getPos();

        // Delta values.
        double x = position[0] - prevPosition[0];
        double z = position[2] - prevPosition[2];

        return TurokMath.sqrt(x * x + z * z) / (Onepop.MC.timer.tickLength / 1000.0d);
    }

    public static void setPosition(double x, double y, double z) {
        Onepop.MC.player.setPosition(x, y, z);
    }

    public static void setYaw(float yaw) {
        Onepop.MC.player.rotationYaw = yaw;
        Onepop.MC.player.rotationYawHead = yaw;
    }

    public static void setPitch(float pitch) {
        Onepop.MC.player.rotationPitch = pitch;
    }

    public static Dimension getCurrentDimension() {
        Dimension dimension = null;

        if (Onepop.MC.player.dimension == -1) {
            dimension = Dimension.NETHER;
        }

        if (Onepop.MC.player.dimension == 0) {
            dimension = Dimension.WORLD;
        }

        if (Onepop.MC.player.dimension == 1) {
            dimension = Dimension.END;
        }

        return dimension;
    }
}
