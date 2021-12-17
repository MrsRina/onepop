package rina.onepop.club.client.event.render;

import net.minecraft.entity.Entity;
import rina.onepop.club.api.event.Event;
import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.EntityLivingBase;

/**
 * @author SrRina
 * @since 19/04/2021 at 19:34
 **/
public class RenderModelEvent extends Event {
    /* The entity model is actual rendering. */
    private Entity entity;
    private ModelBase modelBase;

    /* Im not sure what are this fields values... */
    private float limbSwing;
    private float limbSwingAmount;
    private float ageInTicks;
    private float netHeadYaw;
    private float headPitch;
    private float scaleFactor;

    public RenderModelEvent(Entity entityIn, ModelBase modelBase, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor) {
        this.entity = entityIn;
        this.modelBase = modelBase;
        this.limbSwing = limbSwing;
        this.limbSwingAmount = limbSwingAmount;
        this.ageInTicks = ageInTicks;
        this.netHeadYaw = netHeadYaw;
        this.headPitch = headPitch;
        this.scaleFactor = scaleFactor;
    }

    public Entity getEntity() {
        return entity;
    }

    public ModelBase getModelBase() {
        return modelBase;
    }

    public void setModelBase(ModelBase modelBase) {
        this.modelBase = modelBase;
    }

    public void setLimbSwing(float limbSwing) {
        this.limbSwing = limbSwing;
    }

    public float getLimbSwing() {
        return limbSwing;
    }

    public void setLimbSwingAmount(float limbSwingAmount) {
        this.limbSwingAmount = limbSwingAmount;
    }

    public float getLimbSwingAmount() {
        return limbSwingAmount;
    }

    public void setAgeInTicks(float ageInTicks) {
        this.ageInTicks = ageInTicks;
    }

    public float getAgeInTicks() {
        return ageInTicks;
    }

    public void setNetHeadYaw(float netHeadYaw) {
        this.netHeadYaw = netHeadYaw;
    }

    public float getNetHeadYaw() {
        return netHeadYaw;
    }

    public void setHeadPitch(float headPitch) {
        this.headPitch = headPitch;
    }

    public float getHeadPitch() {
        return headPitch;
    }

    public void setScaleFactor(float scaleFactor) {
        this.scaleFactor = scaleFactor;
    }

    public float getScaleFactor() {
        return scaleFactor;
    }
}

