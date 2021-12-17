package rina.onepop.club.mixin.mixins.render;

import rina.onepop.club.Onepop;
import rina.onepop.club.api.event.impl.EventStage;
import rina.onepop.club.client.event.render.RenderModelEvent;
import rina.onepop.club.client.event.render.RenderNameEvent;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

/**
 * @author SrRina
 * @since 25/02/2021 at 16:21
 **/
@Mixin(RenderLivingBase.class)
public class MixinRenderLivingBase<T extends EntityLivingBase> extends Render<T> {
    public MixinRenderLivingBase(RenderManager renderManagerIn, ModelBase modelBaseIn, float shadowSizeIn) {
        super(renderManagerIn);
    }

    @Inject(method = "renderName", at = @At("HEAD"), cancellable = true)
    public void onRenderNamePre(T entity, double x, double y, double z, CallbackInfo ci) {
        RenderNameEvent event = new RenderNameEvent(EventStage.PRE);

        Onepop.getPomeloEventManager().dispatchEvent(event);

        if (event.isCanceled()) {
            ci.cancel();
        }
    }

    @Inject(method = "renderName", at = @At("RETURN"), cancellable = true)
    public void onRenderNamePost(T entity, double x, double y, double z, CallbackInfo ci) {
        RenderNameEvent event = new RenderNameEvent(EventStage.POST);

        Onepop.getPomeloEventManager().dispatchEvent(event);

        if (event.isCanceled()) {
            ci.cancel();
        }
    }

    @Redirect(method = "renderModel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/ModelBase;render(Lnet/minecraft/entity/Entity;FFFFFF)V"))
    public void renderModel(ModelBase modelBase, Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        final RenderModelEvent event = new RenderModelEvent((EntityLivingBase) entityIn, modelBase, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);

        Onepop.getPomeloEventManager().dispatchEvent(event);

        if (event.isCanceled()) {
            return;
        }

        event.getModelBase().render(entityIn, event.getLimbSwing(), event.getLimbSwingAmount(), event.getAgeInTicks(), event.getNetHeadYaw(), event.getHeadPitch(), event.getScaleFactor());
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(T t) {
        return null;
    }
}