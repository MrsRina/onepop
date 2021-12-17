package rina.onepop.club.mixin.mixins.render;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderEnderCrystal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import rina.onepop.club.Onepop;
import rina.onepop.club.client.event.render.RenderModelEvent;

/**
 * @author SrRina
 * @since 06/07/2021 at 00:34
 **/
@Mixin(RenderEnderCrystal.class)
public class MixinRenderEnderCrystal {
    @Redirect(method = "doRender", at = @At(value = "INVOKE",  target = "Lnet/minecraft/client/model/ModelBase;render(Lnet/minecraft/entity/Entity;FFFFFF)V"))
    public void doRender(ModelBase modelBase, Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        final RenderModelEvent event = new RenderModelEvent(entityIn, modelBase, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);

        Onepop.getPomeloEventManager().dispatchEvent(event);

        if (event.isCanceled()) {
            return;
        }

        event.getModelBase().render(entityIn, event.getLimbSwing(), event.getLimbSwingAmount(), event.getAgeInTicks(), event.getNetHeadYaw(), event.getHeadPitch(), event.getScaleFactor());
    }
}