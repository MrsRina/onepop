package rina.onepop.club.mixin.mixins.entity;

import rina.onepop.club.Onepop;
import rina.onepop.club.client.event.entity.PushPlayerEvent;
import rina.onepop.club.client.module.player.ModuleSafeWalk;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * @author SrRina
 * @since 23/02/2021 at 22:57
 **/
@Mixin(Entity.class)
public abstract class MixinEntity {
    @Shadow
    public void move(MoverType type, double x, double y, double z) {}

    @Shadow public abstract boolean removeTag(String tag);

    @Shadow public int entityId;

    // Event
    @Redirect(method = "applyEntityCollision", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;addVelocity(DDD)V"))
    public void onPushPlayer(Entity entity, double x, double y, double z) {
        final PushPlayerEvent event = new PushPlayerEvent();

        Onepop.getPomeloEventManager().dispatchEvent(event);

        if (!event.isCanceled()) {
            entity.motionX += x;
            entity.motionY += y;
            entity.motionZ += z;
        }
    }

    // redirect should work?
    @Redirect(method = "move", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;isSneaking()Z"))
    public boolean isSneaking(Entity entity) {
        return ModuleSafeWalk.INSTANCE.isEnabled() || entity.isSneaking();
    }
}