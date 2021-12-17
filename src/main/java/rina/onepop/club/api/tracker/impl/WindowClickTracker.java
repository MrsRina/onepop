package rina.onepop.club.api.tracker.impl;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketClickWindow;
import rina.onepop.club.api.tracker.Tracker;

/**
 * @author SrRina
 * @since 30/06/2021 at 04:23
 **/
public class WindowClickTracker extends Tracker {
    private int windowId;
    private int slotId;
    private int mouseButton;

    private ClickType type;
    private EntityPlayer player;

    public WindowClickTracker(int windowId, int slotId, int mouseButton, ClickType type, EntityPlayer player) {
        super("Window Click", null);

        this.windowId = windowId;
        this.slotId = slotId;
        this.mouseButton = mouseButton;
        this.type = type;
        this.player = player;
    }

    @Override
    public void onPre() {
        short short1 = player.openContainer.getNextTransactionID(player.inventory);
        ItemStack itemstack = player.openContainer.slotClick(slotId, mouseButton, type, player);

        this.setPacket(new CPacketClickWindow(windowId, slotId, mouseButton, type, itemstack, short1));
    }
}
