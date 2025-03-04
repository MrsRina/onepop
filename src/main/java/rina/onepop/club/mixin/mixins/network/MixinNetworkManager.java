package rina.onepop.club.mixin.mixins.network;

import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.network.play.server.SPacketSpawnObject;
import rina.onepop.club.Onepop;
import rina.onepop.club.api.util.chat.ChatUtil;
import rina.onepop.club.client.event.network.PacketEvent;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import rina.onepop.club.client.module.combat.autocrystalrewrite.ModuleAutoCrystalRewrite;

import static rina.onepop.club.client.module.combat.autocrystalrewrite.ModuleAutoCrystalRewrite.*;

/**
 * @author SrRina
 * @since 16/11/20 at 10:05pm
 */
@Mixin(NetworkManager.class)
public class MixinNetworkManager {
    @Inject(method = "sendPacket(Lnet/minecraft/network/Packet;)V", at = @At("HEAD"), cancellable = true)
    public void onSendPacket(Packet<?> packet, CallbackInfo callbackInfo) {
        PacketEvent event = new PacketEvent.Send(packet);

        Onepop.getPomeloEventManager().dispatchEvent(event);

        if (event.isCanceled()) {
            callbackInfo.cancel();
        }
    }

    @Inject(method = "channelRead0", at = @At("HEAD"), cancellable = true)
    public void onReceivePacket(ChannelHandlerContext context, Packet<?> packet, CallbackInfo callbackInfo) {
        if (settingPredict.getValue() && packet instanceof SPacketSpawnObject && ((SPacketSpawnObject) packet).getType() == 51) {
            ModuleAutoCrystalRewrite.INSTANCE.doBreakCrystal((SPacketSpawnObject) packet);
        }

        if (settingNoSoundDelay.getValue() && packet instanceof SPacketSoundEffect) {
            ModuleAutoCrystalRewrite.INSTANCE.doCleanCrystals((SPacketSoundEffect) packet);
        }

        final PacketEvent event = new PacketEvent.Receive(packet);
        Onepop.getPomeloEventManager().dispatchEvent(event);

        if (event.isCanceled()) {
            callbackInfo.cancel();
        }
    }

    @Inject(method = "exceptionCaught", at = @At("HEAD"), cancellable = true)
    private void exception(ChannelHandlerContext exc, Throwable exc_, CallbackInfo callback) {
        if (exc_ instanceof Exception) {
            callback.cancel();
        }
    }
}
