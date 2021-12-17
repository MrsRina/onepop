package rina.onepop.club.client.module.combat.offhand;

import me.rina.turok.util.TurokTick;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.Item;
import net.minecraft.network.play.server.SPacketUpdateHealth;
import rina.onepop.club.api.ISLClass;
import rina.onepop.club.api.module.Module;
import rina.onepop.club.api.module.impl.ModuleCategory;
import rina.onepop.club.api.module.registry.Registry;
import rina.onepop.club.api.setting.value.ValueBind;
import rina.onepop.club.api.setting.value.ValueBoolean;
import rina.onepop.club.api.setting.value.ValueEnum;
import rina.onepop.club.api.setting.value.ValueNumber;
import rina.onepop.club.api.util.client.NullUtil;
import rina.onepop.club.api.util.item.SlotUtil;
import rina.onepop.club.client.event.client.RunTickEvent;
import rina.onepop.club.client.event.network.PacketEvent;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

/**
 * @author SrRina
 * @since 10/02/2021 at 16:50
 **/
@Registry(name = "Offhand", tag = "Offhand", description = "Automatically set offhand item.", category = ModuleCategory.COMBAT)
public class ModuleOffhand extends Module {
    /* Keybinds. */
    public static ValueBoolean settingCustomBinds = new ValueBoolean("Custom Binds", "CustomBinds", "Enable custom keybinds for offhand!", true);

    public static ValueBind settingTotemBind = new ValueBind("Totem Bind", "TotemBind", "Sets totem bind.", -1);
    public static ValueBind settingGappleBind = new ValueBind("Gapple Bind", "GappleBind", "Sets gapple bind.", -1);
    public static ValueBind settingCrystalBind = new ValueBind("Crystal Bind", "CrystalBind", "Sets crystal bind.", -1);
    public static ValueBind settingBowBind = new ValueBind("Bow Bind", "BowBind", "Sets bow bind.", -1);
    public static ValueBind settingBedBind = new ValueBind("Bed Bind", "BedBind", "Sets bed bind.", -1);
    public static ValueBind settingSplashBind = new ValueBind("Splash Bind", "SplashBind", "Sets splash potion bind.", -1);

    /* Misc. */
    public static ValueBoolean settingSafe = new ValueBoolean("Safe", "Safe", "Safe use for others modes not totem!", false);
    public static ValueEnum settingOffhandMode = new ValueEnum("Offhand Mode", "OffhandMode", "Modes for items in your offhand.", OffhandMode.TOTEM);
    public static ValueEnum settingFindMode = new ValueEnum("Find Mode", "FindMode", "Modes to find the item(s).", FindMode.FULL);
    public static ValueNumber settingDelay = new ValueNumber("Delay", "Delay", "Sets the delay.", 2f, 0f, 10f);

    /* Totem. */
    public static ValueBoolean settingPredict = new ValueBoolean("Predict", "Predict", "Requires a totem in hot bar.", true);
    public static ValueEnum settingNoFail = new ValueEnum("No Fail", "NoFail", "No fails totem.", NoFail.HEALTH);
    public static ValueNumber settingSmartTotem = new ValueNumber("Smart Totem", "SmartTotem", "Automatically sets and remove at life", 20, 1, 20);

    private float currentHealth;
    private boolean sentPacket;

    private final TurokTick delay = new TurokTick();

