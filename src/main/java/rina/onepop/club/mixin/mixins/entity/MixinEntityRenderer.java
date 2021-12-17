package rina.onepop.club.mixin.mixins.entity;

import com.google.common.base.Predicate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.util.glu.Project;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import rina.onepop.club.Onepop;
import rina.onepop.club.client.event.render.PerspectiveEvent;
import rina.onepop.club.client.module.misc.ModuleNoEntityTrace;
import rina.onepop.club.client.module.render.ModuleCustomCamera;
import rina.onepop.club.client.module.render.ModuleNoRender;

import java.util.ArrayList;
import java.util.List;

/**
 * @author SrRina
 * @since 22/03/2021 at 12:32
 **/
@Mixin(EntityRenderer.class)
public class MixinEntityRenderer {
    private final Minecraft mc = Minecraft.getMinecraft();

    @Shadow
    public ItemStack itemActivationItem;

    @Inject(method = "renderItemActivation", at = @At("HEAD"), cancellable = true)
    public void onRenderItemActivation(CallbackInfo ci) {
        boolean flag = ModuleNoRender.INSTANCE.isEnabled() && ModuleNoRender.settingTotemPop.getValue();

        if (this.itemActivationItem != null && this.itemActivationItem.getItem() == Items.TOTEM_OF_UNDYING && flag) {
            ci.cancel();
        }
    }

    @Redirect(method = "orientCamera", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/WorldClient;rayTraceBlocks(Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/RayTraceResult;"))
    public RayTraceResult rayTraceBlocks(WorldClient world, Vec3d start, Vec3d end) {
        boolean flag = ModuleCustomCamera.INSTANCE.isEnabled() && ModuleCustomCamera.settingNoCameraClip.getValue();

        return flag ? null : world.rayTraceBlocks(start, end);
    }

    @Redirect(method = "getMouseOver", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/WorldClient;getEntitiesInAABBexcluding(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/AxisAlignedBB;Lcom/google/common/base/Predicate;)Ljava/util/List;"))
    public List<Entity> getEntitiesInAABBexcluding(WorldClient worldClient, Entity entityIn, AxisAlignedBB boundingBox, Predicate predicate) {
        boolean flag = ModuleNoEntityTrace.INSTANCE.isEnabled() && ModuleNoEntityTrace.INSTANCE.doAccept();

        return flag ? new ArrayList<>() : worldClient.getEntitiesInAABBexcluding(entityIn, boundingBox, predicate);
    }

    @Inject(method = "hurtCameraEffect", at = @At("HEAD"), cancellable = true)
    public void onHurtCameraEffect(float partialTicks, CallbackInfo ci) {
        boolean flag = ModuleNoRender.INSTANCE.isEnabled() && ModuleNoRender.settingHurtEffectCamera.getValue();

        if (flag) {
            ci.cancel();
        }
    }

    @Inject(method = "setupFog", at = @At("RETURN"), cancellable = true)
    public void onSetupFrog(int startCoords, float partialTicks, CallbackInfo ci) {
        boolean flag = ModuleNoRender.INSTANCE.isEnabled() && ModuleNoRender.settingFog.getValue();

        if (flag) {
            GlStateManager.disableFog();
        }
    }

    @Redirect(method = "setupCameraTransform", at = @At(value = "INVOKE", target = "Lorg/lwjgl/util/glu/Project;gluPerspective(FFFF)V"))
    private void onSetupCameraTransform(float fovy, float aspect, float zNear, float zFar) {
        PerspectiveEvent event = new PerspectiveEvent((float) this.mc.displayWidth / (float) this.mc.displayHeight);
        Onepop.getPomeloEventManager().dispatchEvent(event);
        Project.gluPerspective(fovy, event.getAspect(), zNear, zFar);
    }

    @Redirect(method = "renderWorldPass", at = @At(value = "INVOKE", target = "Lorg/lwjgl/util/glu/Project;gluPerspective(FFFF)V"))
    private void onRenderWorldPass(float fovy, float aspect, float zNear, float zFar) {
        PerspectiveEvent event = new PerspectiveEvent((float) this.mc.displayWidth / (float) this.mc.displayHeight);
        Onepop.getPomeloEventManager().dispatchEvent(event);
        Project.gluPerspective(fovy, event.getAspect(), zNear, zFar);
    }

    @Redirect(method = "renderCloudsCheck", at = @At(value = "INVOKE", target = "Lorg/lwjgl/util/glu/Project;gluPerspective(FFFF)V"))
    private void onRenderCloudsCheck(float fovy, float aspect, float zNear, float zFar) {
        PerspectiveEvent event = new PerspectiveEvent((float) this.mc.displayWidth / (float) this.mc.displayHeight);
        Onepop.getPomeloEventManager().dispatchEvent(event);
        Project.gluPerspective(fovy, event.getAspect(), zNear, zFar);
    }
}
