package rina.onepop.club.api.strict.rotation;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import rina.onepop.club.api.strict.util.Rotation;
import rina.onepop.club.api.strict.util.StrictVec;

/**
 * @author SrRina
 * @since 06/07/2021 at 19:29
 **/
public class RStrict {
    final static Minecraft mc = Minecraft.getMinecraft();

    public static Rotation serverRotation = new Rotation(0.0F, 0.0F);

    public static StrictVec faceBlock(BlockPos blockPos) {
        if (blockPos == null)
            return null;
        StrictVec vecRotation = null;
        for (double xSearch = 0.1D; xSearch < 0.9D; xSearch += 0.1D) {
            double ySearch;
            for (ySearch = 0.1D; ySearch < 0.9D; ySearch += 0.1D) {
                double zSearch;
                for (zSearch = 0.1D; zSearch < 0.9D; zSearch += 0.1D) {
                    Vec3d eyesPos = new Vec3d(mc.player.posX, (mc.player.getEntityBoundingBox()).minY + mc.player.getEyeHeight(), mc.player.posZ);
                    Vec3d posVec = (new Vec3d(blockPos)).add(xSearch, ySearch, zSearch);
                    double dist = eyesPos.distanceTo(posVec);
                    double diffX = posVec.x - eyesPos.x;
                    double diffY = posVec.y - eyesPos.y;
                    double diffZ = posVec.z - eyesPos.z;
                    double diffXZ = MathHelper.sqrt(diffX * diffX + diffZ * diffZ);
                    Rotation rotation = new Rotation(MathHelper.wrapDegrees((float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0F), MathHelper.wrapDegrees((float) -Math.toDegrees(Math.atan2(diffY, diffXZ))));
                    Vec3d rotationVector = getVectorForRotation(rotation);
                    Vec3d vector = eyesPos.add(rotationVector.x * dist, rotationVector.y * dist, rotationVector.z * dist);
                    RayTraceResult obj = mc.world.rayTraceBlocks(eyesPos, vector, false, false, true);
                    if (obj.typeOfHit == RayTraceResult.Type.BLOCK) {
                        StrictVec currentVec = new StrictVec(posVec, rotation);
                        if (vecRotation == null || getRotationDifference(currentVec.getRotation()) < getRotationDifference(vecRotation.getRotation()))
                            vecRotation = currentVec;
                    }
                }
            }
        }
        return vecRotation;
    }

    public static void setPlayerRotations(float yaw, float pitch, boolean packet) {
        if (packet) {
            mc.player.connection.sendPacket(new CPacketPlayer.Rotation(yaw, pitch, mc.player.onGround));

            mc.player.renderYawOffset = yaw;
            mc.player.rotationYawHead = yaw;
        } else {
            mc.player.rotationYaw = yaw;
            mc.player.rotationPitch = pitch;
        }
    }

    public static Vec3d getVectorForRotation(Rotation rotation) {
        float yawCos = MathHelper.cos(-rotation.getYaw() * 0.017453292F - 3.1415927F);
        float yawSin = MathHelper.sin(-rotation.getYaw() * 0.017453292F - 3.1415927F);
        float pitchCos = -MathHelper.cos(-rotation.getPitch() * 0.017453292F);
        float pitchSin = MathHelper.sin(-rotation.getPitch() * 0.017453292F);
        return new Vec3d((yawSin * pitchCos), pitchSin, (yawCos * pitchCos));
    }

    public static double getRotationDifference(Rotation rotation) {
        return (serverRotation == null) ? 0.0D : getRotationDifference(rotation, serverRotation);
    }

    public static double getRotationDifference(Rotation a, Rotation b) {
        return Math.hypot(getAngleDifference(a.getYaw(), b.getYaw()), (a.getPitch() - b.getPitch()));
    }

    private static float getAngleDifference(float a, float b) {
        return ((a - b) % 360.0F + 540.0F) % 360.0F - 180.0F;
    }

    public static float[] calculateLookAt(final double px, final double py, final double pz, final EntityPlayer me) {
        double dirx = me.posX - px;
        double diry = me.posY - py;
        double dirz = me.posZ - pz;
        final double len = Math.sqrt(dirx * dirx + diry * diry + dirz * dirz);
        dirx /= len;
        diry /= len;
        dirz /= len;
        float pitch = (float) Math.asin(diry);
        float yaw = (float) Math.atan2(dirz, dirx);
        pitch = pitch * 180.0F / 3.141592653589793F;
        yaw = yaw * 180.0F / 3.141592653589793F;
        yaw += 90.0F;
        return new float[]{yaw, pitch};
    }
}
