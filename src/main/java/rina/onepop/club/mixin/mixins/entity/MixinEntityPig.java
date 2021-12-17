package rina.onepop.club.mixin.mixins.entity;

import rina.onepop.club.Onepop;
import rina.onepop.club.api.event.impl.EventStage;
import rina.onepop.club.client.event.entity.PigEvent;
import net.minecraft.entity.passive.EntityPig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author SrRina
 * @since 16/05/2021 at 19:33
 *
 * PIGS <3
 **/
@Mixin(EntityPig.class)
public class MixinEntityPig {

    // event
    @Inject(method = "canBeSteered", at = @At("HEAD"), cancellable = true)
    public void onPigSteered(CallbackInfoReturnable<Boolean> cir) {
        final PigEvent event = new PigEvent(EventStage.PRE);

        Onepop.getPomeloEventManager().dispatchEvent(event);

        if (event.isCanceled()) {
            cir.setReturnValue(true);
        }
    }

    // event
    @Inject(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/passive/EntityPig;setAIMoveSpeed(F)V"), cancellable = true)
    public void onPigTravel(float strafe, float vertical, float forward, CallbackInfo ci) {
        final PigEvent event = new PigEvent(EventStage.POST);

        Onepop.getPomeloEventManager().dispatchEvent(event);

        if (event.isCanceled()) {
            ci.cancel();
        }
    }
}
