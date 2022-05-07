package rina.onepop.club.client.module.misc;

import me.rina.turok.util.TurokTick;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketClickWindow;
import rina.onepop.club.Onepop;
import rina.onepop.club.api.ISLClass;
import rina.onepop.club.api.module.Module;
import rina.onepop.club.api.module.impl.ModuleCategory;
import rina.onepop.club.api.module.registry.Registry;
import rina.onepop.club.api.setting.value.ValueNumber;
import rina.onepop.club.api.tracker.impl.WindowClickTracker;
import rina.onepop.club.api.util.client.NullUtil;
import rina.onepop.club.api.util.item.SlotUtil;
import rina.onepop.club.client.event.client.ClientTickEvent;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author SrRina
 * @since 15/05/2021 at 17:14
 *
 * Lol, is 04:15 am, now I will go to sleep! lol
 **/
@Registry(name = "Auto-Refill", tag = "AutoRefill", description = "Automatically fills hot bar items!", category = ModuleCategory.MISC)
public class ModuleAutoRefill extends Module {
    // Misc.
    public static ValueNumber settingMinimumStackSize = new ValueNumber("Minimum Stack Size", "MinimumStackSize", "Value for refill!", 32, 1, 63);
    public static ValueNumber settingRefillTime = new ValueNumber("Refill Time", "RefillTime", "Reset fill verification.", 100, 50, 250);

    private final Set<Integer> blackListFill = new HashSet<>();

    private final TurokTick refillStamp = new TurokTick();
    private final TurokTick refillDelay = new TurokTick();
    
    private int slotToRefill = -1;

    @Override
    public void onEnable() {
    }

    @Override
    public void onDisable() {
        this.blackListFill.clear();
    }

    @Listener
    public void onTick(ClientTickEvent event) {
        if (NullUtil.isPlayerWorld()) {
            return;
        }

        if (mc.currentScreen != null && !(mc.currentScreen instanceof GuiChat)) {
            return;
        }

        if (this.refillStamp.isPassedMS(settingRefillTime.getValue().intValue() * 100)) {
            this.blackListFill.clear();
            this.refillStamp.reset();
        }

        if (this.slotToRefill == -1) {
            for (int i = 0; i < 9; i++) {
                final ItemStack itemStack = SlotUtil.getItemStack(i);

                if (itemStack.getItem() != Items.AIR && itemStack.getMaxStackSize() > 1 && itemStack.stackSize <= settingMinimumStackSize.getValue().intValue() && !this.blackListFill.contains(i)) {
                    this.slotToRefill = i;

                    break;
                }
            }
        } else {
            final ItemStack itemStack = SlotUtil.getItemStack(this.slotToRefill);
            final HashMap<Integer, Integer> map = SlotUtil.mapItemCountWithSize(itemStack.getItem());

            if (map.size() == 0) {
                this.blackListFill();

                return;
            }

            int size = itemStack.stackSize;

            if (size >= 64) {
                this.doReset();

                return;
            }

            for (Map.Entry<Integer, Integer> maps : map.entrySet()) {
                int slot = maps.getKey();
                int stackSize = maps.getValue();

                this.doFill(slot);

                size += stackSize;
            }
        }
    }

    public void doReset() {
        this.slotToRefill = -1;
    }

    public void doFill(int slot) {
        if (slot == -1) {
            return;
        }

        mc.playerController.windowClick(0, slot, 0, ClickType.PICKUP, ISLClass.mc.player);
        mc.playerController.windowClick(0, 36 + slot, 0, ClickType.PICKUP, ISLClass.mc.player);
        mc.playerController.windowClick(0, slot, 0, ClickType.PICKUP, ISLClass.mc.player);
    }

    public void blackListFill() {
        if (this.slotToRefill == -1) {
            return;
        }

        this.blackListFill.add(this.slotToRefill);
        this.slotToRefill = -1;
    }
}
