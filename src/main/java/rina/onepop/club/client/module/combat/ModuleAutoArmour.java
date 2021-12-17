package rina.onepop.club.client.module.combat;

import me.rina.turok.util.TurokMath;
import me.rina.turok.util.TurokTick;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemExpBottle;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.util.EnumHand;
import rina.onepop.club.Onepop;
import rina.onepop.club.api.ISLClass;
import rina.onepop.club.api.module.Module;
import rina.onepop.club.api.module.impl.ModuleCategory;
import rina.onepop.club.api.module.registry.Registry;
import rina.onepop.club.api.setting.value.ValueBoolean;
import rina.onepop.club.api.setting.value.ValueNumber;
import rina.onepop.club.api.social.management.SocialManager;
import rina.onepop.club.api.social.type.SocialType;
import rina.onepop.club.api.tracker.impl.WindowClickTracker;
import rina.onepop.club.api.util.client.NullUtil;
import rina.onepop.club.api.util.item.ItemUtil;
import rina.onepop.club.api.util.item.SlotUtil;
import rina.onepop.club.client.event.client.ClientTickEvent;
import rina.onepop.club.client.event.network.PacketEvent;
import rina.onepop.club.client.manager.network.HotBarManager;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

import java.util.HashMap;
import java.util.Map;

/**
 * @author SrRina
 * @since 16/02/2021 at 12:35
 *
 * // Made by jake. // NO MORE BY JAKE, I REWROTE IN 28/06/2021 AT 01:43AM
 * // Jake also leaked the client... :SOB:
 *
 * The -1 & 1 numbers, is because the slot id of recipe is 0, so we add + 1
 * cause the first craft slot is 1, 2, 3 & 4! the 5+ is armor!
 *
 **/
@Registry(name = "Auto-Armor", tag = "AutoArmor", description = "Auto equip armour, and no, the name module is not in british.", category = ModuleCategory.COMBAT)
public class ModuleAutoArmour extends Module {
    // Misc.
    public static ValueNumber settingEquipDelay = new ValueNumber("Equip Delay", "EquipDelay", "Delay for next equip event.", 20, 0, 100);
    public static ValueBoolean settingAutoGear = new ValueBoolean("Auto-Gear", "AutoGear", "Gear player... I hate gear players (FLLMALL)!", false);
    public static ValueBoolean settingSilent = new ValueBoolean("Silent", "Silent", "Silent gear.", false);
    public static ValueNumber settingCooldown = new ValueNumber("Cooldown", "Cooldown", "Cooldown for equip all again.", 250, 50, 500);
    public static ValueNumber settingPaketDelay = new ValueNumber("Packet Delay", "PacketDelay", "Delay for send packets, legit speed!", 3, 0, 6);

    public static ValueBoolean settingSafe = new ValueBoolean("Safe", "Safe", "Safe moments.", true);

    public static ValueBoolean settingAllowsCrystals = new ValueBoolean("Allows Crystals", "AllowCrystals", "Crystals can kill you!", false);
    public static ValueBoolean settingClosestPlayer = new ValueBoolean("Closest Player", "ClosestPlayer", "Prevents player on your range.", true);
    public static ValueNumber settingRangeLimit = new ValueNumber("Range Limit", "RangeLimit", "Range limit for gear.", 6f, 4f, 13f);
    public static ValueBoolean settingIgnoreFriend = new ValueBoolean("Ignore Friends", "IgnoreFriends", "Ignore friends.", true);

    private static final int ALL = 5;
    private static final int HELMET = 3;
    private static final int CHEST_PLATE = 2;
    private static final int LEGGINGS = 1;
    private static final int BOOTS = 0;

    private final Map<Integer, Integer> recoveryMap = new HashMap<>();
    
    private EntityPlayer closestPlayer;
    private boolean safeOfCrystals;

    private boolean interrupted;
    private int flag = ALL;

    private int slotInArmor = -1;
    private int slotToArmor = -1;

    private int slotInArmorUnequip;

    private final TurokTick delayer = new TurokTick();
    private final TurokTick cooldown = new TurokTick();
    private final TurokTick stamp = new TurokTick();

