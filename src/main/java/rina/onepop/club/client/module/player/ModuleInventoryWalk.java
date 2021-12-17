package rina.onepop.club.client.module.player;

import rina.onepop.club.api.ISLClass;
import rina.onepop.club.api.module.Module;
import rina.onepop.club.api.module.impl.ModuleCategory;
import rina.onepop.club.api.module.registry.Registry;
import rina.onepop.club.api.util.client.KeyUtil;
import rina.onepop.club.api.util.client.NullUtil;
import rina.onepop.club.client.event.client.ClientTickEvent;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

/**
 * @author SrRina
 * @since 10/02/2021 at 12:36
 **/
@Registry(name = "Inventory Walk", tag = "InventoryWalk", description = "You can move while a GUI is open.", category = ModuleCategory.PLAYER)
public class ModuleInventoryWalk extends Module {
    @Listener
    public void onListen(ClientTickEvent event) {
        if (NullUtil.isPlayer()) {
            return;
        }

        if (ISLClass.mc.currentScreen instanceof GuiChat || ISLClass.mc.currentScreen == null) {
            return;
        }

        for (KeyBinding keys : KeyUtil.ALL_MOVEMENT_KEY_BIND) {
            KeyUtil.press(keys, Keyboard.isKeyDown(keys.getKeyCode()));
        }
    }
}
