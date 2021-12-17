package rina.onepop.club.client.module.player;

import rina.onepop.club.api.module.Module;
import rina.onepop.club.api.module.impl.ModuleCategory;
import rina.onepop.club.api.module.registry.Registry;
import rina.onepop.club.api.util.client.NullUtil;
import rina.onepop.club.client.event.client.RunTickEvent;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

/**
 * @author SrRina
 * @since 15/05/2021 at 17:15
 **/
@Registry(name = "Jesus", tag = "Jesus", description = "Make you walks on water/lava. Deprecated...", category = ModuleCategory.PLAYER)
public class ModuleJesus extends Module {
    @Listener
    public void onTick(RunTickEvent event) {
        if (NullUtil.isPlayerWorld()) {
            return;
        }

        if (mc.player.isInLava() || mc.player.isInWater()) {
            mc.player.motionY += 0.1;
        }
    }
}
