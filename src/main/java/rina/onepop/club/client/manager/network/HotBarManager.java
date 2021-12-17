package rina.onepop.club.client.manager.network;

import rina.onepop.club.api.manager.Manager;
import rina.onepop.club.api.util.client.NullUtil;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import rina.onepop.club.client.event.network.PacketEvent;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

/**
 * @author SrRina
 * @since 14/05/2021 at 16:11
 **/
public class HotBarManager extends Manager {
    public static final int CLIENT = 0;
    public static final int SERVER = 1;

    public static HotBarManager INSTANCE;

    private int currentItemFromClient;
    private int currentItemFromServer;

    public HotBarManager() {
        super("Hot Bar Manager", "Manage hot bar!!");

        INSTANCE = this;
    }

    public int getCurrentItemFromClient() {
        return currentItemFromClient;
    }

    public int getCurrentItemFromServer() {
        return currentItemFromServer;
    }

    public static int currentItem(int protocol) {
        return protocol == CLIENT ? INSTANCE.getCurrentItemFromClient() : INSTANCE.getCurrentItemFromServer();
    }

    @Listener
    public void onPacketSend(PacketEvent.Send event) {
        if (event.getPacket() instanceof CPacketHeldItemChange) {
            final CPacketHeldItemChange packet = (CPacketHeldItemChange) event.getPacket();

            this.currentItemFromServer = packet.getSlotId();
        }
    }

    @Override
    public void onUpdateAll() {
        if (NullUtil.isPlayerWorld()) {
            return;
        }

        this.currentItemFromClient = mc.player.inventory.currentItem;
    }
}
