package rina.onepop.club.mixin.mixins.entity;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.MoverType;
import org.spongepowered.asm.mixin.Mixin;
import rina.onepop.club.Onepop;
import rina.onepop.club.client.event.entity.PlayerJumpEvent;
import rina.onepop.club.client.event.entity.PlayerMoveEvent;
import rina.onepop.club.mixin.interfaces.IEntityPlayerSP;
import rina.onepop.club.mixin.mixins.player.MixinEntityPlayer;

/**
 * @author SrRina
 * @since 23/02/2021 at 23:06
 **/
@Mixin(EntityPlayerSP.class)
public abstract class MixinEntityPlayerSP extends MixinEntityPlayer implements IEntityPlayerSP {
    @Override
    public void jump() {
        PlayerJumpEvent event = new PlayerJumpEvent();

        if (!event.isCanceled()) {
            super.jump();
        }
    }

    // Event
    @Override
    public void move(MoverType type, double x, double y, double z) {
        PlayerMoveEvent event = new PlayerMoveEvent(type, x, y, z);

        Onepop.getPomeloEventManager().dispatchEvent(event);

        super.move(event.getType(), event.getX(), event.getY(), event.getZ());
    }

   /* @Inject(method = "pushOutOfBlocks", at = @At("HEAD"), cancellable = true)
    public void onPushByBlocks(double x, double y, double z, CallbackInfoReturnable<Boolean> cir) {

    } don't need this...*/
}