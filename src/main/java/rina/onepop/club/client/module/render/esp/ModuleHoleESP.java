package rina.onepop.club.client.module.render.esp;

import me.rina.turok.util.TurokMath;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import rina.onepop.club.Onepop;
import rina.onepop.club.api.ISLClass;
import rina.onepop.club.api.module.Module;
import rina.onepop.club.api.module.impl.ModuleCategory;
import rina.onepop.club.api.module.registry.Registry;
import rina.onepop.club.api.setting.value.ValueBoolean;
import rina.onepop.club.api.setting.value.ValueColor;
import rina.onepop.club.api.setting.value.ValueEnum;
import rina.onepop.club.api.setting.value.ValueNumber;
import rina.onepop.club.client.manager.world.HoleManager;
import rina.onepop.club.client.render.Cube;
import rina.onepop.club.client.render.type.EnumCube;

import java.awt.*;

/**
 * @author mwsrina
 * @since 28/01/2021 at 13:38
 **/
@Registry(name = "Hole ESP", tag = "HoleESP", description = "Draws holes to help visibility at crystal pvp.", category = ModuleCategory.RENDER)
public class ModuleHoleESP extends Module {
    // Misc.
    public static ValueNumber settingRange = new ValueNumber("Range", "Range", "Maximum distance to render.", 8f, 1f, 13f);
    public static ValueBoolean settingDoubleHole = new ValueBoolean("Double Hole", "DoubleHole", "Verify for double holes.", true);

    public static ValueColor settingSafeSolid = new ValueColor("Safe Solid", "SafeSolid", "Sets safe solid color.", true, Color.GREEN);
    public static ValueColor settingSafeOutline = new ValueColor("Safe Outline", "SafeOutline", "Sets safe outline color.", true, Color.GREEN);

    public static ValueColor settingUnsafeSolid = new ValueColor("Unsafe Solid", "UnsafeSolid", "Sets unsafe solid color.", true, Color.RED);
    public static ValueColor settingUnsafeOutline = new ValueColor("Unsafe Outline", "UnsafeOutline", "Sets unsafe outline color.", true, Color.RED);

    /* Solid. */
    public static ValueEnum settingSolidRender = new ValueEnum("Solid Render", "SolidRender", "Render mode.", EnumCube.MODERN);
    public static ValueBoolean settingSolidDepth = new ValueBoolean("Solid Depth", "SolidDepth", "Depth view for solid.", false);
    public static ValueNumber settingSolidOffsetY = new ValueNumber("Solid Offset Y", "SolidOffsetY", "Sets solid offset Y.", 0, 0, 100);

    /* Outline. */
    public static ValueEnum settingOutlineRender = new ValueEnum("Out. Render", "OutlineRender", "Render mode.", EnumCube.NORMAL);
    public static ValueNumber settingOutlineOffsetY = new ValueNumber("Out. Offset Y", "OutlineOffsetY", "Sets outline offset Y.", 0, 0, 100);
    public static ValueNumber settingOutlineSize = new ValueNumber("Outline Size", "OutlineSize", "Size of line.", 1f, 1f, 5f);
    public static ValueBoolean settingOutlineDepth = new ValueBoolean("Outline Depth", "OutlineDepth", "Depth view for outline.", true);

    @Override
    public void onSetting() {
        final boolean flagSolid = settingSafeSolid.getValue() || settingUnsafeSolid.getValue();
        final boolean flagOutline = settingSafeOutline.getValue() || settingUnsafeOutline.getValue();

        settingSolidDepth.setEnabled(flagSolid);
        settingSolidRender.setEnabled(flagSolid);
        settingSolidOffsetY.setEnabled(flagSolid && settingSolidRender.getValue() == EnumCube.NORMAL);

        settingOutlineDepth.setEnabled(flagOutline);
        settingOutlineRender.setEnabled(flagOutline);
        settingOutlineOffsetY.setEnabled(flagOutline && settingOutlineRender.getValue() == EnumCube.NORMAL);
        settingOutlineSize.setEnabled(flagOutline);
    }

