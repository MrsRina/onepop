package rina.onepop.club.client.module.player;

import rina.onepop.club.api.module.Module;
import rina.onepop.club.api.module.impl.ModuleCategory;
import rina.onepop.club.api.module.registry.Registry;
import rina.onepop.club.api.util.client.NullUtil;
import rina.onepop.club.client.event.client.RunTickEvent;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

/**
 * @author SrRina
 * @since 15/05/2021 at 17:21
 **/
@Registry(name = "Creative Fly", tag = "CreativeFly", description = "Forces you creative fly.", category = ModuleCategory.PLAYER)
public class ModuleCreativeFly extends Module {
    @Override
    public void onDisable() {
        if (NullUtil.isPlayerWorld()) {
            return;
        }

        mc.player.capabilities.isFlying = false;
    }

    @Listener
    public void onTick(RunTickEvent event) {
        if (NullUtil.isPlayerWorld()) {
            return;
        }

        mc.player.capabilities.isFlying = true;
    }
}
