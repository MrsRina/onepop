package rina.onepop.club.client.module.player;

import rina.onepop.club.api.module.Module;
import rina.onepop.club.api.module.impl.ModuleCategory;
import rina.onepop.club.api.module.registry.Registry;
import rina.onepop.club.api.setting.value.ValueBoolean;
import rina.onepop.club.api.util.client.NullUtil;
import rina.onepop.club.client.event.client.ClientTickEvent;
import rina.onepop.club.api.ISLClass;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

/**
 * @author SrRina
 * @since 13/04/2021 at 20:48
 **/
@Registry(name = "Sprint", tag = "Sprint", description = "Automatically sprints player.", category = ModuleCategory.PLAYER)
public class ModuleSprint extends Module {
    /* Misc. */
    public static ValueBoolean settingAlways = new ValueBoolean("Always", "Always", "Set every tick sprint to true!", false);

    @Listener
    public void onTick(ClientTickEvent event) {
        if (NullUtil.isPlayerWorld()) {
            return;
        }

        boolean flag = settingAlways.getValue();

        if ((ISLClass.mc.player.movementInput.moveForward != 0 || ISLClass.mc.player.movementInput.moveStrafe != 0) && !settingAlways.getValue()) {
            flag = true;
        }

        ISLClass.mc.player.setSprinting(flag);
    }
}
