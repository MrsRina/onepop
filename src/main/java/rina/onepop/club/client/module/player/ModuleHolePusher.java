package rina.onepop.club.client.module.player;

import me.rina.turok.util.TurokTick;
import net.minecraft.network.play.client.CPacketInput;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.BlockPos;
import rina.onepop.club.Onepop;
import rina.onepop.club.api.module.Module;
import rina.onepop.club.api.module.impl.ModuleCategory;
import rina.onepop.club.api.module.registry.Registry;
import rina.onepop.club.api.setting.value.ValueNumber;
import rina.onepop.club.api.util.client.NullUtil;
import rina.onepop.club.client.event.client.RunTickEvent;
import rina.onepop.club.client.event.network.PacketEvent;
import rina.onepop.club.client.manager.world.BlockManager;
import rina.onepop.club.client.manager.world.HoleManager;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

/**
 * @author SrRina
 * @since 05/07/2021 at 02:11
 **/
@Registry(name = "Hole Pusher", tag = "HolePusher", description = "Get pushed by a close hole.", category = ModuleCategory.PLAYER)
public class ModuleHolePusher extends Module {
    public static ValueNumber settingDelay = new ValueNumber("Delay", "Delay", "The delay for rollback.", 2000, 0, 3000);

    private BlockPos hole;
    private boolean unsetAndCancel;

    private final TurokTick theCooldown = new TurokTick();

    @Listener
    public void onPacketSend(PacketEvent.Send event) {
        if ((event.getPacket() instanceof CPacketPlayer || event.getPacket() instanceof CPacketInput) && this.unsetAndCancel && hole != null && !this.theCooldown.isPassedMS(settingDelay.getValue().floatValue())) {
            event.setCanceled(true);

            mc.player.setPosition(hole.x + 0.5f, hole.y + 1f, hole.z + 0.5f);
        }
    }

    @Listener
    public void onRunTick(RunTickEvent event) {
        if (NullUtil.isPlayerWorld()) {
            return;
        }

        float bestDistance = 1f;
        HoleManager.Hole bestHole = null;

        if (!this.unsetAndCancel) {
            this.theCooldown.reset();
        } else {
            if (this.theCooldown.isPassedMS(settingDelay.getValue().floatValue())) {
                this.unsetAndCancel = false;
            }
        }

        for (HoleManager.Hole holes : Onepop.getHoleManager().getHoleList()) {
            final BlockPos position = holes.getPosition();
            final float distance = (float) mc.player.getDistance(position.x, position.y, position.z);

            if (distance < bestDistance) {
                bestHole = holes;
                bestDistance = distance;
            }
        }

        if (bestHole != null && mc.player.onGround && !BlockManager.getAirSurroundPlayer().isEmpty() && !this.unsetAndCancel) {
            this.hole = bestHole.getPosition();
            this.unsetAndCancel = true;
        }
    }
}
