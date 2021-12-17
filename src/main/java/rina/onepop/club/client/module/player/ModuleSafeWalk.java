package rina.onepop.club.client.module.player;

import rina.onepop.club.api.module.Module;
import rina.onepop.club.api.module.impl.ModuleCategory;
import rina.onepop.club.api.module.registry.Registry;
import rina.onepop.club.api.util.client.NullUtil;
import rina.onepop.club.client.event.client.RunTickEvent;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

/**
 * @author SrRina
 * @since 19/05/2021 at 13:19
 **/
@Registry(name = "Safe Walk", tag = "SafeWalk", description = "Sneak on normal speed.", category = ModuleCategory.PLAYER)
public class ModuleSafeWalk extends Module {
    public static ModuleSafeWalk INSTANCE;

    public ModuleSafeWalk() {
        INSTANCE = this;
    }

    @Listener
    public void onTick(RunTickEvent event) {
        if (NullUtil.isPlayerWorld()) {
            return;
        }
    }
}