    @Override
    public void onRender3D() {
        for (HoleManager.Hole holes : Onepop.getHoleManager().getHoleList()) {
            if (holes.getPosition().getDistance((int) ISLClass.mc.player.posX, (int) ISLClass.mc.player.posY, (int) ISLClass.mc.player.posZ) >= settingRange.getValue().intValue()) {
                continue;
            }

            if (!settingDoubleHole.getValue() && holes.getDirection() != null) {
                continue;
            }

            this.renderSolid(holes);
            this.renderOutline(holes);
        }
    }

    public void renderSolid(final HoleManager.Hole holeClass) {
        if ((holeClass.getType() == HoleManager.SAFE && !settingSafeSolid.getValue()) || (holeClass.getType() == HoleManager.UNSAFE && !settingUnsafeSolid.getValue())) {
            return;
        }

        final BlockPos hole = holeClass.getPosition();

        float w = 1f;
        float l = 1f;

        float x = hole.x;
        float z = hole.z;

        if (holeClass.getDirection() != null) {
            final EnumFacing facing = holeClass.getDirection();
            final BlockPos extra = hole.offset(facing);

            float diffX = extra.x - hole.x;
            float diffZ = extra.z - hole.z;

            x = diffX < 0 ? extra.x : hole.x;
            z = diffZ < 0 ? extra.z : hole.z;

            if (diffX != 0 && diffZ == 0) {
                w = 2;
            }

            if (diffX == 0 && diffZ != 0) {
                l = 2;
            }
        }

        final double[] position = new double[] {x, hole.y, z};
        final double[] size = new double[] {w, (settingSolidRender.getValue() == EnumCube.NORMAL ? (settingSolidOffsetY.getValue().floatValue() / 100f) : 1 + 0.25f), l};

        final ValueColor color = holeClass.getType() == HoleManager.SAFE ? settingSafeSolid : settingUnsafeSolid;

        final Color[] colors = new Color[] {
                color.getColor(this.getFadingEffectAlpha(color.getA(), hole)), settingSolidRender.getValue() == EnumCube.NORMAL ? color.getColor(this.getFadingEffectAlpha(color.getA(), hole)) : new Color(0, 0, 0, 0)
        };

        Cube.render(camera, position, size, -1, colors, settingSolidDepth.getValue(), (EnumCube) settingSolidRender.getValue());
    }

    public void renderOutline(final HoleManager.Hole holeClass) {
        if ((holeClass.getType() == HoleManager.SAFE && !settingSafeOutline.getValue()) || (holeClass.getType() == HoleManager.UNSAFE && !settingUnsafeOutline.getValue())) {
            return;
        }

        final BlockPos hole = holeClass.getPosition();

        float x = hole.x;
        float z = hole.z;

        float w = 1;
        float l = 1;

        if (holeClass.getDirection() != null) {
            final EnumFacing facing = holeClass.getDirection();
            final BlockPos extra = hole.offset(facing);

            float diffX = extra.x - hole.x;
            float diffZ = extra.z - hole.z;

            x = diffX < 0 ? extra.x : hole.x;
            z = diffZ < 0 ? extra.z : hole.z;

            if (diffX != 0 && diffZ == 0) {
                w = 2;
            }

            if (diffX == 0 && diffZ != 0) {
                l = 2;
            }
        }

        final double[] position = new double[] {x, hole.y, z};
        final double[] size = new double[] {w, (settingOutlineRender.getValue() == EnumCube.NORMAL ? (settingOutlineOffsetY.getValue().floatValue() / 100f) : 1 + 0.25f), l};

        final ValueColor color = holeClass.getType() == HoleManager.SAFE ? settingSafeOutline : settingUnsafeOutline;

        final Color[] colors = new Color[] {
                color.getColor(this.getFadingEffectAlpha(color.getA(), hole)), settingOutlineRender.getValue() == EnumCube.NORMAL ? color.getColor(this.getFadingEffectAlpha(color.getA(), hole)) : new Color(0, 0, 0, 0)
        };

        Cube.render(camera, position, size, settingOutlineSize.getValue().floatValue(), colors, settingOutlineDepth.getValue(), (EnumCube) settingOutlineRender.getValue());
    }

    public int getFadingEffectAlpha(int alpha, BlockPos position) {
        return TurokMath.clamp(alpha - (int) TurokMath.distancingValues((float) mc.player.getDistanceSq(position), (settingRange.getValue().intValue() * settingRange.getValue().intValue()), alpha), 0, alpha);
    }
}