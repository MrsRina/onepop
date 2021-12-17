package rina.onepop.club.client.module.misc.autoeat;

import rina.onepop.club.api.ISLClass;
import rina.onepop.club.api.module.Module;
import rina.onepop.club.api.module.impl.ModuleCategory;
import rina.onepop.club.api.module.registry.Registry;
import rina.onepop.club.api.setting.value.ValueBoolean;
import rina.onepop.club.api.setting.value.ValueEnum;
import rina.onepop.club.api.setting.value.ValueNumber;
import rina.onepop.club.api.util.client.NullUtil;
import rina.onepop.club.api.util.item.SlotUtil;
;
import rina.onepop.club.client.event.client.RunTickEvent;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.util.EnumHand;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

/**
 * @author SrRina
 * @since 20/02/2021 at 00:25
 **/
@Registry(name = "Auto-Eat", tag = "AutoEat", description = "Find an food in hot bar if is hunger and automatically eat.", category = ModuleCategory.MISC)
public class ModuleAutoEat extends Module {
    public static ValueEnum settingMode = new ValueEnum("Mode", "Mode", "Modes to verify if eats or no!", Mode.HEALTH);

    /* Food misc. */
    public static ValueNumber settingFood = new ValueNumber("Food", "Food", "Food stats to start eat!", 10, 1, 20);
    public static ValueNumber settingFoodFill = new ValueNumber("Fill", "FoodFill", "Stop eating in.", 20, 1, 20);

    /* Health misc. */
    public static ValueBoolean settingOnlyGoldenApple = new ValueBoolean("Only Golden Apple", "OnlyGoldenApple", "Only golden eat!", false);
    public static ValueNumber settingHealth = new ValueNumber("Health", "Health", "Health to start eat.", 20, 1, 20);
    public static ValueNumber settingHealthFill = new ValueNumber("Fill", "HealthFill", "Stop eating in.", 20, 1, 20);

    private boolean isToEat;
    private boolean isReturned;

    private int oldSlot;
    private int newSlot;

    @Override
    public void onSetting() {
        settingFood.setEnabled(settingMode.getValue() == Mode.FOOD);
        settingFoodFill.setEnabled(settingMode.getValue() == Mode.FOOD);

        settingHealth.setEnabled(settingMode.getValue() == Mode.HEALTH);
        settingHealthFill.setEnabled(settingMode.getValue() == Mode.HEALTH);
        settingOnlyGoldenApple.setEnabled(settingMode.getValue() == Mode.HEALTH);

        if (settingHealthFill.getValue().intValue() <= settingHealth.getValue().intValue()) {
            settingHealthFill.setValue(settingHealth.getValue());
        }

        if (settingFoodFill.getValue().intValue() <= settingFood.getValue().intValue()) {
            settingFoodFill.setValue(settingFood.getValue());
        }
    }

    @Override
    public void onShutdown() {
        this.setDisabled();
    }

    @Listener
    public void onListen(RunTickEvent event) {
        if (NullUtil.isPlayerWorld()) {
            return;
        }

        if (ISLClass.mc.player.isCreative()) {
            return;
        }

        if (this.isToEat) {
            boolean flagO = this.newSlot == 40; // Offhand.
            boolean flagM = this.newSlot != 40; // Main hand.

            if (settingMode.getValue() == Mode.HEALTH && settingOnlyGoldenApple.getValue() == false && ISLClass.mc.player.getFoodStats().getFoodLevel() == 20) {
                this.isToEat = false;
            }

            // Set to back old slot.
            this.isReturned = true;

            /*
             * Offhand doesn't have food but main hand yes!!
             */
            if (flagO == false) {
                Item item = SlotUtil.getItemStack(this.newSlot).getItem();

                if (this.doAccept(item)) {
                    ISLClass.mc.player.inventory.currentItem = this.newSlot;

                    this.doEat();
                } else {
                    // Last check to disable.
                    this.newSlot = this.findFoodSlot();
                    this.isToEat = this.newSlot == -1;
                }
            }

            /*
             * Main hand doesn't have food but offhand yes.
             */
            if (flagM == false) {
                Item item = SlotUtil.getItemStack(this.newSlot).getItem();

                if (this.doAccept(item)) {
                    this.doEat();
                } else {
                    // Last check to disable.
                    this.newSlot = this.findFoodSlot();
                    this.isToEat = this.newSlot == -1;
                }
            }
        } else {
            if (this.isReturned) {
                ISLClass.mc.gameSettings.keyBindUseItem.pressed = false;
                ISLClass.mc.player.inventory.currentItem = this.oldSlot;

                this.isReturned = false;
            } else {
                this.oldSlot = ISLClass.mc.player.inventory.currentItem;
            }
        }

        int slot = this.findFoodSlot();

        if (slot != -1) {
            Item item = SlotUtil.getItem(slot);

            switch ((Mode) settingMode.getValue()) {
                case HEALTH: {
                    this.isToEat = ISLClass.mc.player.getHealth() <= settingHealth.getValue().intValue() && (ISLClass.mc.player.getFoodStats().getFoodLevel() != 20 || settingOnlyGoldenApple.getValue());

                    break;
                }

                case FOOD: {
                    this.isToEat = ISLClass.mc.player.getFoodStats().getFoodLevel() <= settingFood.getValue().intValue();

                    break;
                }
            }
        }
    }

    /**
     * Make player eat.
     */
    public void doEat() {
        // We need verify hand.
        EnumHand hand = this.doAccept(ISLClass.mc.player.getHeldItemOffhand().getItem()) ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND;

        ISLClass.mc.gameSettings.keyBindUseItem.pressed = true;

        // Process right click.
        ISLClass.mc.playerController.processRightClick(ISLClass.mc.player, ISLClass.mc.world, hand);
    }

    public int findFoodSlot() {
        int slot = -1;

        if (this.doAccept(ISLClass.mc.player.getHeldItemOffhand().getItem())) {
            return 40; // 40 is offhand slot!
        }

        for (int i = 0; i < 9; i++) {
            final Item items = ISLClass.mc.player.inventory.getStackInSlot(i).getItem();

            if (doAccept(items)) {
                slot = i;

                break;
            }
        }

        return slot;
    }

    public boolean doAccept(Item item) {
        boolean isAccepted = false;

        if (item == Items.GOLDEN_APPLE && settingOnlyGoldenApple.isEnabled() && settingOnlyGoldenApple.getValue()) {
            isAccepted = true;
        } else if (item instanceof ItemFood && (!settingOnlyGoldenApple.isEnabled() || (settingOnlyGoldenApple.isEnabled() && !settingOnlyGoldenApple.getValue()))) {
            if (item != Items.CHORUS_FRUIT || item != Items.ROTTEN_FLESH || item != Items.POISONOUS_POTATO || item != Items.SPIDER_EYE) {
                isAccepted = true;
            }
        }

        return isAccepted;
    }
}
