package rina.onepop.club.api.util.math;

import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import rina.onepop.club.Onepop;
import rina.onepop.club.api.util.network.PacketUtil;

/**
 * @author SrRina
 * @since 12/02/2021 at 12:20
 **/
public class PositionUtil {
    public static void setPosition(double x, double y, double z) {
        PacketUtil.send(new CPacketPlayer.Position(x, y, z, Minecraft.getMinecraft().player.onGround));
        Onepop.MC.player.setPosition(x, y, z);
    }

    public static BlockPos toBlockPos(double x, double y, double z) {
        return new BlockPos(x, y, z);
    }

    public static BlockPos toBlockPos(Vec3d pos) {
        return new BlockPos(pos.x, pos.y, pos.z);
    }

    public static Vec3d toVec(BlockPos pos) {
        return new Vec3d(pos.x, pos.y, pos.z);
    }

    public static boolean collideBlockPos(BlockPos a, BlockPos b) {
        return a.getX() == a.getX() && a.getY() == b.getY() && a.getZ() == b.getZ();
    }

    public static Vec3d calculateHitPlace(BlockPos pos, EnumFacing facing) {
        return new Vec3d(pos).add(0.5, 0.5, 0.5).add(new Vec3d(facing.getDirectionVec()).scale(0.5));
    }
}
