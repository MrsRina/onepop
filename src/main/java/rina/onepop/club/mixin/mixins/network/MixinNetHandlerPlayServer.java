package rina.onepop.club.mixin.mixins.network;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.client.CPacketVehicleMove;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import rina.onepop.club.Onepop;
import rina.onepop.club.api.event.impl.EventStage;
import rina.onepop.club.client.event.entity.UseEntityWorldProcessEvent;
import rina.onepop.club.client.event.network.TextComponentEvent;

/**
 * @author SrRina
 * @since 30/06/2021 at 15:39
 **/
@Mixin(NetHandlerPlayServer.class)
public abstract class MixinNetHandlerPlayServer {
    @Redirect(method = "processUseEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/play/client/CPacketUseEntity;getEntityFromWorld(Lnet/minecraft/world/World;)Lnet/minecraft/entity/Entity;"))
    public Entity onProcessUseEntity(CPacketUseEntity cPacketUseEntity, World worldIn) {
        final UseEntityWorldProcessEvent event = new UseEntityWorldProcessEvent(worldIn, cPacketUseEntity);

        Onepop.getPomeloEventManager().dispatchEvent(event);

        return event.isCanceled() ? null : worldIn.getEntityByID(event.getPacket().entityId);
    }
}
