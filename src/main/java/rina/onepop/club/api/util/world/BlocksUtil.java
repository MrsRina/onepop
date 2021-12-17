package rina.onepop.club.api.util.world;

import rina.onepop.club.Onepop;
import rina.onepop.club.api.util.math.PositionUtil;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

/**
 * @author SrRina
 * @since 28/01/2021 at 16:46
 **/
public class BlocksUtil {
    public static final BlockPos[] BURROW = {
            new BlockPos(0, 0, 0),
            new BlockPos(1, 0, 0),
            new BlockPos(0, 0, 1),
            new BlockPos(-1, 0, 0),
            new BlockPos(0, 0, -1),
            new BlockPos(0, 0, 0),
            new BlockPos(1, 0, 0),
            new BlockPos(0, 0, 1),
            new BlockPos(-1, 0, 0),
            new BlockPos(0, 0, -1)
    };

    public static final BlockPos[] SURROUND = {
            new BlockPos(0, -1, 0),
            new BlockPos(1, 0, 0),
            new BlockPos(0, 0, 1),
            new BlockPos(-1, 0, 0),
            new BlockPos(0, 0, -1)
    };

    public static final BlockPos[] FULL_SURROUND = {
            new BlockPos(0, -1, 0),
            new BlockPos(1, -1, 0),
            new BlockPos(0, -1, 1),
            new BlockPos(-1, -1, 0),
            new BlockPos(0, -1, -1),

            new BlockPos(1, 0, 0),
            new BlockPos(0, 0, 1),
            new BlockPos(-1, 0, 0),
            new BlockPos(0, 0, -1)
    };

    public static List<BlockPos> getSphereList(final float range) {
        final BlockPos selfPos = new BlockPos(Onepop.MC.player.posX, Onepop.MC.player.posY, Onepop.MC.player.posZ);
        final List<BlockPos> sphereList = new ArrayList<>();

        for (int x = (int) (selfPos.x - range); x <= selfPos.x + range; x++) {
            for (int z = (int) (selfPos.z - range); z <= selfPos.z + range; z++) {
                for (int y = (int) (selfPos.y - range); y <= selfPos.y + range; y++) {
                    double dist = (selfPos.x - x) * (selfPos.x - x) + (selfPos.z - z) * (selfPos.z - z) + (selfPos.y - y) * (selfPos.y - y);

                    if (dist < range * range) {
                        sphereList.add(new BlockPos(x, y, z));
                    }
                }
            }
        }

        return sphereList;
    }

    public static List<BlockPos> getSphereList(BlockPos blockPos, float r, int h, boolean hollow, boolean sphere) {
        List<BlockPos> sphereList = new ArrayList<>();

        int cx = blockPos.x;
        int cy = blockPos.y;
        int cz = blockPos.z;

        for (int x = cx - (int) r; x <= cx + r; ++x) {
            for (int z = cz - (int) r; z <= cz + r; ++z) {
                for (int y = sphere ? (cy - (int) r) : cy; y < (sphere ? (cy + r) : ((float) (cy + h))); ++y) {
                    double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? ((cy - y) * (cy - y)) : 0);
                    if (dist < r * r && (!hollow || dist >= (r - 1.0f) * (r - 1.0f))) {
                        BlockPos spheres = new BlockPos(x, y, z);

                        sphereList.add(spheres);
                    }
                }
            }
        }

        return sphereList;
    }

    public static boolean contains(BlockPos position, BlockPos[] list) {
        for (BlockPos positions : list) {
            if (PositionUtil.collideBlockPos(positions, position)) {
                return true;
            }
        }

        return false;
    }
}
