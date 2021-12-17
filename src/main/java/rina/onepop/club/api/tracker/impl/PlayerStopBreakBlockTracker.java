package rina.onepop.club.api.tracker.impl;

import net.minecraft.network.play.client.CPacketPlayerDigging;
import rina.onepop.club.api.tracker.Tracker;
import rina.onepop.club.api.util.world.BlockUtil;

/**
 * @author SrRina
 * @since 09/02/2021 at 19:18
 **/
public class PlayerStopBreakBlockTracker extends Tracker {
    private BlockUtil.BlockDamage block;

    public PlayerStopBreakBlockTracker(BlockUtil.BlockDamage block) {
            super("Break Block", new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, block.getPos(), block.getFacing()));

        this.block = block;
    }

    public void setBlock(BlockUtil.BlockDamage block) {
        this.block = block;
    }

    public BlockUtil.BlockDamage getBlock() {
        return block;
    }

    @Override
    public void onPre() {
    }

    @Override
    public void onPost() {
    }
}