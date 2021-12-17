package rina.onepop.club.client.module.misc.armoralert;

import me.rina.turok.render.font.management.TurokFontManager;
import me.rina.turok.util.TurokDisplay;
import me.rina.turok.util.TurokMath;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import rina.onepop.club.api.module.Module;
import rina.onepop.club.api.module.impl.ModuleCategory;
import rina.onepop.club.api.module.registry.Registry;
import rina.onepop.club.api.setting.value.ValueBoolean;
import rina.onepop.club.api.setting.value.ValueEnum;
import rina.onepop.club.api.setting.value.ValueNumber;
import rina.onepop.club.api.setting.value.ValueString;
import rina.onepop.club.api.social.management.SocialManager;
import rina.onepop.club.api.social.type.SocialType;
import rina.onepop.club.api.util.chat.ChatUtil;
import rina.onepop.club.api.util.client.NullUtil;
import rina.onepop.club.client.event.client.RunTickEvent;
import rina.onepop.club.client.module.client.ModuleHUD;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

import java.util.ArrayList;
import java.util.List;

/**
 * @author SrRina
 * @since 15/05/2021 at 17:21
 **/
@Registry(name = "Armor Alert", tag = "ArmorAlert", description = "Alerts if armor low durability.", category = ModuleCategory.MISC)
public class ModuleArmorAlert extends Module {
    /* Misc. */
    public static ValueBoolean settingFriend = new ValueBoolean("Friend", "Friend", "Alert friends!", true);
    public static ValueNumber settingArmorPercentage = new ValueNumber("Armor Percentage", "ArmorPercentage", "The percentage of armor to alerts!", 25, 0, 100);
    public static ValueNumber settingArmorQuantity = new ValueNumber("Armor Quantity", "ArmorQuantity", "The quantity of armor equipped to notify.", 4, 1, 4);
    public static ValueEnum settingAlertMode = new ValueEnum("Mode Alert", "ModeAlert", "Modes for alert!", Mode.CHAT);

    /* Chat message. */
    public static ValueString settingMessageInput = new ValueString("Message Input", "MessageInput", "The input message!", "/w <player> your <armor> breaking!");

    private boolean notified;
    private String staticText;

    private final List<EntityPlayer> confirmedMessage = new ArrayList<>();

    @Override
    public void onSetting() {
        settingMessageInput.setEnabled(settingFriend.getValue());
    }

    @Override
    public void onRender2D() {
        TurokDisplay display = new TurokDisplay(mc);

        if (this.notified && settingAlertMode.getValue() == Mode.RENDER) {
            TurokFontManager.render(this.staticText, display.getScaledWidth() / 2 - (TurokFontManager.getStringWidth(this.staticText) / 2), 10, true, ModuleHUD.settingColor.getColor());
        }
    }

    @Listener
    public void onTick(RunTickEvent event) {
        if (NullUtil.isPlayerWorld()) {
            return;
        }

        final ItemStack currentBreakingArmor = this.getArmorBreaking(mc.player);

        if (currentBreakingArmor == null && this.notified) {
            this.notified = false;
        }

        if (currentBreakingArmor != null && !this.notified) {
            this.staticText = "Your " + this.getCorrectEnglish(currentBreakingArmor) + " breaking!";

            switch ((Mode) settingAlertMode.getValue()) {
                case CHAT: {
                    this.print(this.staticText);

                    break;
                }

                case RENDER: {
                    break;
                }
            }

            this.notified = true;
        }

        if (settingFriend.getValue()) {
            for (EntityPlayer entities : mc.world.playerEntities) {
                if (entities == mc.player || SocialManager.get(entities.getName()) == null || SocialManager.get(entities.getName()).getType() != SocialType.FRIEND) {
                    continue;
                }

                final ItemStack currentFriendBreakingArmor = this.getArmorBreaking(entities);

                if (currentFriendBreakingArmor == null && this.confirmedMessage.contains(entities)) {
                    this.confirmedMessage.remove(entities);
                }

                if (currentFriendBreakingArmor != null && !this.confirmedMessage.contains(entities)) {
                    final String formattedMessage = settingMessageInput.getValue().replaceAll("<player>", entities.getName()).replaceAll("<armor>", this.getCorrectEnglish(currentFriendBreakingArmor));

                    ChatUtil.message(formattedMessage);

                    this.confirmedMessage.add(entities);
                }
            }
        }
    }

    /**
     * Fix "are" "is" for the type of armor, better english.
     */
    public String getCorrectEnglish(ItemStack stack) {
        if (stack.getItem() == Items.DIAMOND_HELMET || stack.getItem() == Items.IRON_HELMET || stack.getItem() == Items.GOLDEN_HELMET || stack.getItem() == Items.CHAINMAIL_HELMET || stack.getItem() == Items.LEATHER_HELMET) {
            return "helmet is";
        }

        if (stack.getItem() == Items.DIAMOND_CHESTPLATE || stack.getItem() == Items.IRON_CHESTPLATE || stack.getItem() == Items.GOLDEN_CHESTPLATE || stack.getItem() == Items.CHAINMAIL_CHESTPLATE || stack.getItem() == Items.LEATHER_CHESTPLATE || stack.getItem() == Items.ELYTRA) {
            return "chest plate is";
        }

        if (stack.getItem() == Items.DIAMOND_LEGGINGS || stack.getItem() == Items.IRON_LEGGINGS || stack.getItem() == Items.GOLDEN_LEGGINGS || stack.getItem() == Items.CHAINMAIL_LEGGINGS || stack.getItem() == Items.LEATHER_LEGGINGS) {
            return "leggings are";
        }

        if (stack.getItem() == Items.DIAMOND_BOOTS || stack.getItem() == Items.IRON_BOOTS || stack.getItem() == Items.GOLDEN_BOOTS || stack.getItem() == Items.CHAINMAIL_BOOTS || stack.getItem() == Items.LEATHER_BOOTS) {
            return "boots are";
        }

        return "";
    }

    public ItemStack getArmorBreaking(final EntityPlayer player) {
        float mostDamagePercentage = 101;
        ItemStack mostDamageItemStack = null;

        int equippedCount = 0;

        for (ItemStack armors : player.inventory.armorInventory) {
            if (armors.getItem() == Items.AIR) {
                continue;
            }

            equippedCount++;

            final float percentage = TurokMath.amount(armors.getItemDamage(), armors.getMaxDamage());

            if (percentage < mostDamagePercentage) {
                mostDamagePercentage = percentage;
                mostDamageItemStack = armors;
            }
        }

        return equippedCount >= settingArmorQuantity.getValue().intValue() && mostDamagePercentage != 101 && mostDamagePercentage <= settingArmorPercentage.getValue().intValue() ? mostDamageItemStack : null;
    }
}
