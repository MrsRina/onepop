package rina.onepop.club.client.module.render.fullbright;

import rina.onepop.club.api.ISLClass;
import rina.onepop.club.api.module.Module;
import rina.onepop.club.api.module.impl.ModuleCategory;
import rina.onepop.club.api.module.registry.Registry;
import rina.onepop.club.api.setting.value.ValueEnum;
import rina.onepop.club.api.util.client.NullUtil;
import rina.onepop.club.client.event.client.RunTickEvent;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

/**
 * Jake! not make me insane! don't put 1k in gamma!!! it glitch all game light!!!!
 * lol
 * sorry rina -jake
 */
@Registry(name = "Full Bright", tag = "FullBright", description = "Changes brightness level of Minecraft.", category = ModuleCategory.RENDER)
public class ModuleFullBright extends Module {
    /* Misc. */
    public static ValueEnum settingMode = new ValueEnum("Mode", "Mode", "Modes for full bright!", Mode.POTION);

    @Listener
    public void onRunTick(RunTickEvent event) {
        if (NullUtil.isPlayerWorld()) {
            return;
        }

        mc.gameSettings.gammaSetting = 100f;

        if (settingMode.getValue() == Mode.POTION) {
            mc.player.addPotionEffect(new PotionEffect(MobEffects.NIGHT_VISION, 5210));
        }
    }

    @Override
    public void onEnable() {
        ISLClass.mc.gameSettings.gammaSetting = 100f;
    }

    @Override
    public void onDisable() {
        mc.gameSettings.gammaSetting = 1.0f;
    }
}