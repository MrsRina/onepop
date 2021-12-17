package rina.onepop.club.api.tracker.management;

import me.rina.turok.util.TurokTick;
import net.minecraft.client.Minecraft;
import rina.onepop.club.api.tracker.Tracker;
import rina.onepop.club.client.module.client.ModuleGeneral;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author SrRina
 * @since 05/02/2021 at 13:02
 **/
public class TrackerManager {
    public static TrackerManager INSTANCE;
    public static float DELAY = 50f;

    private final Queue<Tracker> trackQueue;
    private final TurokTick delayMS = new TurokTick();

    public TrackerManager() {
        INSTANCE = this;

        this.trackQueue = new ConcurrentLinkedQueue<>();
    }

    public Queue<Tracker> getTrackQueue() {
        return trackQueue;
    }

    public void dispatch(Tracker track) {
        this.trackQueue.add(track);
    }

    public void onUpdateAll() {
        final Minecraft mc = Minecraft.getMinecraft();

        if (mc.player == null) {
            return;
        }

        if (!this.trackQueue.isEmpty() && this.delayMS.isPassedMS(ModuleGeneral.settingDelay.getValue().floatValue() * 1000)) {
            final Tracker packetTracker = this.trackQueue.poll();

            if (packetTracker != null) {
                packetTracker.onPre();
                mc.player.connection.sendPacket(packetTracker.getPacket());
                packetTracker.onPost();

                this.delayMS.reset();
            }
        }
    }
}
