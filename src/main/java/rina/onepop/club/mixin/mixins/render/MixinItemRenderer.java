package rina.onepop.club.mixin.mixins.render;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import rina.onepop.club.Onepop;
import rina.onepop.club.client.event.render.EnumHandSideEvent;
import rina.onepop.club.client.module.render.ModuleNoRender;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.util.EnumHandSide;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderer.class)
public class MixinItemRenderer {
    @Inject(method = "transformSideFirstPerson", at = @At("HEAD"))
    public void transformSideFirstPerson(EnumHandSide hand, float p_187459_2_, CallbackInfo ci) {
        EnumHandSideEvent event = new EnumHandSideEvent(hand);
        Onepop.getPomeloEventManager().dispatchEvent(event);
    }

    @Inject(method = "transformFirstPerson", at = @At("HEAD"))
    public void transformFirstPerson(EnumHandSide hand, float p_187453_2_, CallbackInfo ci){
        EnumHandSideEvent event = new EnumHandSideEvent(hand);
        Onepop.getPomeloEventManager().dispatchEvent(event);
    }

    @Inject(method = "renderFireInFirstPerson", at = @At("HEAD"), cancellable = true)
    public void onRenderFireInFirstPerson(CallbackInfo ci) {
        boolean flag = ModuleNoRender.INSTANCE.isEnabled() && ModuleNoRender.settingFire.getValue();

        if (flag) {
            ci.cancel();
        }
    }

    @Inject(method = "renderSuffocationOverlay", at = @At("HEAD"), cancellable = true)
    public void onRenderSuffocationOverlay(TextureAtlasSprite sprite, CallbackInfo ci) {
        boolean flag = ModuleNoRender.INSTANCE.isEnabled() && ModuleNoRender.settingSuffocation.getValue();

        if (flag) {
            ci.cancel();
        }
    }
}