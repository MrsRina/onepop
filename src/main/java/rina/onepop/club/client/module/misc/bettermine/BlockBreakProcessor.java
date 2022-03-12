package rina.onepop.club.client.module.misc.bettermine;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

/**
 * @author SrRina
 * @since 15/01/2022 at 23:43
 **/
public class BlockBreakProcessor {
    private static final Minecraft mc = Minecraft.getMinecraft();
    private final ModuleBetterMine master;

    private Block breakingBlockType;
    private BlockPos breakingBlockPosition;
    private EnumFacing breakingBlockFacing;

    protected boolean firstPacketSent;
    protected boolean requestNewPacket;

    protected int amountTicks;

    public BlockBreakProcessor(ModuleBetterMine master) {
        this.master = master;
    }

    public ModuleBetterMine getMaster() {
        return master;
    }

    public void setBreakingBlockPosition(BlockPos breakingBlockPosition) {
        this.breakingBlockPosition = breakingBlockPosition;
    }

    public BlockPos getBreakingBlockPosition() {
        return breakingBlockPosition;
    }

    public void setBreakingBlockType(Block breakingBlockType) {
        this.breakingBlockType = breakingBlockType;
    }

    public Block getBreakingBlockType() {
        return breakingBlockType;
    }

    public void setBreakingBlockFacing(EnumFacing breakingBlockFacing) {
        this.breakingBlockFacing = breakingBlockFacing;
    }

    public EnumFacing getBreakingBlockFacing() {
        return breakingBlockFacing;
    }

    public boolean isPreventNullable() {
        return mc.player == null || mc.world == null || this.getBreakingBlockPosition() == null || this.getBreakingBlockType() == null;
    }

    public void setBreaking(BlockPos position, Block block, EnumFacing facing) {
        this.setBreakingBlockPosition(position);
        this.setBreakingBlockType(block);
        this.setBreakingBlockFacing(facing);

        this.firstPacketSent = false;
        this.requestNewPacket = false;
    }

    public void onUpdate() {
        if (this.isPreventNullable()) {
            return;
        }

        if (!this.firstPacketSent || this.requestNewPacket) {
            if (this.breakingBlockFacing == null) {
                this.breakingBlockFacing = EnumFacing.getDirectionFromEntityLiving(this.getBreakingBlockPosition(), mc.player);
            }

            mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, this.getBreakingBlockPosition(), this.getBreakingBlockFacing()));
            mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, this.getBreakingBlockPosition(), this.getBreakingBlockFacing()));

            this.firstPacketSent = true;
            this.requestNewPacket = false;
        }

        if (this.amountTicks >= ModuleBetterMine.settingOffTicksInteger.getValue().intValue()) {
            this.requestNewPacket = true;
            this.amountTicks = 0;
        }

        this.amountTicks++;
    }
}
