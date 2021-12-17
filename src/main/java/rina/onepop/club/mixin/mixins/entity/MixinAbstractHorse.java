package rina.onepop.club.mixin.mixins.entity;

import rina.onepop.club.Onepop;
import rina.onepop.club.api.event.impl.EventStage;
import rina.onepop.club.client.event.entity.AbstractHorseEvent;
import net.minecraft.entity.passive.AbstractHorse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author SrRina
 * @since 16/05/2021 at 19:50
 **/
@Mixin(AbstractHorse.class)
public class MixinAbstractHorse {

    // Event
    @Inject(method = "canBeSteered", at = @At("HEAD"), cancellable = true)
    public void onAbstractHorseCanBeSteered(CallbackInfoReturnable<Boolean> cir) {
        final AbstractHorseEvent event = new AbstractHorseEvent(EventStage.PRE);

        Onepop.getPomeloEventManager().dispatchEvent(event);

        if (event.isCanceled()) {
            cir.setReturnValue(true);
        }
    }

    // Event
    @Inject(method = "isHorseSaddled", at = @At("HEAD"), cancellable = true)
    public void onSaddled(CallbackInfoReturnable<Boolean> cir) {
        final AbstractHorseEvent event = new AbstractHorseEvent(EventStage.POST);

        Onepop.getPomeloEventManager().dispatchEvent(event);

        if (event.isCanceled()) {
            cir.setReturnValue(true);
        }
    }
}