package rina.onepop.club.api.util.client;

import rina.onepop.club.Onepop;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.settings.KeyConflictContext;

/**
 * @author SrRina
 * @since 06/02/2021 at 12:50
 **/
public class KeyUtil {
    public static final KeyBinding[] ALL_MOVEMENT_KEY_BIND = {
            Onepop.MC.gameSettings.keyBindForward, Onepop.MC.gameSettings.keyBindBack, Onepop.MC.gameSettings.keyBindLeft, Onepop.MC.gameSettings.keyBindRight, Onepop.MC.gameSettings.keyBindJump
    };

    public static void press(KeyBinding keyBinding, boolean pressed) {
        if (keyBinding.getKeyConflictContext() != KeyConflictContext.UNIVERSAL) {
            keyBinding.setKeyConflictContext(KeyConflictContext.UNIVERSAL);
        }

        KeyBinding.setKeyBindState(keyBinding.getKeyCode(), pressed);
    }

    public static boolean isMoving() {
        if (NullUtil.isPlayerWorld()) {
            return false;
        }

        return (Onepop.MC.gameSettings.keyBindForward.isKeyDown() || Onepop.MC.gameSettings.keyBindBack.isKeyDown() || Onepop.MC.gameSettings.keyBindLeft.isKeyDown() || Onepop.MC.gameSettings.keyBindRight.isKeyDown());
    }

    public static boolean isJumping() {
        return Onepop.MC.gameSettings.keyBindJump.isKeyDown();
    }

    public static boolean isPressed(KeyBinding keyBinding) {
        return keyBinding.isKeyDown();
    }
}