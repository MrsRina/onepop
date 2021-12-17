package rina.onepop.club.client.module.render;

import net.minecraft.util.math.BlockPos;
import rina.onepop.club.api.module.Module;
import rina.onepop.club.api.module.impl.ModuleCategory;
import rina.onepop.club.api.module.registry.Registry;
import rina.onepop.club.api.setting.value.ValueColor;
import rina.onepop.club.api.setting.value.ValueNumber;
import rina.onepop.club.api.util.client.NullUtil;
import rina.onepop.club.api.util.render.RenderUtil;
import rina.onepop.club.api.util.world.BlockUtil;

import java.awt.*;

/**
 * @author SrRina
 * @since 27/09/2021 at 00:56AM
 */
@Registry(name = "Feet Highlight", tag = "FeetHighlight", description = "Highlight the block you are on.", category = ModuleCategory.RENDER)
public class ModuleFeetHighlight extends Module {
    // Misc.
    public static ValueColor settingColor = new ValueColor("Color", "Color", "Sets color.", Color.ORANGE);
    public static ValueNumber settingLineAlpha = new ValueNumber("Line Alpha", "LineAlpha", "Sets line alpha.", 255, 0, 255);
    public static ValueNumber settingLineSize = new ValueNumber("Line Size", "LineSize", "Sets line size.", 1f, 1f, 5f);

    @Override
    public void onRender3D() {
        if (NullUtil.isPlayerWorld()) {
            return;
        }

        final BlockPos position = new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ);

        if (mc.player.onGround && !BlockUtil.isAir(position.add(0, -1, 0))) {
            RenderUtil.drawSolidBlock(camera, position.x, position.y, position.z, 1, 0, 1, settingColor.getColor());
            RenderUtil.drawOutlineBlock(camera, position.x, position.y, position.z, 1, 0, 1, settingLineSize.getValue().floatValue(), settingColor.getColor(settingLineAlpha.getValue().intValue()));
        }
    }
}
