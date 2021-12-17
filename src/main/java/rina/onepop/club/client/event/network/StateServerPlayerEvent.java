package rina.onepop.club.client.event.network;

import net.minecraft.client.network.NetworkPlayerInfo;
import rina.onepop.club.api.event.Event;
import rina.onepop.club.api.event.impl.EventStage;

/**
 * @author SrRina
 * @since 13/07/2021 at 01:42
 **/
public class StateServerPlayerEvent extends Event {
    private final NetworkPlayerInfo player;

    public StateServerPlayerEvent(EventStage stage, NetworkPlayerInfo player) {
        super(stage);

        this.player = player;
    }

    public NetworkPlayerInfo getPlayer() {
        return player;
    }
}