    @Override
    public void onSetting() {
        settingSilent.setEnabled(settingAutoGear.getValue());
        settingSafe.setEnabled(settingAutoGear.getValue() && !settingSilent.getValue());
        settingPaketDelay.setEnabled(settingAutoGear.getValue() && !settingSilent.getValue());
        settingCooldown.setEnabled(settingAutoGear.getValue() && !settingSilent.getValue());
        settingClosestPlayer.setEnabled(settingAutoGear.getValue() && settingSafe.getValue() && !settingSilent.getValue());
        settingRangeLimit.setEnabled(settingAutoGear.getValue() && settingSafe.getValue() && !settingSilent.getValue() && settingClosestPlayer.getValue());
        settingIgnoreFriend.setEnabled(settingAutoGear.getValue() && settingSafe.getValue() && !settingSilent.getValue() && settingClosestPlayer.getValue());
        settingAllowsCrystals.setEnabled(settingAutoGear.getValue() && !settingSilent.getValue() && settingSafe.getValue());
    }

    @Listener
    public void onSendPacket(PacketEvent.Send event) {
        if (event.getPacket() instanceof CPacketPlayerTryUseItem) {
            final CPacketPlayerTryUseItem packet = (CPacketPlayerTryUseItem) event.getPacket();

            if ((packet.getHand() == EnumHand.OFF_HAND && mc.player.getHeldItemOffhand().getItem() instanceof ItemExpBottle) || SlotUtil.getItem(HotBarManager.currentItem(HotBarManager.SERVER)) instanceof ItemExpBottle) {
                this.cooldown.reset();
            }
        }
    }

    @Listener
    public void onTick(ClientTickEvent event) {
        if (NullUtil.isPlayerWorld()) {
            return;
        }

        this.safeOfCrystals = this.isSafeOfCrystals(mc.player);

        if ((mc.currentScreen != null && !(mc.currentScreen instanceof GuiChat || mc.currentScreen instanceof GuiIngameMenu)) || mc.player.isCreative()) {
            return;
        }

        this.updateClosetsPlayer();

        if (settingAutoGear.getValue() && !settingSilent.getValue()) {
            int lowerArmorDurabilitySlot = this.getLowerArmorDurability(mc.player);

            if (lowerArmorDurabilitySlot != -1 && !this.cooldown.isPassedMS(settingCooldown.getValue().intValue()) && this.safeOfCrystals) {
                this.flag = lowerArmorDurabilitySlot;

                this.updateUnequip();
                this.interrupted = false;
            }
        }

        if (!settingAutoGear.getValue() || this.closestPlayer != null || this.cooldown.isPassedMS(settingCooldown.getValue().intValue()) || !this.safeOfCrystals) {
            this.interrupted = true;
            this.flag = ALL;
        }

        if (this.cooldown.isPassedMS(settingCooldown.getValue().intValue() * 2)) {
            this.recoveryMap.clear();
        }

        if (this.interrupted) {
            this.updateArmor();

            this.slotInArmorUnequip = -1;

            if (this.closestPlayer == null && settingAutoGear.getValue() && !settingSilent.getValue() && settingSafe.getValue() && settingClosestPlayer.getValue()) {
                this.interrupted = false;
            }
        }
    }

    public void updateClosetsPlayer() {
        if (!settingAutoGear.getValue() || settingSilent.getValue() || !settingSafe.getValue() || !settingClosestPlayer.getValue()) {
            this.closestPlayer = null;

            return;
        }

        EntityPlayer target = null;

        for (EntityPlayer entities : mc.world.playerEntities) {
            if (entities == null || entities.isDead || entities == mc.player) {
                continue;
            }

            if (mc.player.getDistance(entities) <= settingRangeLimit.getValue().floatValue()) {
                final boolean flagFriend = SocialManager.is(entities.getName(), SocialType.FRIEND);

                if (flagFriend || settingIgnoreFriend.getValue()) {
                    target = entities;
                }
            }
        }

        this.closestPlayer = target;
    }

