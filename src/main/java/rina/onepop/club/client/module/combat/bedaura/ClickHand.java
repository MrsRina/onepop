package rina.onepop.club.client.module.combat.bedaura;

import net.minecraft.util.EnumHand;

/**
 * @author SrRina
 * @since 04/10/2021 at 18:34
 **/
public enum ClickHand {
    AUTO(null), OFF(EnumHand.OFF_HAND), MAIN(EnumHand.MAIN_HAND);

    EnumHand hand;

    ClickHand(EnumHand hand) {
        this.hand = hand;
    }

    public EnumHand getHand() {
        return hand;
    }
}
