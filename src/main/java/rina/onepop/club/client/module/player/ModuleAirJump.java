package rina.onepop.club.client.module.player;

import rina.onepop.club.api.module.Module;
import rina.onepop.club.api.module.impl.ModuleCategory;
import rina.onepop.club.api.module.registry.Registry;
import rina.onepop.club.api.setting.value.ValueBoolean;
import rina.onepop.club.api.util.client.NullUtil;
import rina.onepop.club.client.event.client.RunTickEvent;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

/**
 * @author SrRina
 * @since 31/05/2021 at 00:28
 **/
@Registry(name = "Air Jump", tag = "AirJump", description = "Jump at air!", category = ModuleCategory.PLAYER)
public class ModuleAirJump extends Module {
    /* Misc. */
    public static ValueBoolean settingBoost = new ValueBoolean("Boost", "Boost", "Boost jump.", false);

    private boolean pressed;

    @Listener
    public void onTick(RunTickEvent event) {
        if (NullUtil.isPlayerWorld()) {
            return;
        }

        if (mc.gameSettings.keyBindJump.isKeyDown()) {
            if (!this.pressed) {
                mc.player.jump();

                if (!settingBoost.getValue()) {
                    this.pressed = true;
                }
            }
        } else {
            this.pressed = false;
        }
    }
}
