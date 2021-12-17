package rina.onepop.club.client.module.combat.autocity;

import rina.onepop.club.Onepop;
import rina.onepop.club.api.module.Module;
import rina.onepop.club.api.module.impl.ModuleCategory;
import rina.onepop.club.api.module.registry.Registry;
import rina.onepop.club.api.setting.value.ValueBoolean;
import rina.onepop.club.api.setting.value.ValueEnum;
import rina.onepop.club.api.setting.value.ValueNumber;
import rina.onepop.club.api.util.chat.ChatUtil;
import rina.onepop.club.api.util.client.NullUtil;
import rina.onepop.club.api.util.entity.EntityUtil;
import rina.onepop.club.api.util.render.RenderUtil;
import rina.onepop.club.client.event.client.RunTickEvent;
import rina.onepop.club.client.event.network.PacketEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.opengl.GL11;
import rina.onepop.club.api.ISLClass;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

import java.awt.*;

@Registry(name = "Auto-City", tag = "AutoCity", description = "Mines the city of your enemy", category = ModuleCategory.COMBAT)
public class ModuleAutoCity extends Module {

    ValueEnum breakMode = new ValueEnum("Break Mode", "breakMode", "How it breaks blocks", BreakModes.PacketMine);
    ValueBoolean burrowMine = new ValueBoolean("Mine Burrow", "mineBurrow", "Mines the block the player is burrowed in", true);
    ValueNumber targetRange = new ValueNumber("Target Range", "targetRange", "Range for targeting", 7d, 2d, 10d);
    ValueNumber breakRange = new ValueNumber("Break Range", "breakRange", "Range for breaking", 7d, 2d, 10d);
    ValueBoolean oneThirteen = new ValueBoolean("One Thirteen", "oneThirteen", "Allows crystal placements in a 1x1x1", false);
    ValueBoolean antiNaked = new ValueBoolean("Anti Naked", "antiNaked", "Doesnt target nakeds", true);

    ValueBoolean renderRGB = new ValueBoolean("RGB", "rgb", "Rainbow", false);
    ValueNumber renderRed = new ValueNumber("Render Red", "renderRed", "Red render value", 150, 0, 255);
    ValueNumber renderGreen = new ValueNumber("Render Green", "renderGreen", "Green render value", 150, 0, 255);
    ValueNumber renderBlue = new ValueNumber("Render Blue", "renderBlue", "Blue render value", 150, 0, 255);
    ValueNumber renderAlpha = new ValueNumber("Render Alpha", "renderAlpha", "Alpha render value", 150, 0, 255);
    ValueNumber lineAlpha = new ValueNumber("Line Alpha", "lineAlpha", "Render outline alpha", 150, 0, 255);
    ValueNumber lineWidth = new ValueNumber("Line Width", "lineWidth", "Render outline width", 1, 0, 3);

    BlockPos mining;
    BlockPos minedAt;
    EntityPlayer target;
    boolean packetCancel = false;

    @Override
    public void onSetting() {
        super.onSetting();
        if (renderRGB.getValue()) {
            renderRed.setValue(Onepop.getClientEventManager().getCurrentRGBColor()[0]);
            renderGreen.setValue(Onepop.getClientEventManager().getCurrentRGBColor()[1]);
            renderBlue.setValue(Onepop.getClientEventManager().getCurrentRGBColor()[2]);
        }

    }

    @Override
    public void onDisable() {
        super.onDisable();
        mining = null;
        minedAt = null;
    }

    @Listener
    public void packetListener(PacketEvent packetEvent){
        if (packetEvent.getPacket() instanceof CPacketPlayerDigging){
            if (((CPacketPlayerDigging) packetEvent.getPacket()).getAction() == CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK && packetCancel) packetEvent.setCanceled(true);
        }
    }

