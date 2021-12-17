package rina.onepop.club.client.module.misc.middleclick;

import net.minecraft.init.Items;
import net.minecraft.item.Item;

public enum Mode {
    ENDER_PEARL(Items.ENDER_PEARL), XP(Items.EXPERIENCE_BOTTLE), FRIEND(null), ENEMY(null), BURROW(null);

    Item item;

    Mode(Item item) {
        this.item = item;
    }

    public Item getItem() {
        return item;
    }
}