    @Override
    public void onSetting() {
        if (settingOffhandMode.getValue() != OffhandMode.TOTEM) {
            settingSmartTotem.setEnabled(settingNoFail.getValue() != NoFail.NONE);
        } else {
            settingSmartTotem.setEnabled(false);
        }

        settingTotemBind.setEnabled(settingCustomBinds.getValue());
        settingGappleBind.setEnabled(settingCustomBinds.getValue());
        settingCrystalBind.setEnabled(settingCustomBinds.getValue());
        settingBowBind.setEnabled(settingCustomBinds.getValue());
        settingBedBind.setEnabled(settingCustomBinds.getValue());
        settingSplashBind.setEnabled(settingCustomBinds.getValue());

        if (settingCustomBinds.getValue() && settingTotemBind.getValue() && settingOffhandMode.getValue() != OffhandMode.TOTEM) {
            settingGappleBind.setValue(false);
            settingCrystalBind.setValue(false);
            settingBowBind.setValue(false);
            settingBedBind.setValue(false);
            settingSplashBind.setValue(false);

            settingOffhandMode.setValue(OffhandMode.TOTEM);
        }

        if (settingCustomBinds.getValue() && settingGappleBind.getValue() && settingOffhandMode.getValue() != OffhandMode.GAPPLE) {
            settingTotemBind.setValue(false);
            settingCrystalBind.setValue(false);
            settingBowBind.setValue(false);
            settingBedBind.setValue(false);
            settingSplashBind.setValue(false);

            settingOffhandMode.setValue(OffhandMode.GAPPLE);
        }

        if (settingCustomBinds.getValue() && settingCrystalBind.getValue() && settingOffhandMode.getValue() != OffhandMode.CRYSTAL) {
            settingTotemBind.setValue(false);
            settingGappleBind.setValue(false);
            settingBowBind.setValue(false);
            settingBedBind.setValue(false);
            settingSplashBind.setValue(false);

            settingOffhandMode.setValue(OffhandMode.CRYSTAL);
        }

        if (settingCustomBinds.getValue() && settingCrystalBind.getValue() && settingOffhandMode.getValue() != OffhandMode.CRYSTAL) {
            settingTotemBind.setValue(false);
            settingGappleBind.setValue(false);
            settingBowBind.setValue(false);
            settingBedBind.setValue(false);
            settingSplashBind.setValue(false);

            settingOffhandMode.setValue(OffhandMode.CRYSTAL);
        }

        if (settingCustomBinds.getValue() && settingBowBind.getValue() && settingOffhandMode.getValue() != OffhandMode.BOW) {
            settingTotemBind.setValue(false);
            settingGappleBind.setValue(false);
            settingCrystalBind.setValue(false);
            settingBedBind.setValue(false);
            settingSplashBind.setValue(false);

            settingOffhandMode.setValue(OffhandMode.BOW);
        }

        if (settingCustomBinds.getValue() && settingBedBind.getValue() && settingOffhandMode.getValue() != OffhandMode.BED) {
            settingTotemBind.setValue(false);
            settingGappleBind.setValue(false);
            settingCrystalBind.setValue(false);
            settingBowBind.setValue(false);
            settingSplashBind.setValue(false);

            settingOffhandMode.setValue(OffhandMode.BED);
        }

        if (settingCustomBinds.getValue() && settingSplashBind.getValue() && settingOffhandMode.getValue() != OffhandMode.SPLASH) {
            settingTotemBind.setValue(false);
            settingGappleBind.setValue(false);
            settingCrystalBind.setValue(false);
            settingBowBind.setValue(false);
            settingBedBind.setValue(false);

            settingOffhandMode.setValue(OffhandMode.SPLASH);
        }
    }

    @Listener
    public void onTick(RunTickEvent event) {
        if (NullUtil.isPlayerWorld()) {
            return;
        }

        if (settingNoFail.getValue() != NoFail.PACKET) {
            this.doOffhandCalculate(mc.player.getHealth() + mc.player.getAbsorptionAmount());
        }
    }

    @Listener
    public void onListen(PacketEvent.Receive event) {
        if (NullUtil.isPlayerWorld()) {
            return;
        }

        if (event.getPacket() instanceof SPacketUpdateHealth) {
            SPacketUpdateHealth packet = (SPacketUpdateHealth) event.getPacket();

            this.currentHealth = packet.getHealth() + mc.player.getAbsorptionAmount();

            if (settingPredict.getValue()) {
                if (this.currentHealth <= mc.player.getHealth() + mc.player.getAbsorptionAmount()) {
                    int slot = SlotUtil.findItemSlotFromHotBar(Items.TOTEM_OF_UNDYING);

                    if (slot != -1) {
                        mc.player.inventory.currentItem = slot;
                        mc.playerController.updateController();
                    }
                }
            }

            if (settingNoFail.getValue() == NoFail.PACKET) {
                this.doOffhandCalculate(this.currentHealth);
            }
        }
    }

