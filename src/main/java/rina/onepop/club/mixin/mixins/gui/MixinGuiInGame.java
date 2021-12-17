package rina.onepop.club.mixin.mixins.gui;

import rina.onepop.club.client.module.render.ModuleNoRender;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author SrRina
 * @since 25/02/2021 at 22:26
 **/
@Mixin(GuiIngame.class)
public class MixinGuiInGame {

    // event
    @Inject(method = "renderPotionEffects", at = @At("HEAD"), cancellable = true)
    public void onRenderPotionEffects(ScaledResolution resolution, CallbackInfo ci) {
        boolean flag = ModuleNoRender.INSTANCE.isEnabled() && ModuleNoRender.settingPotionIcons.getValue();

        if (flag) {
            ci.cancel();
        }
    }
    // event

    @Inject(method = "renderPumpkinOverlay", at = @At("HEAD"), cancellable = true)
    public void onRenderPumpkin(ScaledResolution scaledRes, CallbackInfo ci) {
        boolean flag = ModuleNoRender.INSTANCE.isEnabled() && ModuleNoRender.settingPumpkin.getValue();

        if (flag) {
            ci.cancel();
        }
    }
    // event

    @Inject(method = "renderPortal", at = @At("HEAD"), cancellable = true)
    public void onRenderPortal(float timeInPortal, ScaledResolution scaledRes, CallbackInfo ci) {
        boolean flag = ModuleNoRender.INSTANCE.isEnabled() && ModuleNoRender.settingPortal.getValue();

        if (flag) {
            ci.cancel();
        }
    }
}
