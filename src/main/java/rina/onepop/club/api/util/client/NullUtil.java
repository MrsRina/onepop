package rina.onepop.club.api.util.client;

import rina.onepop.club.Onepop;

/**
 * @author SrRina
 * @since 02/02/2021 at 00:01
 **/
public class NullUtil {
    public static boolean isPlayerWorld() {
        return Onepop.MC.player == null && Onepop.MC.world == null;
    }

    public static boolean isWorld() {
        return Onepop.MC.world == null;
    }

    public static boolean isPlayer() {
        return Onepop.MC.player == null;
    }

    public static boolean isGUI() {
        return Onepop.MC.currentScreen == null;
    }
}
