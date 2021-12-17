package rina.onepop.club.client.module.render;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import rina.onepop.club.api.ISLClass;
import rina.onepop.club.api.module.Module;
import rina.onepop.club.api.module.impl.ModuleCategory;
import rina.onepop.club.api.module.registry.Registry;
import rina.onepop.club.api.setting.value.ValueColor;
import rina.onepop.club.api.setting.value.ValueNumber;
import rina.onepop.club.api.util.client.NullUtil;
import rina.onepop.club.api.util.render.RenderUtil;
import rina.onepop.club.client.module.client.anticheat.ModuleAntiCheat;

import java.awt.*;

/**
 * @author SrRina
 * @since 06/12/20 at 03:48pm
 **/
@Registry(name = "Block Highlight", tag = "BlockHighlight", description = "Draw block over the mouse.", category = ModuleCategory.RENDER)
public class ModuleBlockHighlight extends Module {
    /* Color. */
    public static ValueColor settingColor = new ValueColor("Color", "Color", "Sets color", new Color(190, 190, 0, 100));
    public static ValueNumber settingLineSize = new ValueNumber("Line Size", "LineSize", "Sets line size.", 1.0f, 1f, 3.0f);
    public static ValueNumber settingLineAlpha = new ValueNumber("Line Alpha", "LineAlpha", "Sets line alpha color.", 255, 0, 255);

    /* Color for render. */
    private Color outline = new Color(255, 255, 255, 255);
    private Color solid = new Color(255, 255, 255, 255);

    @Override
    public void onSetting() {
        this.solid = settingColor.getColor();
        this.outline = settingColor.getColor(settingLineAlpha.getValue().intValue());
    }

    @Override
    public void onRender3D() {
        if (NullUtil.isWorld()) {
            return;
        }

        // Get the mouse object over split.
        RayTraceResult splitResult = ISLClass.mc.player.rayTrace(ModuleAntiCheat.getRange(), ISLClass.mc.getRenderPartialTicks());

        if (splitResult != null && splitResult.typeOfHit == RayTraceResult.Type.BLOCK) {
            BlockPos block = splitResult.getBlockPos();

            // No air!
            float l = settingLineSize.getValue().floatValue();

            RenderUtil.drawSolidBlock(camera, block.x, block.y, block.z, 1, 1, 1, this.solid);
            RenderUtil.drawOutlineBlock(camera, block.x, block.y, block.z, 1, 1, 1, l, this.outline);
        }
    }
}
