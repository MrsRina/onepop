package rina.onepop.club.client.module.render.esp;

import net.minecraft.util.math.BlockPos;
import rina.onepop.club.api.module.Module;
import rina.onepop.club.api.module.impl.ModuleCategory;
import rina.onepop.club.api.module.registry.Registry;
import rina.onepop.club.api.setting.value.ValueColor;
import rina.onepop.club.api.setting.value.ValueNumber;
import rina.onepop.club.api.util.client.NullUtil;
import rina.onepop.club.api.util.render.RenderUtil;
import rina.onepop.club.api.util.world.BlockUtil;
import rina.onepop.club.client.event.client.RunTickEvent;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author SrRina
 * @since 05/10/2021 at 19:08
 **/
@Registry(name = "Void ESP", tag = "VoidESP", description = "Draws void close of you.", category = ModuleCategory.RENDER)
public class ModuleVoidESP extends Module {
    // Misc.
    public static ValueNumber settingRange = new ValueNumber("Range", "Range", "Range for render.", 10, 4, 16);
    public static ValueColor settingColor = new ValueColor("Color", "Color", "Color of void.", Color.pink);
    public static ValueNumber settingLineSize = new ValueNumber("Line Size", "LineSize", "Line size.", 1f, 1f, 5f);
    public static ValueNumber settingLineAlpha = new ValueNumber("Line Alpha", "LineAlpha", "Sets line alpha.", 255, 0, 255);

    private final List<BlockPos> renderList = new ArrayList<>();

    @Override
    public void onRender3D() {
        if (NullUtil.isPlayerWorld()) {
            return;
        }

        for (BlockPos position : this.renderList) {
            RenderUtil.drawSolidBlock(camera, position.x, position.y, position.z, 1f, 0, 1f, settingColor.getColor());
            RenderUtil.drawOutlineBlock(camera, position.x, position.y, position.z, 1f, 0f, 1f, settingLineSize.getValue().floatValue(), settingColor.getColor(settingLineAlpha.getValue().intValue()));
        }
    }

    @Listener
    public void onRunTickEvent(RunTickEvent event) {
        if (NullUtil.isPlayerWorld()) {
            return;
        }

        this.renderList.clear();

        int range = settingRange.getValue().intValue();

        int posX = (int) mc.player.posX;
        int posZ = (int) mc.player.posZ;

        for (int x = posX - range; x < posX + range; x++) {
            for (int z = posZ - range; z < posX + range; z++) {
                final BlockPos pos = new BlockPos(posX, 1, posZ);

                if (BlockUtil.isAir(pos)) {
                    this.renderList.add(pos);
                }
            }
        }
    }
}
