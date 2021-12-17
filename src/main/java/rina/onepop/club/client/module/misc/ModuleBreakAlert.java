package rina.onepop.club.client.module.misc;

import com.mojang.realmsclient.gui.ChatFormatting;
import rina.onepop.club.api.module.Module;
import rina.onepop.club.api.module.impl.ModuleCategory;
import rina.onepop.club.api.module.registry.Registry;
import rina.onepop.club.api.setting.value.ValueString;
import rina.onepop.club.api.util.client.NullUtil;
import rina.onepop.club.api.util.entity.PlayerUtil;
import rina.onepop.club.api.util.math.PositionUtil;
import rina.onepop.club.api.util.world.BlockUtil;
import rina.onepop.club.api.util.world.BlocksUtil;
import rina.onepop.club.client.event.client.RunTickEvent;
import rina.onepop.club.client.event.network.PacketEvent;
import net.minecraft.network.play.server.SPacketBlockBreakAnim;
import net.minecraft.util.math.BlockPos;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

import java.util.ArrayList;
import java.util.List;

/**
 * @author SrRina
 * @since 15/05/2021 at 17:19
 **/
@Registry(name = "Break Alert", tag = "Break Alert", description = "Alerts if a nn fag is breaking your surround.", category = ModuleCategory.MISC)
public class ModuleBreakAlert extends Module {
    /* Misc. */
    public static ValueString settingAlert = new ValueString("Alert", "Alert", "Text alert.", "Someone is breaking your surround.");

    private final List<BlockPos> confirmBreak = new ArrayList<>();

    @Listener
    public void onReceivePacket(PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketBlockBreakAnim) {
            final SPacketBlockBreakAnim packet = (SPacketBlockBreakAnim) event.getPacket();

            if (BlockUtil.isAir(packet.getPosition())) {
                this.confirmBreak.remove(packet.getPosition());

                return;
            }

            if (!this.confirmBreak.contains(packet.getPosition()) && this.isBreakingSurround(packet.getPosition())) {
                this.print(ChatFormatting.YELLOW + settingAlert.getValue());
                this.confirmBreak.add(packet.getPosition());
            }
        }
    }

    @Listener
    public void onTick(RunTickEvent event) {
        if (NullUtil.isPlayerWorld()) {
            return;
        }

        this.confirmBreak.removeIf(alertBlocksPosition -> BlockUtil.isAir(alertBlocksPosition) || !this.isBreakingSurround(alertBlocksPosition));
    }

    public boolean isBreakingSurround(final BlockPos position) {
        final BlockPos selfPosition = PlayerUtil.getBlockPos();

        for (BlockPos adds : BlocksUtil.SURROUND) {
            final BlockPos added = selfPosition.add(adds);

            if (BlockUtil.isAir(added)) {
                continue;
            }

            if (PositionUtil.collideBlockPos(added, position)) {
                return true;
            }
        }

        return false;
    }
}
