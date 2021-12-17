package rina.onepop.club.client.module.client;

import rina.onepop.club.Onepop;
import rina.onepop.club.api.module.Module;
import rina.onepop.club.api.module.impl.ModuleCategory;
import rina.onepop.club.api.module.registry.Registry;
import rina.onepop.club.api.setting.value.ValueBoolean;

/**
 * @author Glauco
 */
@Registry(name = "RPC", tag = "RPC", description = "The cool RPC.", category = ModuleCategory.CLIENT)
public class ModuleRPC extends Module {
    public static ModuleRPC INSTANCE;

    /* Misc. */
    public static ValueBoolean showName = new ValueBoolean("Show Name", "RPCShowName", "shows your name in the rpc", true);

    public ModuleRPC() {
        INSTANCE = this;
    }

    @Override
    public void onEnable() {
        Onepop.getRPC().run();
    }

    @Override
    public void onDisable() {
        Onepop.getRPC().stop();
    }
}