    public void updateArmor() {
        float delay = settingEquipDelay.getValue().intValue();

        if (this.delayer.isPassedMS(delay)) {
            this.doVerification(HELMET, ItemUtil.ALL_HELMETS);
            this.doVerification(CHEST_PLATE, ItemUtil.ALL_CHEST_PLATES);
            this.doVerification(LEGGINGS, ItemUtil.ALL_LEGGINGS);
            this.doVerification(BOOTS, ItemUtil.ALL_BOOTS);

            if (this.slotToArmor != -1 && this.slotInArmor != -1) {
                this.doEquip(this.slotInArmor, this.slotToArmor);
                this.reset();

                this.delayer.reset();
            }
        }
    }

    public void reset() {
        this.slotToArmor = -1;
        this.slotInArmor = -1;
    }

    public void doVerification(int slotIn, Item[] list) {
        if (SlotUtil.isArmourSlotAir(slotIn) && this.slotInArmor == -1 && this.slotToArmor == -1) {
            for (Item items : list) {
                int foundSlot = SlotUtil.findItemSlot(items);

                if (foundSlot != -1) {
                    this.doSync(slotIn, foundSlot < 9 ? foundSlot + 36 : foundSlot);

                    break;
                }
            }
        }
    }

    public void doSync(int slotIn, int slotTo) {
        this.slotInArmor = slotIn;
        this.slotToArmor = slotTo;
    }

    public void doUnequipVerification(int slot) {
        if (this.flag == slot) {
            return;
        }

        if (!SlotUtil.isArmourSlotAir(slot) && this.slotInArmorUnequip == -1) {
            this.slotInArmorUnequip = slot;
        }
    }

    public void updateUnequip() {
        if (this.flag == ALL) {
            return;
        }

        float delay = settingEquipDelay.getValue().intValue();

        if (this.delayer.isPassedMS(delay)) {
            this.doUnequipVerification(HELMET);
            this.doUnequipVerification(CHEST_PLATE);
            this.doUnequipVerification(LEGGINGS);
            this.doUnequipVerification(BOOTS);

            if (this.slotInArmorUnequip != -1) {
                this.doUnequip(this.slotInArmorUnequip);

                this.slotInArmorUnequip = -1;
                this.delayer.reset();
            }
        }
    }

    public void doUnequip(int slotIn) {
        int findFirst = SlotUtil.findFirstSlotAirFromInventory();

        if (findFirst == -1) {
            return;
        }

        Onepop.getTrackerManager().dispatch(new WindowClickTracker(0, 8 - (slotIn), 0, ClickType.PICKUP, ISLClass.mc.player));
        Onepop.getTrackerManager().dispatch(new WindowClickTracker(0, findFirst, 0, ClickType.PICKUP, ISLClass.mc.player));
        Onepop.getTrackerManager().dispatch(new WindowClickTracker(0, 8 - (slotIn), 0, ClickType.PICKUP, ISLClass.mc.player));
    }

    public void doEquip(int slotIn, int slotTo) {
        mc.playerController.windowClick(0, slotTo, 0, ClickType.PICKUP, ISLClass.mc.player);
        mc.playerController.windowClick(0, 8 - slotIn, 0, ClickType.PICKUP, ISLClass.mc.player);
        mc.playerController.windowClick(0, slotTo, 0, ClickType.PICKUP, ISLClass.mc.player);
        mc.playerController.updateController();
    }

    public boolean isSafeOfCrystals(final EntityPlayer player) {
        if (player == null || settingAllowsCrystals.getValue()) {
            return true;
        }

        boolean safe = true;

        for (Entity entities : mc.world.loadedEntityList) {
            if (entities instanceof EntityEnderCrystal && player.getDistance(entities) <= 6f) {
                safe = false;

                break;
            }
        }

        return safe;
    }

    public int getLowerArmorDurability(final EntityPlayer player) {
        float mostDamagePercentage = 101;
        int slot = -1;

        for (int i = 0; i < 4; i++) {
            final ItemStack armors = player.inventory.armorInventory.get(i);

            if (armors.getItem() == Items.AIR) {
                continue;
            }

            final float percentage = TurokMath.amount(armors.getItemDamage(), armors.getMaxDamage());

            if (percentage >= 90) {
                continue;
            }

            if (percentage < mostDamagePercentage) {
                mostDamagePercentage = percentage;
                slot = i;
            }
        }

        return mostDamagePercentage != 101 ? slot : -1;
    }
}
