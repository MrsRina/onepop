package rina.onepop.club.client.module.client.anticheat;

import rina.onepop.club.api.module.Module;
import rina.onepop.club.api.module.impl.ModuleCategory;
import rina.onepop.club.api.module.registry.Registry;
import rina.onepop.club.api.setting.value.ValueBoolean;
import rina.onepop.club.api.setting.value.ValueEnum;
import rina.onepop.club.api.ISLClass;

/**
 * @author smwwrina
 * @since 27/02/2021 at 16:19
 **/
@Registry(name = "Anti-Cheat", tag = "AntiCheat", description = "Enable if you know what anti-cheat is on.", category = ModuleCategory.CLIENT)
public class ModuleAntiCheat extends Module {
    public static ModuleAntiCheat INSTANCE;

    public static ValueBoolean settingNewerVersionAC = new ValueBoolean("Newer 1.13+ Version", "Newer1.13+Version", "Make all client works with new version", false);
    public static ValueEnum settingType = new ValueEnum("Type", "Type", "Type of anti-cheat.", Type.NCP);

    public ModuleAntiCheat() {
        INSTANCE = this;
    }

    public static float getRange() {
        float range = 0f;

        if (!INSTANCE.isEnabled()) {
            range = ISLClass.mc.playerController.getBlockReachDistance();
        }

        if (settingType.getValue() == Type.NCP) {
            range = 4.3f;
        }

        if (settingType.getValue() == Type.VANILLA) {
            range = ISLClass.mc.playerController.getBlockReachDistance();
        }

        return range;
    }
}
