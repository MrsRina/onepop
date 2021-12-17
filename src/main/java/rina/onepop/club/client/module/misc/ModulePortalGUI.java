package rina.onepop.club.client.module.misc;

import rina.onepop.club.api.module.Module;
import rina.onepop.club.api.module.impl.ModuleCategory;
import rina.onepop.club.api.module.registry.Registry;
import rina.onepop.club.api.util.client.NullUtil;
import rina.onepop.club.client.event.client.RunTickEvent;
import rina.onepop.club.api.ISLClass;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

/**
 * @author SrRina
 * @since 15/05/2021 at 17:14
 **/
@Registry(name = "Portal GUI", tag = "PortalGUI", description = "Allows open guis while in portal.", category = ModuleCategory.MISC)
public class ModulePortalGUI extends Module {
    @Listener
    public void onTick(RunTickEvent event) {
        if (NullUtil.isPlayerWorld()) {
            return;
        }

        ISLClass.mc.player.inPortal = false;
    }
}
