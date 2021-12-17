package rina.onepop.club.api.util.entity;

import rina.onepop.club.api.util.network.PacketUtil;
import rina.onepop.club.Onepop;
import rina.onepop.club.api.util.math.PositionUtil;
import me.rina.turok.util.TurokMath;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

/**
 * @author SrRina
 * @since 16/02/2021 at 09:13
 **/
public class PlayerPositionUtil {
    public static void teleportation(BlockPos pos) {
        Vec3d vec = PositionUtil.toVec(pos);

        teleportation(vec);
    }

    public static void teleportation(Vec3d vec) {
        boolean flag = Onepop.MC.player.onGround;

        teleportation(vec, flag);
    }

    public static void teleportation(Vec3d vec, boolean onGround) {
        double x = vec.x;
        double y = vec.y;
        double z = vec.z;

        PacketUtil.send(new CPacketPlayer.Position(x, y, z, onGround));
        PlayerUtil.setPosition(x, y, z);
    }

    public static void smooth(Vec3d vec, float partialTicks) {
        boolean flag = Onepop.MC.player.onGround;

        smooth(vec, flag, partialTicks);
    }

    public static void smooth(Vec3d vec, boolean onGround, float partialTicks) {
        Vec3d last = PlayerUtil.getVec();
        Vec3d interpolation = TurokMath.lerp(last, vec, partialTicks);

        PlayerUtil.setPosition(interpolation.x, interpolation.y, interpolation.z);
        PacketUtil.send(new CPacketPlayer.Position(interpolation.x, interpolation.y, interpolation.z, onGround));
    }
}
