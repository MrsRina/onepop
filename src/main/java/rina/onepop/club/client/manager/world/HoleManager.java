package rina.onepop.club.client.manager.world;

import me.rina.turok.util.TurokMath;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import rina.onepop.club.api.manager.Manager;
import rina.onepop.club.api.util.client.NullUtil;
import rina.onepop.club.api.util.entity.PlayerUtil;
import rina.onepop.club.api.util.math.PositionUtil;
import rina.onepop.club.api.util.world.BlockUtil;
import rina.onepop.club.api.util.world.BlocksUtil;
import rina.onepop.club.client.module.client.ModuleGeneral;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @author SrRina
 * @since 06/03/2021 at 00:19
 **/
public class HoleManager extends Manager {
    public static final int UNSAFE = 0;
    public static final int SAFE = 1;

    public static class Hole {
        private final BlockPos position;
        private final int type;

        private final EnumFacing direction;

        public Hole(BlockPos position, int type, EnumFacing direction) {
            this.position = position;
            this.type = type;

            this.direction = direction;
        }

        public BlockPos getPosition() {
            return position;
        }

        public int getType() {
            return type;
        }

        public EnumFacing getDirection() {
            return direction;
        }
    }

    private List<Hole> holeList;
    private float range = 10;

    private final List<BlockPos> bufferDoubleHole = new ArrayList<>();

    public HoleManager() {
        super("Hole Manager", "Client calculate holes and place in a list.");

        this.holeList = new ArrayList<>();
    }

    public void setHoleList(ArrayList<Hole> holeList) {
        this.holeList = holeList;
    }

    public List<Hole> getHoleList() {
        return holeList;
    }

    public Hole getHole(BlockPos position) {
        for (Hole holes : this.holeList) {
            if (PositionUtil.collideBlockPos(holes.getPosition(), position)) {
                return holes;
            }
        }

        return null;
    }

    public boolean isHole(BlockPos position) {
        return getHole(position) != null;
    }

    public void setRange(float range) {
        this.range = range;
    }

    public float getRange() {
        return range;
    }

    @Override
    public void onUpdateAll() {
        if (NullUtil.isPlayerWorld()) {
            return;
        }

        this.holeList.clear();
        this.bufferDoubleHole.clear();

        int r = TurokMath.ceiling(ModuleGeneral.settingVisualEffects.getValue().intValue());

        final List<BlockPos> sphereList = BlocksUtil.getSphereList(PlayerUtil.getBlockPos(), r, r, false, true);

        for (BlockPos blocks : sphereList) {
            if (!BlockUtil.isAir(blocks)) {
                continue;
            }

            final Hole hole = BlockUtil.isHole(blocks);

            if (hole == null) {
                continue;
            }

            if (hole.getDirection() != null) {
                if (this.bufferDoubleHole.contains(hole.getPosition().offset(hole.getDirection())) || this.bufferDoubleHole.contains(hole.getPosition())) {
                    continue;
                } else {
                    this.bufferDoubleHole.add(hole.getPosition());
                    this.bufferDoubleHole.add(hole.getPosition().offset(hole.getDirection()));
                }
            }

            this.holeList.add(hole);
        }

        this.holeList.sort(Comparator.comparingDouble(hole -> mc.player.getDistanceSq(hole.getPosition())));
    }
}