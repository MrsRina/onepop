package rina.onepop.club.client.module.misc;

import rina.onepop.club.api.module.Module;
import rina.onepop.club.api.module.impl.ModuleCategory;
import rina.onepop.club.api.module.registry.Registry;
import rina.onepop.club.api.util.client.KeyUtil;
import rina.onepop.club.api.util.client.NullUtil;
import rina.onepop.club.client.event.client.RunTickEvent;
import net.minecraft.item.ItemPickaxe;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

/**
 * @author SrRina
 * @since 15/05/2021 at 17:11
 **/
@Registry(name = "Auto-Mine", tag = "AutoMine", description = "Auto clicks mouse for mine.", category = ModuleCategory.MISC)
public class ModuleAutoMine extends Module {
    @Override
    public void onDisable() {
        KeyUtil.press(mc.gameSettings.keyBindAttack, false);
    }

    @Listener
    public void onTick(RunTickEvent event) {
        if (NullUtil.isPlayerWorld()) {
            return;
        }

        if (mc.player.getHeldItemMainhand().getItem() instanceof ItemPickaxe) {
            KeyUtil.press(mc.gameSettings.keyBindAttack, true);
            mc.gameSettings.keyBindAttack.pressed = true;
        } else {
            KeyUtil.press(mc.gameSettings.keyBindAttack, false);
            mc.gameSettings.keyBindAttack.pressed = false;
        }
    }
}
