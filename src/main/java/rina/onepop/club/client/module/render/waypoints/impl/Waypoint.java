package rina.onepop.club.client.module.render.waypoints.impl;

import me.rina.turok.util.TurokTick;
import net.minecraft.util.math.BlockPos;

/**
 * @author SrRina
 * @since 13/07/2021 at 01:35
 **/
public class Waypoint {
    private final BlockPos position;
    private final String tag;
    private final TurokTick timer = new TurokTick();

    public Waypoint(BlockPos position, String tag) {
        this.position = position;
        this.tag = tag;

        this.timer.reset();
    }

    public String getTag() {
        return tag;
    }

    public BlockPos getPosition() {
        return position;
    }

    public boolean isAlive(int minutes) {
        return !this.timer.isPassedSI(minutes * 60);
    }
}
