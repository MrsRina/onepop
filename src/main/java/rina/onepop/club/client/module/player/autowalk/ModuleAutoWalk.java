package rina.onepop.club.client.module.player.autowalk;

import rina.onepop.club.api.ISLClass;
import rina.onepop.club.api.module.Module;
import rina.onepop.club.api.module.impl.ModuleCategory;
import rina.onepop.club.api.module.registry.Registry;
import rina.onepop.club.api.setting.value.ValueEnum;
import rina.onepop.club.api.util.client.KeyUtil;
import rina.onepop.club.client.event.client.ClientTickEvent;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

/**
 * Created by Jake!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
 * 
 * Update by Rina at 07/02/21 at 21:03.
 */
@Registry(name = "Auto-Walk", tag = "AutoWalk", description = "Automatically walks.", category = ModuleCategory.PLAYER)
public class ModuleAutoWalk extends Module {
    public static ValueEnum settingDirection = new ValueEnum("Direction", "Direction", "The direction of walk.", Direction.BACK);

    @Listener
    public void onUpdate(ClientTickEvent event) {
        switch ((Direction) settingDirection.getValue()) {
            case FORWARD: {
                KeyUtil.press(ISLClass.mc.gameSettings.keyBindForward, true);

                break;
            }

            case BACK: {
                KeyUtil.press(ISLClass.mc.gameSettings.keyBindBack, true);
                break;
            }

            case LEFT: {
                KeyUtil.press(ISLClass.mc.gameSettings.keyBindLeft, true);
                break;
            }

            case RIGHT: {
                KeyUtil.press(ISLClass.mc.gameSettings.keyBindRight, true);
                break;
            }
        }
    }

    @Override
    public void onDisable() {
        KeyUtil.press(ISLClass.mc.gameSettings.keyBindForward, false);
    }
}