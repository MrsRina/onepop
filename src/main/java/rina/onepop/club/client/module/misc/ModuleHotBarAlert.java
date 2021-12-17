package rina.onepop.club.client.module.misc;

import me.rina.turok.util.TurokTick;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import rina.onepop.club.api.module.Module;
import rina.onepop.club.api.module.impl.ModuleCategory;
import rina.onepop.club.api.module.registry.Registry;
import rina.onepop.club.api.setting.value.ValueBoolean;
import rina.onepop.club.api.setting.value.ValueNumber;
import rina.onepop.club.api.setting.value.ValueString;
import rina.onepop.club.api.util.client.NullUtil;
import rina.onepop.club.client.event.client.ClientTickEvent;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Rina
 * @since 12/10/2021 at 00:34am
 **/
@Registry(name = "Hot Bar Alert", tag = "HotBarAlert", description = "Alert items on your hot bar.", category = ModuleCategory.MISC)
public class ModuleHotBarAlert extends Module {
    public static ValueString settingText = new ValueString("String", "String", "Set string alert.", "Slot <slot> remains <slotamount> <item> of <totally>!");
    public static ValueNumber settingCooldown = new ValueNumber("Cooldown", "Cooldown", "Cooldown", 32, 1, 60);
    public static ValueNumber settingMinimumStack = new ValueNumber("Min. Stack", "MinimumStack", "Minimum stack for alert.", 32, 1, 64);
    public static ValueBoolean settingAllItems = new ValueBoolean("All Items", "AllItems", "All items.", false);
    public static ValueBoolean settingGoldenApple = new ValueBoolean("Golden Apple", "GoldenApple", "Alert for golden apple item.", true);
    public static ValueBoolean settingEndCrystal = new ValueBoolean("End Crystal", "EndCrystal", "Alert for end crystal item.", true);
    public static ValueBoolean settingTotem = new ValueBoolean("Totem of Undying", "TotemofUndying", "Alert for totem of undying item.", true);
    public static ValueBoolean settingBottleXP = new ValueBoolean("Bottle XP", "BottleXP", "Alert for bottle xp item.", true);
    public static ValueBoolean settingChorus = new ValueBoolean("Chorus", "Chorus", "Alert for chorus fruit item.", true);
    public static ValueBoolean settingObsidian = new ValueBoolean("Obsidian", "Obsidian", "Alert for obisdian item block.", true);
    public static ValueBoolean settingEnderPerls = new ValueBoolean("Ender Perls", "EnderPerls", "Alert for ender perls item.", true);
    public static ValueBoolean settingWeb = new ValueBoolean("Web", "Web", "Alert for web item.", true);

    private final List<Item> listItem = new ArrayList<>();
    private final TurokTick cooldown = new TurokTick();

    @Override
    public void onSetting() {
        settingGoldenApple.setEnabled(!settingAllItems.getValue());
        settingEndCrystal.setEnabled(!settingAllItems.getValue());
        settingTotem.setEnabled(!settingAllItems.getValue());
        settingBottleXP.setEnabled(!settingAllItems.getValue());
        settingChorus.setEnabled(!settingAllItems.getValue());
        settingObsidian.setEnabled(!settingAllItems.getValue());
        settingEnderPerls.setEnabled(!settingAllItems.getValue());
        settingWeb.setEnabled(!settingAllItems.getValue());
    }

    @Listener
    public void onClientTickEvent(ClientTickEvent event) {
        if (NullUtil.isPlayerWorld()) {
            return;
        }

        if (this.cooldown.isPassedSI(settingCooldown.getValue().intValue())) {
            this.listItem.clear();
            this.cooldown.reset();
        }

        for (int i = 0; i < 9; i++) {
            final ItemStack itemStack = mc.player.inventory.getStackInSlot(i);

            if (this.accept(itemStack.getItem()) && itemStack.getMaxStackSize() > 1 && !this.listItem.contains(itemStack.getItem()) && itemStack.getCount() <= settingMinimumStack.getValue().intValue()) {
                this.print(this.getDataFromItem(itemStack, i));
                this.listItem.add(itemStack.getItem());
            }
        }
    }

    public String getDataFromItem(ItemStack itemStack, int slot) {
        String data = settingText.getValue();

        data = data.replaceAll("<slot>", "" + slot);
        data = data.replaceAll("<slotamount>", "" + itemStack.getCount());

        int count = this.getCountItem(itemStack.getItem()) + (mc.player.getHeldItemOffhand().getItem() == itemStack.getItem() ? mc.player.getHeldItemOffhand().getCount() : 0);

        data = data.replaceAll("<totally>", "" + count);
        data = data.replaceAll("<item>", itemStack.getDisplayName());

        return data;
    }

    public boolean accept(Item item) {
        if (item != Items.AIR && settingAllItems.getValue()) {
            return true;
        }

        if (item == Items.GOLDEN_APPLE && settingGoldenApple.getValue()) {
            return true;
        }

        if (item == Items.TOTEM_OF_UNDYING && settingTotem.getValue()) {
            return true;
        }

        if (item == Items.END_CRYSTAL && settingEndCrystal.getValue()) {
            return true;
        }

        if (item == Items.EXPERIENCE_BOTTLE && settingBottleXP.getValue()) {
            return true;
        }

        if (item == Items.CHORUS_FRUIT && settingChorus.getValue()) {
            return true;
        }

        if (item == Items.ENDER_PEARL && settingEnderPerls.getValue()) {
            return true;
        }

        if (item == Item.getItemFromBlock(Blocks.WEB) && settingWeb.getValue()) {
            return true;
        }

        return item == Item.getItemFromBlock(Blocks.OBSIDIAN) && settingObsidian.getValue();
    }

    public int getCountItem(Item item) {
        return mc.player.inventory.mainInventory.stream().filter(stack -> stack.getItem() == item).mapToInt(ItemStack::getCount).sum();
    }
}
