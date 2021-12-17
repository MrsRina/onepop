package rina.onepop.club.client.module.misc;

import rina.onepop.club.api.module.Module;
import rina.onepop.club.api.module.impl.ModuleCategory;
import rina.onepop.club.api.module.registry.Registry;

/**
 * @author SrRina
 * @since 26/04/2021 at 23:51
 **/
@Registry(name = "Multitask", tag = "Multitask", description = "Magic hands!", category = ModuleCategory.MISC)
public class ModuleMultitask extends Module {
    public static ModuleMultitask INSTANCE;

    public ModuleMultitask() {
        INSTANCE = this;
    }
}