    public void doOffhandCalculate(float health) {
        if (!this.delay.isPassedMS(settingDelay.getValue().floatValue())) {
            return;
        }

        if ((mc.currentScreen != null && !(mc.currentScreen instanceof GuiChat || mc.currentScreen instanceof GuiIngameMenu)) || mc.player.isCreative()) {
            return;
        }

        if (settingOffhandMode.getValue() != OffhandMode.TOTEM) {
            if (settingNoFail.getValue() != NoFail.NONE) {
                if (health <= settingSmartTotem.getValue().intValue() && settingOffhandMode.getValue() != OffhandMode.TOTEM) {
                    this.doOffhand(Items.TOTEM_OF_UNDYING);

                    return;
                }

                if (health > settingSmartTotem.getValue().intValue() && settingOffhandMode.getValue() == OffhandMode.GAPPLE) {
                    this.doOffhand(Items.GOLDEN_APPLE);

                    return;
                }

                if (health > settingSmartTotem.getValue().intValue() && settingOffhandMode.getValue() == OffhandMode.CRYSTAL) {
                    this.doOffhand(Items.END_CRYSTAL);

                    return;
                }

                if (health > settingSmartTotem.getValue().intValue() && settingOffhandMode.getValue() == OffhandMode.BOW) {
                    this.doOffhand(Items.BOW);

                    return;
                }

                if (health > settingSmartTotem.getValue().intValue() && settingOffhandMode.getValue() == OffhandMode.BED) {
                    this.doOffhand(Items.BED);

                    return;
                }

                if (health > settingSmartTotem.getValue().intValue() && settingOffhandMode.getValue() == OffhandMode.SPLASH) {
                    this.doOffhand(Items.SPLASH_POTION);
                }
            } else if (settingNoFail.getValue() == NoFail.NONE) {
                if (settingOffhandMode.getValue() == OffhandMode.GAPPLE) {
                    this.doOffhand(Items.GOLDEN_APPLE);

                    return;
                }

                if (settingOffhandMode.getValue() == OffhandMode.CRYSTAL) {
                    this.doOffhand(Items.END_CRYSTAL);

                    return;
                }

                if (settingOffhandMode.getValue() == OffhandMode.BOW) {
                    this.doOffhand(Items.BOW);

                    return;
                }

                if (settingOffhandMode.getValue() == OffhandMode.BED) {
                    this.doOffhand(Items.BED);
                }
            }
        } else {
            this.doOffhand(Items.TOTEM_OF_UNDYING);
        }
    }

    public void doOffhand(Item item) {
        if (NullUtil.isPlayerWorld()) {
            return;
        }

        if (mc.currentScreen instanceof GuiInventory || ISLClass.mc.currentScreen instanceof GuiContainerCreative) {
            return;
        }

        if (mc.player.getHeldItemOffhand().getItem() == item) {
            this.delay.reset();

            return;
        }

        int slot = this.doFindSlot(item);

        if (slot != -1) {
            this.doOffhandUpdate(slot);
        } else {
            if (item != Items.TOTEM_OF_UNDYING && settingSafe.getValue()) {
                int slotTotems = doFindSlot(Items.TOTEM_OF_UNDYING);

                if (slotTotems != -1 && ISLClass.mc.player.getHeldItemOffhand().getItem() != Items.TOTEM_OF_UNDYING) {
                    this.doOffhandUpdate(slotTotems);
                }
            }
        }
    }

    public void doOffhandUpdate(int slot) {
        mc.playerController.windowClick(0, slot, 0, ClickType.PICKUP, ISLClass.mc.player);
        mc.playerController.windowClick(0, 45, 0, ClickType.PICKUP, ISLClass.mc.player);
        mc.playerController.windowClick(0, slot, 0, ClickType.PICKUP, ISLClass.mc.player);
        mc.playerController.updateController();
    }

    public int doFindSlot(Item item) {
        int slot = -1;

        switch ((FindMode) settingFindMode.getValue()) {
            case FULL: {
                slot = SlotUtil.findItemSlot(item);

                break;
            }

            case INVENTORY: {
                slot = SlotUtil.findItemSlotFromInventory(item);

                break;
            }

            case HOT_BAR: {
                slot = SlotUtil.findItemSlotFromHotBar(item) != -1 ? SlotUtil.findItemSlotFromHotBar(item) + 36 : -1;

                break;
            }
        }

        return slot;
    }
}