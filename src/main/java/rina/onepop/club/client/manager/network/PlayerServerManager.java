package rina.onepop.club.client.manager.network;

import me.rina.turok.util.TurokTick;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import rina.onepop.club.Onepop;
import rina.onepop.club.api.event.impl.EventStage;
import rina.onepop.club.api.manager.Manager;
import rina.onepop.club.api.util.client.NullUtil;
import rina.onepop.club.client.event.network.StateServerPlayerEvent;

import java.util.ArrayList;

/**
 * @author SrRina
 * @since 19/02/2021 at 11:51
 **/
public class PlayerServerManager extends Manager {
    public static PlayerServerManager INSTANCE;

    private ArrayList<NetworkPlayerInfo> onlineList;
    private String lastServer;

    private final TurokTick cooldownConnection = new TurokTick();

    public PlayerServerManager() {
        super("Player Server", "An manager with all players online on server.");

        INSTANCE = this;

        this.onlineList = new ArrayList<>();
    }

    public void setOnlineList(ArrayList<NetworkPlayerInfo> onlineList) {
        this.onlineList = onlineList;
    }

    public ArrayList<NetworkPlayerInfo> getOnlineList() {
        return onlineList;
    }

    public static NetworkPlayerInfo get(String name) {
        for (NetworkPlayerInfo playersInfo : INSTANCE.getOnlineList()) {
            if (playersInfo == null) {
                continue;
            }

            if (playersInfo.getGameProfile().getName().equalsIgnoreCase(name)) {
                return playersInfo;
            }
        }

        return null;
    }

    public static boolean isOnline() {
        final Minecraft mc = Onepop.getMinecraft();

        return mc.getConnection() != null;
    }

    @Override
    public void onUpdateAll() {
        if ((NullUtil.isWorld() && mc.currentServerData == null) || mc.currentServerData == null || (NullUtil.isPlayer() || mc.getConnection().getPlayerInfoMap() == null)) {
            this.lastServer = null;
            this.cooldownConnection.reset();

            return;
        }

        if (this.cooldownConnection.isPassedMS(2000)) {
            this.lastServer = mc.currentServerData.serverIP;

            for (NetworkPlayerInfo players : mc.getConnection().getPlayerInfoMap()) {
                if (!this.onlineList.contains(players)) {
                    final StateServerPlayerEvent event = new StateServerPlayerEvent(EventStage.PRE, players);

                    Onepop.getPomeloEventManager().dispatchEvent(event);

                    this.onlineList.add(players);
                }
            }

            for (NetworkPlayerInfo players : new ArrayList<>(this.onlineList)) {
                if (!mc.getConnection().getPlayerInfoMap().contains(players) && this.onlineList.contains(players)) {
                    final StateServerPlayerEvent event = new StateServerPlayerEvent(EventStage.POST, players);

                    Onepop.getPomeloEventManager().dispatchEvent(event);

                    this.onlineList.remove(players);
                }
            }
        } else {
            this.onlineList.addAll(mc.getConnection().getPlayerInfoMap());
        }
    }
}
