package rina.onepop.club.api.util.item;

import rina.onepop.club.Onepop;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketHeldItemChange;

import java.util.HashMap;

/**
 * @author SrRina
 * @since 02/02/2021 at 13:31
 **/
public class SlotUtil {
    public static void setCurrentItem(int slot) {
        Onepop.MC.player.connection.sendPacket(new CPacketHeldItemChange(slot));
        Onepop.MC.player.inventory.currentItem = slot;
        Onepop.MC.playerController.updateController();
    }

    public static void setServerCurrentItem(int slot) {
        Onepop.MC.player.connection.sendPacket(new CPacketHeldItemChange(slot));
    }

    public static int findFirstSlotAirFromInventory() {
        int slot = -1;

        for (int i = 9; i < 36; i++) {
            ItemStack stack = Onepop.MC.player.inventory.getStackInSlot(i);

            if (stack.getItem() == Items.AIR) {
                slot = i;

                break;
            }
        }

        return slot;
    }

    public static HashMap<Integer, Integer> mapItemCountWithSize(Item item) {
        final HashMap<Integer, Integer> map = new HashMap<>();

        for (int i = 9; i < 36; i++) {
            ItemStack stack = Onepop.MC.player.inventory.getStackInSlot(i);

            if (stack.getItem() == item) {
                map.put(i, stack.stackSize);
            }
        }

        return map;
    }

    public static int[] getTotalStackFromInventory(Item item) {
        int count = 0;
        int totally = 0;

        for (int i = 9; i < 36; i++) {
            ItemStack stack = Onepop.MC.player.inventory.getStackInSlot(i);

            if (stack.getItem() == item) {
                count++;
                totally += stack.getCount();
            }
        }

        return new int[] {count, totally};
    }

    public static Item getArmourItem(int slot) {
        return Onepop.MC.player.inventory.armorItemInSlot(slot).getItem();
    }

    public static ItemStack getArmourItemStack(int slot) {
        return Onepop.MC.player.inventory.armorItemInSlot(slot);
    }

    public static ItemArmor getArmourItemArmor(int slot) {
        return (ItemArmor) Onepop.MC.player.inventory.armorItemInSlot(slot).getItem();
    }

    public static Item getItem(int slot) {
        if (slot == -1) {
            return Items.AIR;
        }

        return Onepop.MC.player.inventory.getStackInSlot(slot).getItem();
    }

    public static ItemStack getItemStack(int slot) {
        return Onepop.MC.player.inventory.getStackInSlot(slot);
    }

    public static boolean isAir(int slot) {
        return Onepop.MC.player.inventory.getStackInSlot(slot).getItem() == Items.AIR;
    }

    public static boolean isArmourSlotAir(int slot) {
        return Onepop.MC.player.inventory.armorItemInSlot(slot).getItem() == Items.AIR;
    }

    public static int getCurrentItemSlotHotBar() {
        int slot = Onepop.MC.player.inventory.currentItem;

        return slot;
    }

    public static Item getCurrentItemHotBar() {
        Item item = Onepop.MC.player.inventory.getStackInSlot(Onepop.MC.player.inventory.currentItem).getItem();

        return item;
    }

    public static int findItemSlot(Item item) {
        int slot = -1;

        for (int i = 0; i < 36; i++) {
            Item items = Onepop.MC.player.inventory.getStackInSlot(i).getItem();

            if (items == item) {
                if (i < 9) {
                    i += 36;
                }

                slot = i;

                break;
            }
        }

        return slot;
    }

    public static int findItemSlotFromInventory(Item item) {
        int slot = -1;

        for (int i = 9; i < 36; i++) {
            Item items = Onepop.MC.player.inventory.getStackInSlot(i).getItem();

            if (items == item) {
                slot = i;

                break;
            }
        }

        return slot;
    }

    public static int findItemSlotFromHotBar(Item item) {
        int slot = -1;

        for (int i = 0; i < 9; i++) {
            Item items = Onepop.MC.player.inventory.getStackInSlot(i).getItem();

            if (items == item) {
                slot = i;

                break;
            }
        }

        return slot;
    }
}
