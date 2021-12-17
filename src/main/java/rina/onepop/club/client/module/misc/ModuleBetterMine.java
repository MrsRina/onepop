package rina.onepop.club.client.module.misc;

import me.rina.turok.util.TurokTick;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import rina.onepop.club.api.module.Module;
import rina.onepop.club.api.module.impl.ModuleCategory;
import rina.onepop.club.api.module.registry.Registry;
import rina.onepop.club.api.setting.value.ValueBoolean;
import rina.onepop.club.api.setting.value.ValueColor;
import rina.onepop.club.api.setting.value.ValueNumber;
import rina.onepop.club.api.util.client.NullUtil;
import rina.onepop.club.api.util.item.SlotUtil;
import rina.onepop.club.api.util.math.PositionUtil;
import rina.onepop.club.api.util.render.RenderUtil;
import rina.onepop.club.api.util.world.BlockUtil;
import rina.onepop.club.client.event.client.RunTickEvent;
import rina.onepop.club.client.event.entity.PlayerDamageBlockEvent;
import rina.onepop.club.client.manager.network.HotBarManager;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author SrRina
 * @since 27/02/2021 at 15:26
 **/
@Registry(name = "Better Mine", tag = "BetterMine", description = "Make your mining very better!", category = ModuleCategory.MISC)
public class ModuleBetterMine extends Module {
    public static ModuleBetterMine INSTANCE;

    /* Misc. */
    public static ValueBoolean settingPerfectTiming = new ValueBoolean("Perfect Timing", "PerfectTiming", "Sets pickaxe at perfect time for break more fast!", false);
    public static ValueBoolean settingInstantMine = new ValueBoolean("Instant", "Instant", "Instant mine!", true);
    public static ValueNumber settingQueueSize = new ValueNumber("Size", "Size", "Maximum queue size!", 4, 2, 10);

    /* Color. */
    public static ValueColor settingBreakingColor = new ValueColor("Breaking", "Breaking", "Sets breaking color.", true, Color.RED);
    public static ValueColor settingBreakColor = new ValueColor("Break", "Break", "Sets done break color.", true, Color.GREEN);
    public static ValueNumber settingLineAlpha = new ValueNumber("Line Alpha", "LineDamageAlpha", "The alpha of current block damage!", 255, 0, 255);
    public static ValueNumber settingLineSize = new ValueNumber("Line Size", "LineSize", "LineSize", 1f, 1f, 5f);

    private BlockPos currentBlock;
    private EnumFacing currentEnumFacing;

    private boolean isBreaking;
    private boolean isSwitched;
    private boolean isCancelled;

    private final List<BlockUtil.BlockDamage> blockList = new ArrayList<>();
    private final TurokTick timerBreak = new TurokTick();

    private float hardnessBreak;

    public ModuleBetterMine() {
        INSTANCE = this;
    }

    @Override
    public void onSetting() {
        settingInstantMine.setEnabled(false);

        settingLineAlpha.setEnabled(settingBreakColor.getValue() || settingBreakingColor.getValue());
        settingLineSize.setEnabled(settingBreakColor.getValue() || settingBreakingColor.getValue());
    }

    @Override
    public void onRender3D() {
        if (NullUtil.isPlayerWorld()) {
            return;
        }

        if (mc.player.isCreative()) {
            return;
        }

        for (BlockUtil.BlockDamage blocksDamage : this.blockList) {
            final BlockPos position = blocksDamage.getPos();
            final Block block = BlockUtil.getBlock(position);

            if (block == Blocks.AIR || this.currentBlock == null) {
                continue;
            }

            if (PositionUtil.collideBlockPos(position, this.currentBlock)) {
                if (this.timerBreak.isPassedMS(this.hardnessBreak)) {
                    if (settingBreakColor.getValue()) {
                        RenderUtil.drawSolidBlock(camera, position, settingBreakColor.getColor());
                        RenderUtil.drawOutlineBlock(camera, position, settingLineSize.getValue().floatValue(), settingBreakColor.getColor(settingLineAlpha.getValue().intValue()));
                    }
                } else {
                    if (settingBreakingColor.getValue()) {
                        RenderUtil.drawSolidBlock(camera, position, settingBreakingColor.getColor());
                        RenderUtil.drawOutlineBlock(camera, position, settingLineSize.getValue().floatValue(), settingBreakingColor.getColor(settingLineAlpha.getValue().intValue()));
                    }
                }
            } else {
                if (settingBreakingColor.getValue()) {
                    RenderUtil.drawSolidBlock(camera, position, settingBreakingColor.getColor());
                    RenderUtil.drawOutlineBlock(camera, position, settingLineSize.getValue().floatValue(), settingBreakingColor.getColor(settingLineAlpha.getValue().intValue()));
                }
            }
        }
    }

