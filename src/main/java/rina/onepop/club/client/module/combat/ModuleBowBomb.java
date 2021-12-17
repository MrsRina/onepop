package rina.onepop.club.client.module.combat;

import me.rina.turok.util.TurokTick;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumHand;
import rina.onepop.club.api.module.Module;
import rina.onepop.club.api.module.impl.ModuleCategory;
import rina.onepop.club.api.module.registry.Registry;
import rina.onepop.club.api.setting.value.ValueBoolean;
import rina.onepop.club.api.setting.value.ValueNumber;
import rina.onepop.club.client.event.network.PacketEvent;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

/**
 * @author BowMcBomb
 * @since 09/10/2021 at 17:43
 **/
@Registry(name = "Bow Bomb", tag = "BowBomb", description = "Spam bow.", category = ModuleCategory.COMBAT)
public class ModuleBowBomb extends Module {
    // Misc.
    public static ValueBoolean settingStrict = new ValueBoolean("Strict", "Strict", "Strict packet.", false);
    public static ValueNumber settingPackets = new ValueNumber("Packets", "Packets", "Amount of packets to send.", 5, 1, 50);
    public static ValueNumber settingDelay = new ValueNumber("Delay", "Delay", "Delay.", 5f, 0.1f, 10f);

    private final TurokTick delay = new TurokTick();

    @Listener
    public void onSendPacket(PacketEvent.Send event) {
        if (event.getPacket() instanceof CPacketPlayerDigging) {
            CPacketPlayerDigging packet = (CPacketPlayerDigging) event.getPacket();

            if (packet.getAction() == CPacketPlayerDigging.Action.RELEASE_USE_ITEM) {
                ItemStack handStack = mc.player.getHeldItem(EnumHand.MAIN_HAND);

                if (!handStack.isEmpty() && handStack.getItem() instanceof ItemBow) {
                    if (this.delay.isPassedMS(settingDelay.getValue().floatValue() * 1000f)) {
                        this.delay.reset();

                        mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SPRINTING));

                        for (int i = 0; i < settingPackets.getValue().intValue(); ++i) {
                            if (settingStrict.getValue()) {
                                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 1e-10, mc.player.posZ, false));
                                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY - 1e-10, mc.player.posZ, true));
                            } else {
                                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY - 1e-10, mc.player.posZ, true));
                                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 1e-10, mc.player.posZ, false));
                            }
                        }
                    }
                }
            }
        }
    }
}
