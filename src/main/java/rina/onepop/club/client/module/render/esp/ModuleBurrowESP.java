package rina.onepop.club.client.module.render.esp;

import rina.onepop.club.api.module.Module;
import rina.onepop.club.api.module.impl.ModuleCategory;
import rina.onepop.club.api.module.registry.Registry;
import rina.onepop.club.api.setting.value.ValueBoolean;
import rina.onepop.club.api.setting.value.ValueColor;
import rina.onepop.club.api.setting.value.ValueNumber;
import rina.onepop.club.api.util.client.NullUtil;
import rina.onepop.club.api.util.render.RenderUtil;
import rina.onepop.club.api.util.world.BlockUtil;
import rina.onepop.club.client.event.client.RunTickEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author SrRina
 * @since 23/04/2021 at 19:13
 **/
@Registry(name = "Burrow ESP", tag = "BurrowESP", description = "ESP 3vt.", category = ModuleCategory.RENDER)
public class ModuleBurrowESP extends Module {
    /* Misc. */
    public static ValueBoolean settingMessage = new ValueBoolean("Message", "Message", "Message if an player uses burrow!", true);

    /* Color. */
    public static ValueColor settingColor = new ValueColor("Color", "Color", "Sets color", new Color(190, 190, 0, 100));
    public static ValueNumber settingLineSize = new ValueNumber("Line Size", "LineSize", "Sets line size.", 1.0f, 1f, 3.0f);
    public static ValueNumber settingLineAlpha = new ValueNumber("Line Alpha", "LineAlpha", "Sets line alpha color.", 255, 0, 255);

    private final List<BlockPos> confirmList = new ArrayList<>();

    Color solid = Color.YELLOW;
    Color outline = Color.YELLOW;

    @Override
    public void onSetting() {
        this.solid = settingColor.getColor();
        this.outline = settingColor.getColor(settingLineAlpha.getValue().intValue());
    }

    @Override
    public void onRender3D() {
        if (NullUtil.isPlayerWorld()) {
            return;
        }

        for (EntityPlayer entities : mc.world.playerEntities) {
            if (mc.player.getDistance(entities) > 6f || mc.player == entities) {
                continue;
            }

            BlockPos targetPosition = new BlockPos(Math.floor(entities.posX), Math.floor(entities.posY + 0.5f), Math.floor(entities.posZ));
            boolean targetFlag = !BlockUtil.isAir(targetPosition) && (BlockUtil.getBlock(targetPosition) == Blocks.OBSIDIAN || BlockUtil.getBlock(targetPosition) == Blocks.ANVIL || BlockUtil.getBlock(targetPosition) == Blocks.ENDER_CHEST);

            if (targetFlag) {
                RenderUtil.drawSolidBlock(camera, targetPosition, this.solid);
                RenderUtil.drawOutlineBlock(camera, targetPosition, settingLineSize.getValue().floatValue(), this.outline);
            }
        }
    }

    @Listener
    public void onTick(RunTickEvent event) {
        if (NullUtil.isPlayerWorld()) {
            return;
        }

        if (!settingMessage.getValue()) {
            this.confirmList.clear();

            return;
        }

        for (EntityPlayer entities : mc.world.playerEntities) {
            if (mc.player.getDistance(entities) > 6f || mc.player == entities) {
                continue;
            }

            BlockPos targetPosition = new BlockPos(Math.floor(entities.posX), Math.floor(entities.posY + 0.5f), Math.floor(entities.posZ));
            boolean targetFlag = !BlockUtil.isAir(targetPosition) && (BlockUtil.getBlock(targetPosition) == Blocks.OBSIDIAN || BlockUtil.getBlock(targetPosition) == Blocks.ANVIL || BlockUtil.getBlock(targetPosition) == Blocks.ENDER_CHEST);

            if (targetFlag) {
                if (!this.confirmList.contains(targetPosition)) {
                    this.print(entities.getName() + " burrowed!");

                    this.confirmList.add(targetPosition);
                }
            } else {
                this.confirmList.remove(targetPosition);
            }
        }
    }
}
