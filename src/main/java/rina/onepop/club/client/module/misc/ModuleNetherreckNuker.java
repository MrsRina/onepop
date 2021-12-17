package rina.onepop.club.client.module.misc;

import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import rina.onepop.club.api.module.Module;
import rina.onepop.club.api.module.impl.ModuleCategory;
import rina.onepop.club.api.module.registry.Registry;
import rina.onepop.club.api.setting.value.ValueBoolean;
import rina.onepop.club.api.setting.value.ValueColor;
import rina.onepop.club.api.setting.value.ValueNumber;
import rina.onepop.club.api.util.client.NullUtil;
import rina.onepop.club.api.util.render.RenderUtil;
import rina.onepop.club.api.util.world.BlockUtil;
import rina.onepop.club.api.util.world.BlocksUtil;
import rina.onepop.club.client.event.client.RunTickEvent;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Rina
 * @since 11/10/2021 at 01:49pm
 **/
@Registry(name = "Netherreck Nuker", tag = "NetherreckNuker", description = "Break all netherrack blocks around you.", category = ModuleCategory.MISC)
public class ModuleNetherreckNuker extends Module {
    // Misc.
    public static ValueNumber settingRange = new ValueNumber("Range", "Range", "Range for break nether blocks.", 5f, 2f, 6f);
    public static ValueBoolean settingInstant = new ValueBoolean("Instant", "Instant", "Instant break.", true);
    public static ValueColor settingColor = new ValueColor("Color", "Color", "Color.", Color.black);
    public static ValueNumber settingLineAlpha = new ValueNumber("Line Alpha", "LineAlpha", "Sets line alpha.", 255, 0, 255);
    public static ValueNumber settingLineSize = new ValueNumber("Line Size", "LineSize", "Line size.", 1f, 1f, 5f);

    private final List<BlockPos> netherrackList = new ArrayList<>();
    private final List<BlockPos> blackList = new ArrayList<>();

    private int lastTick;
    private int lastTickList;

    @Override
    public void onRender3D() {
        if (NullUtil.isPlayerWorld()) {
            return;
        }

        if (settingInstant.getValue()) {
            this.update();
        }

        for (BlockPos netherracks : this.netherrackList) {
            RenderUtil.drawSolidBlock(camera, netherracks, settingColor.getColor());
            RenderUtil.drawOutlineBlock(camera, netherracks, settingLineSize.getMaximum().floatValue(), settingColor.getColor(settingLineAlpha.getMaximum().intValue()));
        }
    }

    @Listener
    public void onRunTickEvent(RunTickEvent event) {
        if (NullUtil.isPlayerWorld()) {
            return;
        }

        if (!settingInstant.getValue()) {
            this.update();
        }
    }

    public void update() {
        if (this.lastTickList >= this.blackList.size()) {
            this.blackList.clear();
            this.lastTickList = 0;
        }

        this.lastTickList++;

        if (this.lastTick >= 1) {
            this.netherrackList.clear();
            this.findBlocks();

            try {
                for (BlockPos netherracks : this.netherrackList) {
                    if (netherracks == null) {
                        continue;
                    }

                    EnumFacing facing = EnumFacing.UP;

                    int old = mc.player.inventory.currentItem;
                    int slot = this.findFirstPickaxeSlot();

                    if (slot != -1) {
                        mc.player.inventory.currentItem = slot;
                        mc.playerController.updateController();
                    }

                    mc.playerController.onPlayerDamageBlock(netherracks, facing);
                    mc.player.swingArm(EnumHand.MAIN_HAND);

                    mc.player.inventory.currentItem = old;
                    mc.playerController.updateController();

                    this.blackList.add(netherracks);
                    this.lastTick = 0;

                    break;
                }
            } catch (Exception exc) {}
        }

        this.lastTick++;
    }

    public void findBlocks() {
        for (BlockPos blocks : BlocksUtil.getSphereList(settingRange.getValue().floatValue())) {
            if (BlockUtil.getBlock(blocks) == Blocks.NETHERRACK && !this.blackList.contains(blocks)) {
                this.netherrackList.add(blocks);
            }
        }
    }

    public int findFirstPickaxeSlot() {
        int slot = -1;

        for (int i = 0; i < 9; i++) {
            Item item = mc.player.inventory.getStackInSlot(i).getItem();

            if (item instanceof ItemPickaxe) {
                slot = i;

                break;
            }
        }

        return slot;
    }
}
