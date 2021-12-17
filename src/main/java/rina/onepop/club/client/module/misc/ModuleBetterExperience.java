package rina.onepop.club.client.module.misc;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityXPOrb;
import rina.onepop.club.api.module.Module;
import rina.onepop.club.api.module.impl.ModuleCategory;
import rina.onepop.club.api.module.registry.Registry;
import rina.onepop.club.api.setting.value.ValueNumber;
import rina.onepop.club.api.util.client.NullUtil;
import rina.onepop.club.client.event.client.RunTickEvent;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

/**
 * @author SrRina
 * @since 27/06/2021 at 03:21
 **/
@Registry(name = "Better Experience", tag = "BetterExperience", description = "Collect better the experience of world!", category = ModuleCategory.MISC)
public class ModuleBetterExperience extends Module {
    // Misc.
    public static ValueNumber settingRange = new ValueNumber("Range", "Range", "Minimum distance for player get XP.", 2f, 1f, 6f);

    @Listener
    public void onTick(RunTickEvent event) {
        if (NullUtil.isPlayerWorld()) {
            return;
        }

        for (Entity entity : mc.world.loadedEntityList) {
            if (entity instanceof EntityXPOrb && mc.player.getDistance(entity) <= settingRange.getValue().floatValue()) {

            }
        }
    }
}