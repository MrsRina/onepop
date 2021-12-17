package rina.onepop.club.client.module.client;

import rina.onepop.club.api.module.Module;
import rina.onepop.club.api.module.impl.ModuleCategory;
import rina.onepop.club.api.module.registry.Registry;
import rina.onepop.club.client.event.network.PacketEvent;

/**
 * @author SrRina
 * @since 24/02/2021 at 12:15
 **/
@Registry(name = "TPS Sync", tag = "TPSSync", description = "Sync client actions with TPS.", category = ModuleCategory.CLIENT)
public class ModuleTPSSync extends Module {
    public static ModuleTPSSync INSTANCE;

    public ModuleTPSSync() {
        INSTANCE = this;
    }
}