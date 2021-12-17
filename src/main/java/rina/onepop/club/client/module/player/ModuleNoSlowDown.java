package rina.onepop.club.client.module.player;

import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraftforge.client.event.InputUpdateEvent;
import rina.onepop.club.api.module.Module;
import rina.onepop.club.api.module.impl.ModuleCategory;
import rina.onepop.club.api.module.registry.Registry;
import rina.onepop.club.api.setting.value.ValueBoolean;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

/**
 * @author SrRina
 * @since 24/02/2021 at 16:01
 **/
@Registry(name = "No Slow Down", tag = "NoSlowDown", description = "No slow down module.", category = ModuleCategory.PLAYER)
public class ModuleNoSlowDown extends Module {
    public static ValueBoolean settingStrict = new ValueBoolean("Strict", "Strict", "Strict option.", false);

    @Listener
    public void onInputUpdateEvent(InputUpdateEvent event) {
        if (mc.player.isHandActive() && !mc.player.isRiding()) {
            if (settingStrict.getValue()) {
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
            } else {
                event.getMovementInput().moveStrafe *= 5;
                event.getMovementInput().moveForward *= 5;
            }
        }
    }
}
