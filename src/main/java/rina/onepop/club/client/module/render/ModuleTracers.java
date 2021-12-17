package rina.onepop.club.client.module.render;

import me.rina.turok.render.opengl.TurokRenderGL;
import me.rina.turok.util.TurokMath;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import rina.onepop.club.Onepop;
import rina.onepop.club.api.module.Module;
import rina.onepop.club.api.module.impl.ModuleCategory;
import rina.onepop.club.api.module.registry.Registry;
import rina.onepop.club.api.setting.value.ValueBoolean;
import rina.onepop.club.api.setting.value.ValueColor;
import rina.onepop.club.api.setting.value.ValueNumber;
import rina.onepop.club.api.util.client.NullUtil;

import java.awt.*;

/**
 * @author SrRina
 * @since 06/07/2021 at 01:06
 **/
@Registry(name = "Tracers", tag = "Tracers", description = "Render tracers.", category = ModuleCategory.RENDER)
public class ModuleTracers extends Module {
    // Misc.
    public static ValueBoolean settingChunk = new ValueBoolean("Chunk", "Chunk", "Render all in chunk.", false);
    public static ValueNumber settingRange = new ValueNumber("Range", "Range", "Range for esp.", 500, 0, 1000);

    public static ValueColor settingPlayer = new ValueColor("Player", "Player","Option for set color and enable.", true, new Color(255, 255, 255, 100));
    public static ValueColor settingEnderCrystal = new ValueColor("Ender Crystal", "EnderCrystal", "Option for set color and enable.", true, new Color(190, 190, 190, 255));
    public static ValueColor settingAnimals = new ValueColor("Animals & Pigs", "Animals", "Option for set color and enable.", true, new Color(100, 200, 100, 100));
    public static ValueColor settingMobs = new ValueColor("Mobs", "Mobs", "Option for set color and enable.", true, new Color(100, 0, 0, 200));
    public static ValueColor settingStorage = new ValueColor("Storage", "Storage", "Option for set color and enable.", false, new Color(190, 190, 190, 190));

    public static ValueNumber settingLineSize = new ValueNumber("Line Size", "LineSize", "Sets line size.", 1f, 1f, 5f);

    @Override
    public void onRender3D() {
        if (NullUtil.isPlayerWorld()) {
            return;
        }

        for (Entity entities : mc.world.loadedEntityList) {
            if (entities.isEntityAlive() && entities != mc.player && (settingChunk.getValue() || mc.player.getDistance(entities) <= settingRange.getValue().floatValue())) {
                final Color colorCheck = this.getCheckedColorByEntity(entities);

                if (colorCheck == null) {
                    continue;
                }

                float partialX = (float) TurokMath.lerp(entities.lastTickPosX, entities.posX, Onepop.getClientEventManager().getCurrentRender3DPartialTicks());
                float partialY = (float) TurokMath.lerp(entities.lastTickPosY, entities.posY, Onepop.getClientEventManager().getCurrentRender3DPartialTicks());
                float partialZ = (float) TurokMath.lerp(entities.lastTickPosZ, entities.posZ, Onepop.getClientEventManager().getCurrentRender3DPartialTicks());

                this.doRender(partialX, partialY + entities.height / 2, partialZ, colorCheck);
            }
        }

        if (settingStorage.getValue()) {
            for (TileEntity entities : mc.world.loadedTileEntityList) {
                final BlockPos position = entities.getPos();

                if ((settingChunk.getValue() || mc.player.getDistance(position.x, position.y, position.z) <= settingRange.getValue().floatValue()) && this.isStorage(entities)) {
                    this.doRender(position.x + .5, position.y + .5, position.z + .5, settingStorage.getColor());
                }
            }
        }
    }

    public void doRender(double x, double y, double z, Color color) {
        if (mc.entityRenderer == null) {
            return;
        }

        boolean flagBobbing = mc.gameSettings.viewBobbing;

        mc.gameSettings.viewBobbing = false;
        mc.entityRenderer.setupCameraTransform(Onepop.getClientEventManager().getCurrentRender3DPartialTicks(), 0);

        final Vec3d forward = new Vec3d(0, 0, 1).rotatePitch(-(float) Math.toRadians(mc.player.rotationPitch)).rotateYaw(-(float) Math.toRadians(mc.player.rotationYaw));

        TurokRenderGL.drawLine3D(forward.x, forward.y + mc.player.getEyeHeight(), forward.z, x - mc.getRenderManager().renderPosX, y - mc.getRenderManager().renderPosY, z - mc.getRenderManager().renderPosZ, color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha(), settingLineSize.getValue().floatValue());

        mc.gameSettings.viewBobbing = flagBobbing;
        mc.entityRenderer.setupCameraTransform(Onepop.getClientEventManager().getCurrentRender3DPartialTicks(), 0);
    }

    public Color getCheckedColorByEntity(final Entity entity) {
        if (entity instanceof EntityPlayer && settingPlayer.getValue()) {
            return settingPlayer.getColor();
        }

        if (entity instanceof EntityEnderCrystal && settingEnderCrystal.getValue()) {
            return settingEnderCrystal.getColor();
        }

        if (entity instanceof EntityAnimal && settingAnimals.getValue()) {
            return settingAnimals.getColor();
        }

        if (entity instanceof EntityMob && settingMobs.getValue()) {
            return settingMobs.getColor();
        }

        return null;
    }

    public boolean isStorage(TileEntity tile) {
        boolean accepted = false;

        if (tile instanceof TileEntityChest) {
            accepted = true;
        }

        if (tile instanceof TileEntityBrewingStand) {
            accepted = true;
        }

        if (tile instanceof TileEntityEnderChest) {
            accepted = true;
        }

        if (tile instanceof TileEntityShulkerBox) {
            accepted = true;
        }

        if (tile instanceof TileEntityDropper) {
            accepted = true;
        }

        if (tile instanceof TileEntityHopper) {
            accepted = true;
        }

        if (tile instanceof TileEntityDispenser) {
            accepted = true;
        }

        if (tile instanceof TileEntityFurnace) {
            accepted = true;
        }

        return accepted;
    }
}
