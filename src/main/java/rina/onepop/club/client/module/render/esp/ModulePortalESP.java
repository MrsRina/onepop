package rina.onepop.club.client.module.render.esp;

import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import rina.onepop.club.api.module.Module;
import rina.onepop.club.api.module.impl.ModuleCategory;
import rina.onepop.club.api.module.registry.Registry;
import rina.onepop.club.api.setting.value.ValueNumber;
import rina.onepop.club.api.util.client.NullUtil;
import rina.onepop.club.api.util.render.RenderUtil;
import rina.onepop.club.api.util.world.BlockUtil;
import rina.onepop.club.api.util.world.BlocksUtil;
import rina.onepop.club.client.event.client.ClientTickEvent;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Registry(name = "Portal ESP", tag = "PortalESP", description = "Render portals close of you.", category = ModuleCategory.RENDER)
public class ModulePortalESP extends Module {
    public static ValueNumber settingRange = new ValueNumber("Range x10", "Range", "Range...", 50, 0, 250);

    private int missingTicks;
    private final List<BlockPos> portalBlocksToRenderList = new ArrayList<>();

    @Override
    public void onRender3D() {
        if (NullUtil.isPlayerWorld()) {
            return;
        }

        for (BlockPos portals : this.portalBlocksToRenderList) {
            RenderUtil.drawSolidBlock(camera, portals, new Color(255, 0, 255, 100));
            RenderUtil.drawOutlineBlock(camera, portals, 1f, new Color(255, 0, 255, 200));
        }
    }

    @Listener
    public void onClientTickEvent(ClientTickEvent event) {
        if (NullUtil.isPlayerWorld()) {
            return;
        }

        if (this.missingTicks >= 80) {
            this.portalBlocksToRenderList.clear();

            for (BlockPos blocks : BlocksUtil.getSphereList(settingRange.getValue().intValue() * 10)) {
                if (BlockUtil.getBlock(blocks) == Blocks.PORTAL) {
                    this.portalBlocksToRenderList.add(blocks);
                }

            }

            this.missingTicks = 0;
        }
    }
}
