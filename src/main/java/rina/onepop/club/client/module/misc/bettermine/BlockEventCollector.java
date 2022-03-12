package rina.onepop.club.client.module.misc.bettermine;

import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import rina.onepop.club.client.event.entity.PlayerDamageBlockEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * @author SrRina
 * @since 16/01/2022 at 00:03
 **/
public class BlockEventCollector {
    private static final Minecraft mc = Minecraft.getMinecraft();
    private final ModuleBetterMine master;

    private PlayerDamageBlockEvent lastEvent;
    private PlayerDamageBlockEvent currentEvent;

    /*
     * We use a list to queue mode.
     */
    protected List<PlayerDamageBlockEvent> queue;

    public BlockEventCollector(ModuleBetterMine master) {
        this.master = master;
        this.queue = new ArrayList<>();
    }

    public ModuleBetterMine getMaster() {
        return master;
    }

    public List<PlayerDamageBlockEvent> getQueue() {
        return queue;
    }

    public boolean isPreventNullable() {
        return mc.player == null || mc.world == null;
    }

    public void setCurrentEvent(PlayerDamageBlockEvent currentEvent) {
        this.currentEvent = currentEvent;
    }

    public PlayerDamageBlockEvent getCurrentEvent() {
        return currentEvent;
    }

    public void setLastEvent(PlayerDamageBlockEvent lastEvent) {
        this.lastEvent = lastEvent;
    }

    public PlayerDamageBlockEvent getLastEvent() {
        return lastEvent;
    }

    public boolean contains(BlockPos pos) {
        boolean contains = false;

        for (PlayerDamageBlockEvent events : this.queue) {
            final BlockPos position = events.getPos();

            if (position.x == pos.x && position.y == pos.y && position.z == pos.z) {
                contains = true;

                break;
            }
        }

        return contains;
    }

    public void add(PlayerDamageBlockEvent event) {
        if (this.contains(event.getPos())) {
            return;
        }

        this.queue.add(event);
    }

    public void onUpdate() {
        if (this.isPreventNullable()) {
            return;
        }

        if (!this.queue.isEmpty()) {
            this.setCurrentEvent(this.queue.get(0));

            // Variable check.
            boolean IsNonExistentOrDistant = mc.world.getBlockState(this.queue.get(0).getPos()).getBlock() == Blocks.AIR || this.isDistantFromPlayer(this.queue.get(0).getPos());

            if (IsNonExistentOrDistant) {
                this.setLastEvent(this.queue.get(0));
                this.queue.remove(0);
            }
        }
    }

    public boolean isDistantFromPlayer(BlockPos position) {
        double x = Math.floor(position.x) + 0.5f;
        double y = Math.floor(position.y) + 0.5f;
        double z = Math.floor(position.z) + 0.5f;

        return mc.player.getDistance(x, y, z) > ModuleBetterMine.settingUpdateDistance.getValue().doubleValue();
    }
}
