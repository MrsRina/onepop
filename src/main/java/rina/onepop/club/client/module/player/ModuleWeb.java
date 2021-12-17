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
 * @since 31/05/2021 at 00:18
 **/
@Registry(name = "Web", tag = "Web", description = "Special module for webs!", category = ModuleCategory.PLAYER)
public class ModuleWeb extends Module {
    /* Misc. */
    public static ValueBoolean settingSideIgnore = new ValueBoolean("Side Ignore", "SideIgnore", "Side ignore webs.", true);

    @Listener
    public void onTick(RunTickEvent event) {
        if (NullUtil.isPlayerWorld()) {
            return;
        }

        if (settingSideIgnore.getValue()) {
            mc.player.isInWeb = false;
        } else {
            if (mc.player.isInWeb) {
                mc.player.motionY += 0.1f;
            }
        }
    }
}
