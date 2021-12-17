package rina.onepop.club.client.manager.network;

import rina.onepop.club.api.manager.Manager;
import rina.onepop.club.client.event.network.PacketEvent;
import net.minecraft.network.play.server.SPacketTimeUpdate;
import net.minecraft.util.math.MathHelper;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

import java.util.Arrays;

/**
 * @author SrRina
 * @since 17/05/2021 at 01:38
 **/
public class TPSManager extends Manager {
    public TPSManager() {
        super("TPS Manager", "Manager tps on client.");

        this.reset();
    }

    final float[] ticks = new float[20];

    private long lastUpdate;
    private int nextIndex = 0;

    @Listener
    public void onPacketReceive(PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketTimeUpdate) {
            this.refresh();
        }
    }

    public float getTPS() {
        float num_ticks = 0.0f;
        float sum_ticks = 0.0f;

        for (float tick : this.ticks) {
            if (tick > 0.0f) {
                sum_ticks += tick;
                num_ticks += 1.0f;
            }
        }

        return MathHelper.clamp(sum_ticks / num_ticks, 0.0f, 20.0f);
    }

    public void reset() {
        this.nextIndex = 0;
        this.lastUpdate = -1L;

        Arrays.fill(ticks, 0.0f);
    }

    public void refresh() {
        if (this.lastUpdate != -1L) {
            float time = (float) (System.currentTimeMillis() - this.lastUpdate) / 1000.0f;
            this.ticks[(this.nextIndex % ticks.length)] = MathHelper.clamp(20.0f / time, 0.0f, 20.0f);

            this.nextIndex += 1;
        }

        this.lastUpdate = System.currentTimeMillis();
    }
}