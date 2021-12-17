package rina.onepop.club.mixin.mixins;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerArmorBase;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.inventory.EntityEquipmentSlot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import rina.onepop.club.client.module.render.ModuleNoRender;

/**
 * @author SrRina
 * @since 04/10/2021 at 21:59
 **/
@Mixin(LayerBipedArmor.class)
public abstract class MixinLayerBipedArmor extends LayerArmorBase<ModelBiped> {
    public MixinLayerBipedArmor(RenderLivingBase<?> rendererIn) {
        super(rendererIn);
    }

    @Inject(method = "setModelSlotVisible", at = @At("HEAD"), cancellable = true)
    protected void setModelSlotVisible(ModelBiped p_188359_1_, EntityEquipmentSlot slotIn, CallbackInfo ci) {
        boolean flag = ModuleNoRender.INSTANCE.isEnabled() && ModuleNoRender.settingNoRenderArmor.getValue();

        if (flag) {
            switch (slotIn) {
                case HEAD: {
                    modelArmor.bipedHead.showModel = false;
                    modelArmor.bipedHeadwear.showModel = false;

                    break;
                }

                case CHEST: {
                    modelArmor.bipedBody.showModel = false;
                    modelArmor.bipedRightArm.showModel = false;
                    modelArmor.bipedLeftArm.showModel = false;

                    break;
                }

                case LEGS: {
                    modelArmor.bipedBody.showModel = false;
                    modelArmor.bipedRightLeg.showModel = false;
                    modelArmor.bipedLeftLeg.showModel = false;

                    break;
                }

                case FEET: {
                    modelArmor.bipedRightLeg.showModel = false;
                    modelArmor.bipedLeftLeg.showModel = false;

                    break;
                }
            }
        }
    }
}
