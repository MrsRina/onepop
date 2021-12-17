package rina.onepop.club.api.tracker.impl;

import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.util.EnumHand;
import rina.onepop.club.Onepop;
import rina.onepop.club.api.tracker.Tracker;

/**
 * @author SrRina
 * @since 05/02/2021 at 13:16
 **/
public class RightMouseClickTracker extends Tracker {
    private EnumHand hand;

    public RightMouseClickTracker(EnumHand hand) {
        super("Right Mouse Click Tracker", new CPacketPlayerTryUseItem(hand == null ? EnumHand.MAIN_HAND : hand));

        this.hand = hand;
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
