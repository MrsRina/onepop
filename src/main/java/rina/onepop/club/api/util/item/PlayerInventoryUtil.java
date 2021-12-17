package rina.onepop.club.api.util.item;

import rina.onepop.club.Onepop;

/**
 * @author SrRina
 * @since 07/03/2021 at 18:24
 **/
public class PlayerInventoryUtil {
    public static void setCurrentHotBarItem(int slotIn) {
        Onepop.MC.player.inventory.currentItem = slotIn;
    }
}
