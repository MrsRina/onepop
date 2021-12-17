package rina.onepop.club.api.tracker.impl;

import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumHand;
import rina.onepop.club.Onepop;
import rina.onepop.club.api.tracker.Tracker;
import rina.onepop.club.api.util.world.BlockUtil;

/**
 * @author SrRina
 * @since 09/02/2021 at 19:18
 **/
public class PlayerStartBreakBlockTracker extends Tracker {
    private BlockUtil.BlockDamage block;
    private EnumHand hand;

    public PlayerStartBreakBlockTracker(EnumHand hand, BlockUtil.BlockDamage block) {
        super("Break Block", new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, block.getPos(), block.getFacing()));

        this.block = block;
        this.hand = hand;
    }

    public void setBlock(BlockUtil.BlockDamage block) {
        this.block = block;
    }

    public BlockUtil.BlockDamage getBlock() {
        return block;
    }

    public void setHand(EnumHand hand) {
        this.hand = hand;
    }

    public EnumHand getHand() {
        return hand;
    }

    @Override
    public void onPre() {
        if (this.hand != null) {
            Onepop.MC.player.swingArm(this.hand);
        }
    }

    @Override
    public void onPost() {
    }
}
