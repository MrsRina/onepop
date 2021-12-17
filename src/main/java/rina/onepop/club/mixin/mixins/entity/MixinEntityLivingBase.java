package rina.onepop.club.mixin.mixins.entity;

import net.minecraft.entity.EntityLivingBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import rina.onepop.club.Onepop;
import rina.onepop.club.client.event.entity.SetHealthEvent;

/**
 * @author SrRina
 * @since 03/05/2021 at 18:46
 **/
@Mixin(EntityLivingBase.class)
public class MixinEntityLivingBase {
    // Event
    @Inject(method = "setHealth", at = @At("HEAD"))
    public void onSetHealth(float health, CallbackInfo ci) {
        SetHealthEvent event = new SetHealthEvent(health);

        Onepop.getPomeloEventManager().dispatchEvent(event);
    }
}
