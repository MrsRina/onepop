package rina.onepop.club.client.module.player;

import rina.onepop.club.api.module.Module;
import rina.onepop.club.api.module.impl.ModuleCategory;
import rina.onepop.club.api.module.registry.Registry;
import rina.onepop.club.api.setting.value.ValueBoolean;
import rina.onepop.club.api.util.client.NullUtil;
import rina.onepop.club.client.event.entity.PushPlayerEvent;
import rina.onepop.club.client.event.entity.PushWatterEvent;
import rina.onepop.club.client.event.network.PacketEvent;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.network.play.server.SPacketExplosion;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

/**
 * @author SrRina
 * @since 16/02/2021 at 11:01
 **/
@Registry(name = "Velocity", tag = "Velocity", description = "No kinetic force for Minecraft.", category = ModuleCategory.PLAYER)
public class ModuleVelocity extends Module {
    /* Misc. */
    public static ValueBoolean settingCancelExplosion = new ValueBoolean("Cancel Explosion", "CancelExplosion", "Client cancel explosion packet event.", true);
    public static ValueBoolean settingCancelVelocity = new ValueBoolean("Cancel Velocity", "CancelVelocity", "Cancels velocity hits.", true);
    public static ValueBoolean settingLiquid = new ValueBoolean("Liquid", "Liquid", "No liquid push.", true);
    public static ValueBoolean settingPush = new ValueBoolean("Push", "Push", "No push player.", true);

    @Listener
    public void onListen(PacketEvent.Receive event) {
        if (NullUtil.isPlayerWorld()) {
            return;
        }

        if (event.getPacket() instanceof SPacketExplosion && settingCancelExplosion.getValue()) {
            event.setCanceled(true);
        }

        if (event.getPacket() instanceof SPacketEntityVelocity && ((SPacketEntityVelocity) event.getPacket()).entityID == mc.player.getEntityId() && settingCancelVelocity.getValue()) {
            event.setCanceled(true);
        }
    }

    @Listener
    public void onWatter(PushWatterEvent event) {
        if (settingLiquid.getValue()) {
            event.setCanceled(true);
        }
    }

    @Listener
    public void onPush(PushPlayerEvent event) {
        if (settingPush.getValue()) {
            event.setCanceled(true);
        }
    }
}
