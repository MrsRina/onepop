package rina.onepop.club.client.module.misc;

import rina.onepop.club.Onepop;
import rina.onepop.club.api.ISLClass;
import rina.onepop.club.api.module.Module;
import rina.onepop.club.api.module.impl.ModuleCategory;
import rina.onepop.club.api.module.registry.Registry;
import rina.onepop.club.api.setting.value.ValueBoolean;
import rina.onepop.club.api.util.client.KeyUtil;
import rina.onepop.club.api.util.client.NullUtil;
import rina.onepop.club.api.util.entity.PlayerUtil;
import me.rina.turok.util.TurokMath;
import me.rina.turok.util.TurokTick;
import rina.onepop.club.client.event.client.ClientTickEvent;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;
import net.minecraft.util.EnumHand;

/**
 * @author SrRina
 * @since 07/02/2021 at 15:57
 **/
@Registry(name = "Anti-AFK", tag = "AntiAFK", description = "Make you no get kicked by server.", category = ModuleCategory.MISC)
public class ModuleAntiAFK extends Module {
    public static ValueBoolean settingRotate = new ValueBoolean("Rotate", "Rotate", "Rotate camera.", true);

    private TurokTick tick = new TurokTick();
    private float angle;

    @Override
    public void onShutdown() {
        this.setDisabled();
    }

    @Listener
    public void onListen(ClientTickEvent event) {
        if (NullUtil.isPlayerWorld()) {
            return;
        }

        float ms = 3500f;

        if (tick.isPassedMS(ms / 2)) {
            this.angle = TurokMath.serp(this.angle, 0, Onepop.getClientEventManager().getCurrentRender3DPartialTicks());
        
            // We send swing to...
            ISLClass.mc.player.swingArm(EnumHand.MAIN_HAND);
        } else {
            this.angle = TurokMath.serp(this.angle, 90.0f, Onepop.getClientEventManager().getCurrentRender3DPartialTicks());
        }

        // I don't understand.
        if (tick.isPassedMS(ms)) {
            tick.reset();
        }

        if (ISLClass.mc.player.onGround) {
            KeyUtil.press(ISLClass.mc.gameSettings.keyBindJump, true);
        } else {
            KeyUtil.press(ISLClass.mc.gameSettings.keyBindJump, false);
        }

        // Real pitch.
        if (settingRotate.getValue()) {
            PlayerUtil.setPitch(this.angle);
        }
    }
}
