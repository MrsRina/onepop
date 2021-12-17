package rina.onepop.club.client.module.misc;

import rina.onepop.club.api.module.Module;
import rina.onepop.club.api.module.impl.ModuleCategory;
import rina.onepop.club.api.module.registry.Registry;
import rina.onepop.club.api.setting.value.ValueBoolean;
import rina.onepop.club.api.util.client.NullUtil;
import net.minecraft.item.ItemPickaxe;

/**
 * @author SrRina
 * @since 29/04/2021 at 00:17
 **/
@Registry(name = "No Entity Trace", tag = "NoEntityTrace", description = "No entity trace when mining!", category = ModuleCategory.MISC)
public class ModuleNoEntityTrace extends Module {
    public static ModuleNoEntityTrace INSTANCE;

    /* Misc. */
    public static ValueBoolean settingOnlyPickaxe = new ValueBoolean("Only Pickaxe", "OnlyPickaxe", "Only pickaxe.", false);

    public ModuleNoEntityTrace() {
        INSTANCE = this;
    }

    public boolean doAccept() {
        if (NullUtil.isPlayerWorld()) {
            return false;
        }

        return !settingOnlyPickaxe.getValue() || mc.player.getHeldItemMainhand().getItem() instanceof ItemPickaxe;
    }
}