    @Listener
    public void onDamageBlock(PlayerDamageBlockEvent event) {
        final float blockHardness = BlockUtil.getHardness(event.getPos());

        if (blockHardness == -1 || BlockUtil.getBlock(event.getPos()) == Blocks.WEB) {
            return;
        }

        final BlockUtil.BlockDamage damage = new BlockUtil.BlockDamage(event.getPos(), event.getFacing());

        if (this.containsBlockDamage(damage.getPos()) || this.blockList.size() >= settingQueueSize.getValue().intValue()) {
            return;
        }

        this.add(damage);
    }

    @Listener
    public void onTick(RunTickEvent event) {
        if (NullUtil.isPlayerWorld()) {
            return;
        }

        if (mc.player.isCreative()) {
            return;
        }

        if (!this.blockList.isEmpty()) {
            BlockUtil.BlockDamage blockDamage = this.blockList.get(0);

            final BlockPos position = blockDamage.getPos();
            final Block block = BlockUtil.getBlock(position);

            final float distance = (float) mc.player.getDistance(position.x, position.y, position.z);
            final boolean flagBreak = block == Blocks.AIR;

            this.isBreaking = true;

            if (distance > 13f || flagBreak) {
                this.doSync();
            }

            this.hardnessBreak = this.calculateTimeBreak(position);

            if (this.timerBreak.isPassedMS(this.hardnessBreak) && settingPerfectTiming.getValue() && distance <= mc.playerController.getBlockReachDistance() && !this.isSwitched) {
                int slot = this.findFirstItemPickaxe();

                if (!(SlotUtil.getItem(HotBarManager.currentItem(HotBarManager.SERVER)) instanceof ItemPickaxe) && slot != -1) {
                    SlotUtil.setServerCurrentItem(slot);

                    this.isSwitched = true;
                }
            }

            if (this.isBreaking) {
                this.currentBlock = position;
                this.currentEnumFacing = blockDamage.getFacing();

                mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, position, blockDamage.getFacing()));
                mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, position, blockDamage.getFacing()));
            }
        } else {
            this.doSwitchSync();
            this.doSyncFlag();
        }
    }

    public void queue(BlockPos position, EnumFacing facing) {
        this.add(new BlockUtil.BlockDamage(position, facing));
    }

    public void add(BlockUtil.BlockDamage block) {
        this.blockList.add(block);
    }

    public void doSwitchSync() {
        if (settingPerfectTiming.getValue() && this.isSwitched) {
            final int server = HotBarManager.currentItem(HotBarManager.SERVER);
            final int client = HotBarManager.currentItem(HotBarManager.CLIENT);

            if (server != client && this.isSwitched) {
                SlotUtil.setServerCurrentItem(client);

                this.print("Sync event: HAPPENED");

                if (mc.player.isHandActive()) {
                    mc.player.stopActiveHand();
                }
            }

            this.isSwitched = false;
        }

        this.timerBreak.reset();
    }

    public void doSyncFlag() {
        this.isBreaking = false;
    }

    public void doClear() {
        this.blockList.clear();
    }

    public void doSync() {
        if (!this.blockList.isEmpty()) {
            this.doSyncFlag();
            this.doSwitchSync();

            this.blockList.remove(0);
        }
    }

    public float calculateTimeBreak(BlockPos position) {
        float math = BlockUtil.getHardness(position) * 10f;

        return math * 4f;
    }

    public int findFirstItemPickaxe() {
        int slot = -1;

        for (int i = 0; i < 9; i++) {
            Item item = SlotUtil.getItem(i);

            if (item instanceof ItemPickaxe) {
                slot = i;

                break;
            }
        }

        return slot;
    }

    public boolean containsBlockDamage(BlockPos position) {
        boolean contains = false;

        for (BlockUtil.BlockDamage blocksDamage : this.blockList) {
            if (PositionUtil.collideBlockPos(blocksDamage.getPos(), position)) {
                contains = true;

                break;
            }
        }

        return contains;
    }
}