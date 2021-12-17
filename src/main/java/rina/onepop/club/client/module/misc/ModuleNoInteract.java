package rina.onepop.club.client.module.misc;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.math.BlockPos;
import rina.onepop.club.api.module.Module;
import rina.onepop.club.api.module.impl.ModuleCategory;
import rina.onepop.club.api.module.registry.Registry;
import rina.onepop.club.api.setting.value.ValueBoolean;
import rina.onepop.club.api.util.world.BlockUtil;
import rina.onepop.club.client.event.client.RunTickEvent;
import rina.onepop.club.client.event.network.PacketEvent;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

/**
 * @author SrRina
 * @since 26/06/2021 at 02:52
 **/
@Registry(name = "No Interact", tag = "NoInteract", description = "Prevents click on blocks!", category = ModuleCategory.MISC)
public class ModuleNoInteract extends Module {
    // Misc.
    public static ValueBoolean settingEnchest = new ValueBoolean("Enchest", "Enchest", "Anti-click for enchest!", true);
    public static ValueBoolean settingShulker = new ValueBoolean("Shulker", "Shulker", "Anti-click for shulker!", true);
    public static ValueBoolean settingChest = new ValueBoolean("Chest", "Chest", "Anti-click for chests!", false);
    public static ValueBoolean settingAnvil = new ValueBoolean("Anvil", "Anvil", "Anti-click for anvils!", true);
    public static ValueBoolean settingHopper = new ValueBoolean("Hopper", "Hopper", "Anti-click for hopper!", true);
    public static ValueBoolean settingDispenser = new ValueBoolean("Dispenser", "Dispenser", "Anti-click for dispenser!", true);
    public static ValueBoolean settingDropper = new ValueBoolean("Dropper", "Dropper", "Anti-click for dropper!", true);

    @Listener
    public void onTick(PacketEvent.Send event) {
        if (event.getPacket() instanceof CPacketPlayerTryUseItemOnBlock && this.checkType(((CPacketPlayerTryUseItemOnBlock) event.getPacket()).getPos())) {
            event.setCanceled(true);
        }
    }

    public boolean checkType(final BlockPos position) {
        final Block block = BlockUtil.getBlock(position);

        return (block == Blocks.ENDER_CHEST && settingEnchest.getValue())
                || (block == Blocks.CHEST && settingChest.getValue())
                || (block == Blocks.ANVIL && settingAnvil.getValue())
                || (block == Blocks.HOPPER && settingHopper.getValue())
                || (block == Blocks.DISPENSER && settingDispenser.getValue())
                || (block == Blocks.DROPPER && settingDropper.getValue()
                || (BlockUtil.SHULKER_LIST.contains(block) && settingShulker.getValue()));
    }
}
