package rina.onepop.club.client.module.misc;

import rina.onepop.club.Onepop;
import rina.onepop.club.api.ISLClass;
import rina.onepop.club.api.module.Module;
import rina.onepop.club.api.module.impl.ModuleCategory;
import rina.onepop.club.api.module.registry.Registry;
import rina.onepop.club.api.setting.value.ValueNumber;
import rina.onepop.club.api.util.client.NullUtil;
import rina.onepop.club.client.event.client.RunTickEvent;
import rina.onepop.club.client.event.network.PacketEvent;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;
import net.minecraft.util.EnumHand;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

import java.util.ArrayList;
import java.util.List;

/**
 * @author SrRina
 * @since 08/05/2021 at 21:00
 **/
@Registry(name = "Blink", tag = "Blink", description = "Cancels packets then sends on disable.", category = ModuleCategory.MISC)
public class ModuleBlink extends Module {
    /* Misc. */
    public static ValueNumber settingPacketLimiter = new ValueNumber("Packet Limiter", "PacketLimiter", "The maximum packets for resend!", 500, 100, 500);

    private final List<Packet<?>> currentPlayerPacket = new ArrayList<>();
    private int packetsCount;

    private EntityOtherPlayerMP customPlayer;

    @Override
    public void onShutdown() {
        this.setDisabled();
    }

    @Override
    public void onEnable() {
        if (NullUtil.isPlayerWorld()) {
            return;
        }

        this.packetsCount = 0;

        this.customPlayer = new EntityOtherPlayerMP(ISLClass.mc.world, ISLClass.mc.player.getGameProfile());
        this.customPlayer.copyLocationAndAnglesFrom(ISLClass.mc.player);

        Onepop.getEntityWorldManager().saveEntity(-888, this.customPlayer);
        mc.world.addEntityToWorld(-888, this.customPlayer);
    }

    @Override
    public void onDisable() {
        if (NullUtil.isPlayerWorld()) {
            return;
        }

        Onepop.getEntityWorldManager().removeEntity(-888);
        mc.world.removeEntityFromWorld(-888);

        this.packetsCount = 0;

        for (Packet<?> packets : new ArrayList<>(this.currentPlayerPacket)) {
            mc.player.connection.sendPacket(packets);

            this.currentPlayerPacket.remove(packets);
        }

        this.currentPlayerPacket.clear();
    }

    @Listener
    public void onTick(RunTickEvent event) {
        if (this.customPlayer != null) {
            this.customPlayer.setHealth(mc.player.getHealth());
            this.customPlayer.setHeldItem(EnumHand.MAIN_HAND, mc.player.getHeldItemMainhand());
            this.customPlayer.setHeldItem(EnumHand.OFF_HAND, mc.player.getHeldItemOffhand());
            this.customPlayer.inventory = mc.player.inventory;
        }

        if (this.packetsCount >= settingPacketLimiter.getValue().intValue()) {
            this.setDisabled();
        }
    }

    @Listener
    public void onSendPacket(PacketEvent.Send event) {
        if (NullUtil.isPlayerWorld()) {
            return;
        }

        if (event.getPacket() instanceof CPacketChatMessage || event.getPacket() instanceof CPacketConfirmTeleport || event.getPacket() instanceof CPacketKeepAlive || event.getPacket() instanceof CPacketTabComplete || event.getPacket() instanceof CPacketClientStatus) {
            return;
        }

        this.currentPlayerPacket.add(event.getPacket());

        event.setCanceled(true);
    }
}