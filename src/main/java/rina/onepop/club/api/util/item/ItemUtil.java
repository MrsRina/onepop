package rina.onepop.club.api.util.item;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

/**
 * @author SrRina
 * @since 06/02/2021 at 12:31
 **/
public class ItemUtil {
    public static final Item[] ALL_HELMETS = {
            Items.DIAMOND_HELMET, Items.IRON_HELMET, Items.GOLDEN_HELMET, Items.CHAINMAIL_HELMET, Items.LEATHER_HELMET
    };

    public static final Item[] ALL_CHEST_PLATES = {
            Items.DIAMOND_CHESTPLATE, Items.IRON_CHESTPLATE, Items.GOLDEN_CHESTPLATE, Items.CHAINMAIL_CHESTPLATE, Items.LEATHER_CHESTPLATE
    };

    public static final Item[] ALL_LEGGINGS = {
            Items.DIAMOND_LEGGINGS, Items.IRON_LEGGINGS, Items.GOLDEN_LEGGINGS, Items.CHAINMAIL_LEGGINGS, Items.LEATHER_LEGGINGS
    };

    public static final Item[] ALL_BOOTS = {
            Items.DIAMOND_BOOTS, Items.IRON_BOOTS, Items.GOLDEN_BOOTS, Items.CHAINMAIL_BOOTS, Items.LEATHER_BOOTS
    };

    public static final Item[] ALL_PICKAXES = {
            Items.DIAMOND_PICKAXE, Items.IRON_PICKAXE, Items.GOLDEN_PICKAXE, Items.STONE_PICKAXE, Items.WOODEN_PICKAXE
    };

    public static final Item[] ALL_CLIENT_OFFHAND_ITEMS_USABLE =  {
            Items.TOTEM_OF_UNDYING, Items.GOLDEN_APPLE, Items.END_CRYSTAL, Items.BOW
    };

    public static boolean contains(Item[] list, Item item) {
        boolean contains = false;

        for (int i = 0; i < list.length; i++) {
            Item items = list[i];

            if (items == item) {
                contains = true;

                break;
            }
        }

        return contains;
    }

    public static float getArmorLevel(ItemStack firstArmor) {
        float k = ((ItemArmor) firstArmor.getItem()).damageReduceAmount;
        float l = EnchantmentHelper.getEnchantmentLevel(Enchantments.PROTECTION, firstArmor);

        return k + l;
    }
}