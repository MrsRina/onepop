package rina.onepop.club.mixin.mixins.network;

import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.play.server.SPacketDisconnect;
import net.minecraft.util.text.ITextComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import rina.onepop.club.Onepop;
import rina.onepop.club.api.event.impl.EventStage;
import rina.onepop.club.client.event.network.TextComponentEvent;

/**
 * @author SrRina
 * @since 01/07/2021 at 18:01
 **/
@Mixin(NetHandlerPlayClient.class)
public class MixinNetHandlerPlayClient {
}
