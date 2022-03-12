package rina.onepop.club.client.module.misc.bettermine;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import rina.onepop.club.api.module.Module;
import rina.onepop.club.api.module.impl.ModuleCategory;
import rina.onepop.club.api.module.registry.Registry;
import rina.onepop.club.api.setting.value.ValueColor;
import rina.onepop.club.api.setting.value.ValueNumber;
import rina.onepop.club.api.util.render.RenderUtil;
import rina.onepop.club.client.event.client.RunTickEvent;
import rina.onepop.club.client.event.entity.PlayerDamageBlockEvent;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

import java.awt.*;

/**
 * @author SrRina
 * @since 15/01/2022 at 23:41
 **/
@Registry(name = "Better Mine", tag = "BetterMine", description = "Improve mining at game.", category = ModuleCategory.MISC)
public class ModuleBetterMine extends Module {
    public static ModuleBetterMine INSTANCE;

    /* Misc */
    public static ValueNumber settingOffTicksInteger = new ValueNumber("Off Ticks", "OffTicks", "Ticks you do not send packets.", 23, 0, 100);
    public static ValueNumber settingUpdateDistance = new ValueNumber("Distance", "Distance", "Distance to stop break.", 4.5, 2.0, 6.0);
    public static ValueColor settingQueueColor = new ValueColor("Queue", "Queue", "Sets queue color.", Color.red);
    public static ValueColor settingBreakColor = new ValueColor("Break", "Break", "Sets break color.", Color.green);

    /*
     * Processor to break block.
     */
    protected BlockBreakProcessor processor;

    /*
     * Add blocks in a queue and adorable manage them.
     */
    protected BlockEventCollector collector;

    public ModuleBetterMine() {
        INSTANCE = this;
        this.onInit();
    }

    public void onInit() {
        this.processor = new BlockBreakProcessor(this);
        this.collector = new BlockEventCollector(this);
    }

    public BlockEventCollector getCollector() {
        return collector;
    }

    @Listener
    public void onBlockDamageEvent(PlayerDamageBlockEvent event) {
        IBlockState state = mc.world.getBlockState(event.getPos());
        Block block = state.getBlock();

        float hardness = state.getBlockHardness(mc.world, event.getPos());

        if (hardness == -1f || block == Blocks.WEB || block == Blocks.AIR) {
            return;
        }

        this.collector.add(event);
    }

    @Override
    public void onRender3D() {
        if (mc.player == null || mc.world == null || this.collector.getCurrentEvent() == null) {
            return;
        }

        for (PlayerDamageBlockEvent event : this.collector.getQueue()) {
            if (mc.world.getBlockState(event.getPos()).getBlock() == Blocks.AIR) {
                continue;
            }

            if (event == this.collector.getCurrentEvent()) {
                RenderUtil.drawSolidBlock(camera, event.getPos(), settingBreakColor.getColor());
            } else {
                RenderUtil.drawSolidBlock(camera, event.getPos(), settingQueueColor.getColor());
            }
        }
    }

    @Listener
    public void onTick(RunTickEvent event) {
        if (mc.player == null || mc.world == null) {
            return;
        }

        if (this.collector.getCurrentEvent() != null && (this.processor.getBreakingBlockPosition() == null || !this.collector.contains(this.processor.getBreakingBlockPosition()))) {
            this.processor.setBreaking(this.collector.getCurrentEvent().getPos(), mc.world.getBlockState(this.collector.getCurrentEvent().getPos()).getBlock(), this.collector.getCurrentEvent().getFacing());
        }

        this.processor.onUpdate();
        this.collector.onUpdate();
    }
}