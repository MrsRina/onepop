package rina.onepop.club.client.module.misc;

import rina.onepop.club.api.module.Module;
import rina.onepop.club.api.module.impl.ModuleCategory;
import rina.onepop.club.api.module.registry.Registry;
import rina.onepop.club.api.setting.value.ValueBoolean;
import rina.onepop.club.api.setting.value.ValueNumber;
import rina.onepop.club.api.util.client.NullUtil;
import rina.onepop.club.client.event.client.RunTickEvent;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

/**
 * @author SrRina
 * @since 19/04/2021 at 18:42
 **/
@Registry(name = "Timer", tag = "Timer", description = "Change all ticks in your Minecraft!", category = ModuleCategory.MISC)
public class ModuleTimer extends Module {
    /* Misc. */
    public static ValueNumber settingValue = new ValueNumber("Value", "Value", "Sets custom timer value.", 2f, 0.1f, 30f);
    public static ValueBoolean settingDisableGUI = new ValueBoolean("Disable in GUI", "DisableInGUI", "Disable timer when any GUIs is open!",true);

    @Override
    public void onShutdown() {
        this.setDisabled();
    }

    @Override
    public void onDisable() {
        this.disableTimer();
    }

    @Listener
    public void onTick(RunTickEvent tick) {
        if (NullUtil.isPlayerWorld()) {
            return;
        }

        this.updateTimer();
    }

    protected void updateTimer() {
        if (settingDisableGUI.getValue() && mc.currentScreen != null) {
            this.disableTimer();
        } else {
            mc.timer.tickLength = (50f / settingValue.getValue().floatValue());
        }
    }

    public void disableTimer() {
        mc.timer.tickLength = 50f;
    }
}