package rina.onepop.club.client.module.render.esp;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import rina.onepop.club.api.module.Module;
import rina.onepop.club.api.module.impl.ModuleCategory;
import rina.onepop.club.api.module.registry.Registry;
import rina.onepop.club.api.setting.value.ValueBoolean;
import rina.onepop.club.api.setting.value.ValueColor;
import rina.onepop.club.api.setting.value.ValueEnum;
import rina.onepop.club.api.setting.value.ValueNumber;
import rina.onepop.club.client.event.render.RenderModelEvent;
import rina.onepop.club.client.module.render.esp.impl.Mode;
import rina.onepop.club.client.module.render.esp.impl.Type;
import rina.onepop.club.client.module.render.esp.process.ProcessESP;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

import java.awt.*;

/**
 * @author SrRina
 * @since 11/07/2021 at 00:30
 **/
@Registry(name = "Entity ESP", tag = "EntityESP", description = "Chams entity.",  category = ModuleCategory.RENDER)
public class ModuleEntityESP extends Module {
    /* Misc. */
    public static ValueColor settingEnderCrystal = new ValueColor("Ender Crystal", "EnderCrystal", "Set color and enable.", true, new Color(255, 0,  255, 100));
    public static ValueColor settingMobs = new ValueColor("Mobs", "Mobs", "Set color and enable.", true, new Color(255, 0,  255, 100));
    public static ValueColor settingAnimals = new ValueColor("Animals & Pigs", "Animals", "Set color and enable.", true, new Color(255, 0,  255, 100));

    public static ValueBoolean settingFrustumNoRender = new ValueBoolean("Frustum No Render", "FrustumNoRender", "Disable ESP on frustum area!", true);
    public static ValueEnum settingLineType = new ValueEnum("Line Type", "LineType", "Sets line type.", Type.SOFT);
    public static ValueNumber settingScale = new ValueNumber("Scale","Scale", "Scale of entity.", 1000, 0, 2000);
    public static ValueNumber settingOffsetY = new ValueNumber("Offset Y", "OffsetY", "Offset space for Y", 0, -2000, 2000);
    public static ValueEnum settingRenderMode = new ValueEnum("Render Mode", "RenderMode", "Type of render.", Mode.SMOOTH);

    /* Post. */
    public static ValueNumber settingAlpha = new ValueNumber("Alpha", "Alpha", "Sets alpha value.", 100, 0, 255);

    /* Render color. */
    public static ValueNumber settingLineSize = new ValueNumber("Line Size", "LineSize", "Sets line size.", 1f, 1f, 5f);

    @Override
    public void onSetting() {
        settingAlpha.setEnabled(settingRenderMode.getValue() == Mode.SKIN || settingRenderMode.getValue() == Mode.OUTLINE);
        settingLineSize.setEnabled(settingRenderMode.getValue() == Mode.SMOOTH || settingRenderMode.getValue() == Mode.OUTLINE);
    }

    @Listener
    public void onRenderModel(RenderModelEvent event) {
        final ValueColor color = this.getColorByCheck(event.getEntity());

        if (color != null) {
            event.setCanceled(true);

            ProcessESP.entityESP(event, settingScale.getValue().floatValue(), settingOffsetY.getValue().floatValue(), settingAlpha.getValue().intValue(), settingLineSize.getValue().floatValue(), settingLineType.getValue() != Type.WIRE, !settingFrustumNoRender.getValue(), color, (Mode) settingRenderMode.getValue());
        }
    }

    public ValueColor getColorByCheck(Entity entity) {
        if (entity instanceof EntityEnderCrystal && settingEnderCrystal.getValue()) {
            return settingEnderCrystal;
        }

        if (entity instanceof EntityMob && settingMobs.getValue()) {
            return settingMobs;
        }

        if (entity instanceof EntityAnimal && settingAnimals.getValue()) {
            return settingAnimals;
        }

        return null;
    }
}