package rina.onepop.club.client.module.combat.critical;

import rina.onepop.club.api.ISLClass;
import rina.onepop.club.api.module.Module;
import rina.onepop.club.api.module.impl.ModuleCategory;
import rina.onepop.club.api.module.registry.Registry;
import rina.onepop.club.api.setting.value.ValueEnum;
import rina.onepop.club.api.util.client.KeyUtil;
import rina.onepop.club.api.util.entity.PlayerUtil;
import rina.onepop.club.api.util.network.PacketUtil;
import rina.onepop.club.client.event.network.PacketEvent;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketUseEntity;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

/**
 * @author SrRina
 * @since 23/02/2021 at 00:21
 **/
@Registry(name = "Critical", tag = "Critical", description = "Critical hits.", category = ModuleCategory.COMBAT)
public class ModuleCritical extends Module {
    /* Misc. */
    public static ValueEnum settingMode = new ValueEnum("Mode", "Mode", "Critical action mode.", Mode.PACKET);

    @Listener
    public void onListen(PacketEvent.Send event) {
        if (!(event.getPacket() instanceof CPacketUseEntity)) {
            return;
        }

        CPacketUseEntity packet = (CPacketUseEntity) event.getPacket();

        boolean flag = ISLClass.mc.player.onGround && packet.getAction() == CPacketUseEntity.Action.ATTACK && packet.getEntityFromWorld(ISLClass.mc.world) instanceof EntityLivingBase;

        if (flag && !KeyUtil.isPressed(ISLClass.mc.gameSettings.keyBindJump)) {
            switch ((Mode) settingMode.getValue()) {
                case JUMP: {
                    mc.player.jump();

                    break;
                }

                case PACKET: {
                    PacketUtil.send(new CPacketPlayer.Position(PlayerUtil.getPos()[0], PlayerUtil.getPos()[1] + 0.1f, PlayerUtil.getPos()[2], false));
                    PacketUtil.send(new CPacketPlayer.Position(PlayerUtil.getPos()[0], PlayerUtil.getPos()[1], PlayerUtil.getPos()[2], false));

                    break;
                }

                case LOWHOP: {
                    mc.player.jump();
                    mc.player.motionY /= 2f;

                    break;
                }
            }
        }
    }
}
