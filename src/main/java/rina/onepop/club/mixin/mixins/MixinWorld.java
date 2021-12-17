package rina.onepop.club.mixin.mixins;

import rina.onepop.club.Onepop;
import rina.onepop.club.client.event.entity.PushWatterEvent;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * @author SrRina
 * @since 19/05/2021 at 01:37
 **/
@Mixin(World.class)
public class MixinWorld {
    @Redirect(method = "handleMaterialAcceleration", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;isPushedByWater()Z"))
    public boolean onPushWatter(Entity entity) {
        final PushWatterEvent event = new PushWatterEvent();

        Onepop.getPomeloEventManager().dispatchEvent(event);

        return !event.isCanceled() && entity.isPushedByWater();
    }
}