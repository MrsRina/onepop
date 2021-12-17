package rina.onepop.club.mixin.mixins.gui;

import rina.onepop.club.client.module.render.ModuleNoRender;
import net.minecraft.client.gui.GuiBossOverlay;
import net.minecraft.world.BossInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author SrRina
 * @since 22/03/2021 at 14:50
 **/
@Mixin(GuiBossOverlay.class)
public class MixinBossOverlay {
    // event
    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void onRender(int x, int y, BossInfo info, CallbackInfo ci) {
        boolean flag = ModuleNoRender.INSTANCE.isEnabled() && ModuleNoRender.settingBossInfo.getValue();

        if (flag) {
            ci.cancel();
        }
    }
    // event
    @Inject(method = "renderBossHealth", at = @At("HEAD"), cancellable = true)
    public void onRenderBoosHealth(CallbackInfo ci) {
        boolean flag = ModuleNoRender.INSTANCE.isEnabled() && ModuleNoRender.settingBossInfo.getValue();

        if (flag) {
            ci.cancel();
        }
    }
}