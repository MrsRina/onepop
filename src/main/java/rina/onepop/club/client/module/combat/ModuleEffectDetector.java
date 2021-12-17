package rina.onepop.club.client.module.combat;

import rina.onepop.club.api.module.Module;
import rina.onepop.club.api.module.impl.ModuleCategory;
import rina.onepop.club.api.module.registry.Registry;
import rina.onepop.club.api.util.client.NullUtil;
import rina.onepop.club.client.event.client.RunTickEvent;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

import java.util.ArrayList;
import java.util.List;

/**
 * @author SrRina
 * @since 02/06/2021 at 02:12
 **/
@Registry(name = "Effect Detector", tag = "EffectDetector", description = "Detects effects from players close of you!", category = ModuleCategory.COMBAT)
public class ModuleEffectDetector extends Module {
    public static class PlayerEffect {}

    private final List<PlayerEffect> currentPlayerList = new ArrayList<>();

    @Listener
    public void onTick(RunTickEvent event) {
        if (NullUtil.isPlayerWorld()) {
            return;
        }
    }
}