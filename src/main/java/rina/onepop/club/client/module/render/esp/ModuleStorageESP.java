package rina.onepop.club.client.module.render.esp;

import net.minecraft.tileentity.*;
import rina.onepop.club.api.module.Module;
import rina.onepop.club.api.module.impl.ModuleCategory;
import rina.onepop.club.api.module.registry.Registry;
import rina.onepop.club.api.setting.value.ValueBoolean;
import rina.onepop.club.api.setting.value.ValueColor;
import rina.onepop.club.api.setting.value.ValueNumber;
import rina.onepop.club.api.util.client.NullUtil;
import rina.onepop.club.api.util.render.RenderUtil;
import rina.onepop.club.client.event.client.ClientTickEvent;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author SrRina
 * @since 15/05/2021 at 17:09
 *
 * Credit for Seth also, because I used his util getColor() from Seppuku Client.
 *
 **/
@Registry(name = "Storage ESP", tag = "StorageESP", description = "Render storages close of you.", category = ModuleCategory.RENDER)
public class ModuleStorageESP extends Module {
    /* Misc. */
    public static ValueBoolean settingChunk = new ValueBoolean("Chunk", "Chunk", "Render all in chunk.", false);
    public static ValueNumber settingRange = new ValueNumber("Range", "Range", "Range for esp.", 500, 0, 1000);

    // Colors.
    public static ValueColor settingEnchest = new ValueColor("Enchest", "Enchest", "Set color for enchest.", true, new Color(255, 0, 255, 100));
    public static ValueColor settingChest = new ValueColor("Chest", "Chest", "Sets chest color.", true, new Color(255, 255, 0, 100));
    public static ValueColor settingRandom = new ValueColor("Random", "Random", "Furnaces, droppers, dispensers...", true, new Color(190, 190, 190, 100));
    public static ValueBoolean settingShulker = new ValueBoolean("Shulker", "Shulker", "Shulkers.", true);
    public static ValueNumber settingShulkerAlpha = new ValueNumber("Shulker Alpha", "ShulkerAlpha", "Sets alpha color.", 100, 0, 255);

    public static ValueNumber settingLineAlpha = new ValueNumber("Line Alpha", "LineAlpha", "Sets line alpha.",  255, 0, 255);
    public static ValueNumber settingLineSize = new ValueNumber("Line Size", "LineSize", "Sets line size.", 1f, 1f, 5f);

    private final List<TileEntity> renderList = new ArrayList<>();

    @Override
    public void onSetting() {
        settingRange.setEnabled(!settingChunk.getValue());
        settingShulkerAlpha.setEnabled(settingShulker.getValue());
    }

    @Override
    public void onRender3D() {
        if (NullUtil.isPlayerWorld()) {
            return;
        }

        for (TileEntity tileEntities : this.renderList) {
            int lineAlpha = settingLineAlpha.getValue().intValue();
            float lineSize = settingLineSize.getValue().intValue();

            final Color color = this.getColor(tileEntities);
            final Color outline = new Color(color.getRed(), color.getGreen(), color.getBlue(), lineAlpha);

            RenderUtil.drawSolidBlock(camera, tileEntities.getPos(), color);
            RenderUtil.drawOutlineBlock(camera, tileEntities.getPos(), lineSize, outline);
        }
    }

    @Listener
    public void onClientTickEvent(ClientTickEvent event) {
        if (NullUtil.isPlayerWorld()) {
            return;
        }

        this.renderList.clear();

        for (TileEntity tileEntities : mc.world.loadedTileEntityList) {
            if (tileEntities == null) {
                continue;
            }

            if (!settingChunk.getValue() && mc.player.getDistance(tileEntities.getPos().x, tileEntities.getPos().y, tileEntities.getPos().z) > settingRange.getValue().intValue()) {
                continue;
            }

            if (this.doAccept(tileEntities)) {
                this.renderList.add(tileEntities);
            }
        }
    }

    public boolean doAccept(TileEntity tile) {
        boolean accepted = false;

        if (tile instanceof TileEntityChest && settingChest.getValue()) {
            accepted = true;
        }

        if (tile instanceof TileEntityBrewingStand && settingRandom.getValue()) {
            accepted = true;
        }

        if (tile instanceof TileEntityEnderChest && settingEnchest.getValue()) {
            accepted = true;
        }

        if (tile instanceof TileEntityShulkerBox && settingRandom.getValue()) {
            accepted = true;
        }

        if (tile instanceof TileEntityDropper && settingRandom.getValue()) {
            accepted = true;
        }

        if (tile instanceof TileEntityHopper && settingRandom.getValue()) {
            accepted = true;
        }

        if (tile instanceof TileEntityDispenser && settingRandom.getValue()) {
            accepted = true;
        }

        if (tile instanceof TileEntityFurnace && settingRandom.getValue()) {
            accepted = true;
        }

        return accepted;
    }

    public Color getColor(TileEntity tile) {
        if (tile instanceof TileEntityChest && settingChest.getValue()) {
            return settingChest.getColor();
        }

        if (tile instanceof TileEntityDropper && settingRandom.getValue()) {
            return settingRandom.getColor();
        }

        if (tile instanceof TileEntityDispenser && settingRandom.getValue())  {
            return settingRandom.getColor();
        }

        if (tile instanceof TileEntityHopper && settingRandom.getValue()) {
            return settingRandom.getColor();
        }

        if (tile instanceof TileEntityFurnace && settingRandom.getValue()) {
            return settingRandom.getColor();
        }

        if (tile instanceof TileEntityBrewingStand && settingRandom.getValue()) {
            return settingRandom.getColor();
        }

        if (tile instanceof TileEntityEnderChest && settingEnchest.getValue()) {
            return settingEnchest.getColor();
        }

        if (tile instanceof TileEntityShulkerBox && settingShulker.getValue()) {
            final TileEntityShulkerBox shulkerBox = (TileEntityShulkerBox) tile;

            int hex = (255 << 24) | shulkerBox.getColor().getColorValue();

            final int r = (hex & 0xFF0000) >> 16;
            final int g = (hex & 0xFF00) >> 8;
            final int b = (hex & 0xFF);

            return new Color(r, g, b, settingShulkerAlpha.getValue().intValue());
        }

        return new Color(0, 0, 0, 0);
    }
}
