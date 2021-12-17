package rina.onepop.club.client.module.combat;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import rina.onepop.club.api.module.Module;
import rina.onepop.club.api.module.impl.ModuleCategory;
import rina.onepop.club.api.module.registry.Registry;
import rina.onepop.club.api.setting.value.ValueBoolean;
import rina.onepop.club.api.setting.value.ValueNumber;
import rina.onepop.club.api.util.client.NullUtil;
import rina.onepop.club.client.event.client.RunTickEvent;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.text.TextComponentString;
import rina.onepop.club.api.ISLClass;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

/**
 * @author SrRina
 * @since 19/05/2021 at 00:54
 **/
@Registry(name = "Auto-Log", tag = "AutoLog", description = "Automatically logs on combat...", category = ModuleCategory.COMBAT)
public class ModuleAutoLog extends Module {
    /* Misc. */
    public static ValueBoolean settingPacketKick = new ValueBoolean("Packet Kick", "PacketKick", "Send packet before you log.", false);
    public static ValueBoolean settingCrystals = new ValueBoolean("Crystals", "Crystals", "Kicks if there is crystals close of you!", false);
    public static ValueNumber settingHealth = new ValueNumber("Health", "Health", "The health for log.", 6, 1, 20);

    @Listener
    public void onTick(RunTickEvent event) {
        if (NullUtil.isPlayerWorld()) {
            return;
        }

        boolean flagCrystal = false;

        for (Entity entities : mc.world.loadedEntityList) {
            if (entities instanceof EntityEnderCrystal && mc.player.getDistance(entities) <= 6f) {
                flagCrystal = true;

                break;
            }
        }

        float health = ISLClass.mc.player.getHealth();

        if ((health <= settingHealth.getValue().intValue() && health != 0f && !mc.player.isDead) || flagCrystal) {
            this.doLog();
            this.setDisabled();
        }
    }

    public void doLog() {
        if (settingPacketKick.getValue()) {
            ISLClass.mc.player.connection.sendPacket(new CPacketPlayer.Position(ISLClass.mc.player.posX, ISLClass.mc.player.posY + 50, ISLClass.mc.player.posZ, false));
        }

        ISLClass.mc.player.connection.getNetworkManager().closeChannel(new TextComponentString("Auto Log!"));
    }
}