    @Listener
    public void tickListener(RunTickEvent event){
        target = EntityUtil.getTarget(targetRange.getValue().floatValue(), false, antiNaked.getValue());
        if (target == null) return;


        mining = getMiningBlock();

        if (mining == null) return;

        if (breakMode.getValue() == BreakModes.PacketMine){
            if (!comparePos(mining, minedAt)) {
                ISLClass.mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, mining, EnumFacing.UP));
                ISLClass.mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, minedAt, EnumFacing.UP));
                ChatUtil.print("Starting to break block");
                minedAt = mining;
            }
        } else if (breakMode.getValue() == BreakModes.InstantMine){
            if (!comparePos(mining, minedAt)) {
                packetCancel = false;
                ISLClass.mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, mining, EnumFacing.UP));
                ChatUtil.print("Starting to break block");
                packetCancel = true;
            } else {
                packetCancel = true;
            }
        }
    }

    BlockPos getMiningBlock() {
        BlockPos theBlock = null;
        final BlockPos targetPosition = new BlockPos(Math.floor(this.target.posX), Math.floor(this.target.posY), Math.floor(this.target.posZ));

        for (EnumFacing facing : EnumFacing.HORIZONTALS) {
            BlockPos added = targetPosition.add(facing.getXOffset(), facing.getYOffset(), facing.getZOffset());
            if (ISLClass.mc.world.getBlockState(added).getBlockHardness(ISLClass.mc.world, added) != -1 && ISLClass.mc.player.getDistance(added.getX(), added.getY(), added.getZ()) <= breakRange.getValue().doubleValue() && !ISLClass.mc.world.getBlockState(added).getMaterial().isReplaceable()) {
                if (oneThirteen.getValue()) {
                    return added;
                } else {
                    BlockPos addedSecond = targetPosition.add(facing.getXOffset() * 2, facing.getYOffset() * 2, facing.getZOffset() * 2);
                    if (ISLClass.mc.world.getBlockState(added.add(0, 1, 0)).getBlock() == Blocks.AIR){
                        return added;
                    } else {
                        if (ISLClass.mc.world.getBlockState(addedSecond).getBlock() == Blocks.AIR && ISLClass.mc.world.getBlockState(addedSecond.add(0, 1, 0)).getBlock() == Blocks.AIR){
                            return added;
                        }
                    }
                }
            }
        }

        if (burrowMine.getValue() && ISLClass.mc.world.getBlockState(targetPosition).getBlockHardness(ISLClass.mc.world, targetPosition) != -1 && ISLClass.mc.player.getDistance(targetPosition.getX(), targetPosition.getY(), targetPosition.getZ()) <= breakRange.getValue().doubleValue() && !ISLClass.mc.world.getBlockState(targetPosition).getMaterial().isReplaceable()) return targetPosition;

        return null;
    }

    @Override
    public void onRender3D() {
        super.onRender3D();
        if (NullUtil.isPlayerWorld() || target == null || mining == null) {
            return;
        }

        Color solid = new Color(renderRed.getValue().intValue(), renderGreen.getValue().intValue(), renderBlue.getValue().intValue(), renderAlpha.getValue().intValue());
        Color outline = new Color(renderRed.getValue().intValue(), renderGreen.getValue().intValue(), renderBlue.getValue().intValue(), lineAlpha.getValue().intValue());

        RenderUtil.drawSolidBlock(camera, this.mining.getX(), this.mining.getY(), this.mining.getZ(), 1, 1, 1, solid);
        GL11.glLineWidth(lineWidth.getValue().floatValue());
        RenderUtil.drawOutlineBlock(camera, this.mining.getX(), this.mining.getY(), this.mining.getZ(), 1, 1, 1, outline);
    }

    boolean comparePos(BlockPos pos1, BlockPos pos2){
        if (pos1 == null || pos2 == null) return false;
        return pos1.getX() == pos2.getX() && pos1.getY() == pos2.getY()&& pos1.getZ() == pos2.getZ();
    }
}
