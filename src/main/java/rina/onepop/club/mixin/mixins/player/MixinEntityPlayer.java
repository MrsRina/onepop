package rina.onepop.club.mixin.mixins.player;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import rina.onepop.club.Onepop;
import rina.onepop.club.client.event.entity.EntityCollisionEvent;
import rina.onepop.club.client.event.entity.EntityUpdateEvent;
import rina.onepop.club.client.event.entity.TravelEvent;
import rina.onepop.club.client.event.network.ConfirmEntityPacketEvent;
import rina.onepop.club.mixin.mixins.entity.MixinEntity;

/**
 * @author SrRina
 * @since 23/02/2021 at 23:05
 **/
@Mixin(EntityPlayer.class)
public abstract class MixinEntityPlayer extends MixinEntity {
    @Shadow
    public void jump() {}

    @Inject(method = "travel", at = @At("HEAD"))
    public void onTravelEvent(float strafe, float vertical, float forward, CallbackInfo ci) {
        TravelEvent event = new TravelEvent();

        Onepop.getPomeloEventManager().dispatchEvent(event);
    }

    @Inject(method = "applyEntityCollision", at = @At("HEAD"), cancellable = true)
    public void onApplyCollision(Entity entityIn, CallbackInfo ci) {
        final EntityCollisionEvent event = new EntityCollisionEvent(entityIn);

        Onepop.getPomeloEventManager().dispatchEvent(event);

        if (event.isCanceled()) {
            ci.cancel();
        }
    }

    @Inject(method = "interactOn", at = @At("HEAD"), cancellable = true)
    public void onInteract(Entity entityToInteractOn, EnumHand hand, CallbackInfoReturnable<EnumActionResult> cir) {
        final ConfirmEntityPacketEvent event = new ConfirmEntityPacketEvent(entityToInteractOn);

        Onepop.getPomeloEventManager().dispatchEvent(event);

        if (event.isCanceled()) {
            cir.cancel();
        }
    }

    @Inject(method = "onUpdate", at = @At("HEAD"), cancellable = false)
    public void onUpdate(CallbackInfo ci) {
        final EntityUpdateEvent event = new EntityUpdateEvent();

        Onepop.getPomeloEventManager().dispatchEvent(event);
    }
}
