package rina.onepop.club.api.util.network;

import net.minecraft.client.network.NetworkPlayerInfo;

/**
 * @author SrRina
 * @since 23/02/2021 at 20:17
 **/
public class ServerUtil {
    public static int getPing(NetworkPlayerInfo player) {
        boolean flag = player.getResponseTime() < 0;

        return flag ? -1 : player.getResponseTime();
    }
}
