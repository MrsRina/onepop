package rina.onepop.club.client.module.render.esp;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.math.BlockPos;
import rina.onepop.club.api.module.Module;
import rina.onepop.club.api.module.impl.ModuleCategory;
import rina.onepop.club.api.module.registry.Registry;
import rina.onepop.club.api.setting.value.ValueBoolean;
import rina.onepop.club.api.setting.value.ValueColor;
import rina.onepop.club.api.setting.value.ValueEnum;
import rina.onepop.club.api.setting.value.ValueNumber;
import rina.onepop.club.api.util.client.NullUtil;
import rina.onepop.club.api.util.render.RenderUtil;
import rina.onepop.club.api.util.world.BlockUtil;
import rina.onepop.club.client.event.client.RunTickEvent;
import rina.onepop.club.client.module.render.esp.impl.ModeRender;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

/**
 * @author SrRina
 * @since 19/07/2021 at 19:09
 **/
@Registry(name = "Item ESP", tag = "ItemESP", description = "ESP item.", category = ModuleCategory.RENDER)
public class ModuleItemESP extends Module {
    // Misc.
    public static ValueBoolean settingChunk = new ValueBoolean("Chunk", "Chunk", "Render everything on chunk.", false);
    public static ValueNumber settingRange = new ValueNumber("Range", "Range", "Sets range.", 50, 1, 500);

    public static ValueColor settingColor = new ValueColor("Color", "Color", "Sets color of render.", Color.WHITE);
    public static ValueNumber settingLineSize = new ValueNumber("Line Size", "LineSize", "Sets line size.", 1.0f, 1f, 3.0f);
    public static ValueNumber settingLineAlpha = new ValueNumber("Line Alpha", "LineAlpha", "Sets line alpha color.", 255, 0, 255);

    public static ValueBoolean settingFullBlock = new ValueBoolean("Full Block", "FullBlock", "Render full block.", false);
    public static ValueEnum settingMode = new ValueEnum("Mode", "Mode", "Mode for render.", ModeRender.TOUCH);

    private final Set<EntityItem> itemSet = new HashSet<>();
    private final Set<BlockPos> itemPosition = new HashSet<>();

    @Override
    public void onSetting() {
        settingColor.setEnabled(settingMode.getValue() != ModeRender.MINECRAFT);
        settingLineSize.setEnabled(settingMode.getValue() != ModeRender.MINECRAFT);
        settingLineAlpha.setEnabled(settingMode.getValue() != ModeRender.MINECRAFT);
        settingFullBlock.setEnabled(settingMode.getValue() == ModeRender.TOUCH);

        settingRange.setEnabled(!settingChunk.getValue());
    }

    @Override
    public void onRender3D() {
        this.itemPosition.clear();

        for (Entity entities : mc.world.loadedEntityList) {
            if (!(entities instanceof EntityItem)) {
                continue;
            }

            if (this.itemPosition.contains(entities.getPosition()) && settingMode.getValue() == ModeRender.TOUCH) {
                continue;
            }

            float distance = mc.player.getDistance(entities);

            if (distance > settingRange.getValue().floatValue() || settingChunk.getValue()) {
                if (settingMode.getValue() == ModeRender.BOX) {
                    RenderUtil.drawSolidBlock(camera, entities.posX - ((entities.width) / 2),entities.posY + entities.getEyeHeight(), entities.posZ - ((entities.width) / 2), (entities.width), entities.getEyeHeight(), (entities.width), settingColor.getColor());
                    RenderUtil.drawOutlineBlock(camera, entities.posX - ((entities.width) / 2), entities.posY + entities.getEyeHeight(), entities.posZ - ((entities.width) / 2), (entities.width), entities.getEyeHeight(), (entities.width), settingLineSize.getValue().floatValue(), settingColor.getColor(settingLineAlpha.getValue().intValue()));
                }

                this.itemPosition.add(entities.getPosition());
            }
        }

        if (settingMode.getValue() != ModeRender.TOUCH) {
            return;
        }

        for (final BlockPos position : this.itemPosition) {
            final BlockPos renderPosition = position.add(0, -1, 0);

            if (!BlockUtil.isAir(renderPosition)) {
                if (settingFullBlock.getValue()) {
                    RenderUtil.drawSolidBlock(camera, renderPosition, settingColor.getColor());
                    RenderUtil.drawOutlineBlock(camera, renderPosition, settingLineSize.getValue().floatValue(), settingColor.getColor(settingLineAlpha.getValue().intValue()));
                } else {
                    RenderUtil.drawSolidBlock(camera, position.x, position.y, position.z, 1, 0, 1, settingColor.getColor());
                    RenderUtil.drawOutlineBlock(camera, position.x, position.y, position.z, 1, 0, 1, settingLineSize.getValue().floatValue(), settingColor.getColor(settingLineAlpha.getValue().intValue()));
                }
            }
        }
    }

    @Listener
    public void onTick(RunTickEvent event) {
        if (NullUtil.isPlayerWorld()) {
            return;
        }

        if (settingMode.getValue() == ModeRender.MINECRAFT) {
            for (Entity entities : mc.world.loadedEntityList) {
                if (entities instanceof EntityItem) {
                    if (settingChunk.getValue()) {
                        entities.setGlowing(true);

                        this.itemSet.add((EntityItem) entities);

                        continue;
                    }

                    float distance = mc.player.getDistance(entities);

                    if (distance > settingRange.getValue().floatValue()) {
                        entities.setGlowing(false);

                        this.itemSet.remove((EntityItem) entities);
                    } else {
                        entities.setGlowing(true);

                        if (!this.itemSet.contains((EntityItem) entities)) {
                            this.itemSet.add((EntityItem) entities);
                        }
                    }
                }
            }
        } else {
            // We do a memory copy.
            for (EntityItem entitiesItem : new HashSet<>(this.itemSet)) {
                entitiesItem.setGlowing(false);

                this.itemSet.remove(entitiesItem);
            }
        }
    }
}
