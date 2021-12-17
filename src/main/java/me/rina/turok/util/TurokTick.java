package me.rina.turok.util;

/**
 * @author SrRina
 * @since 20/11/2020 at 12:00am
 */
public class TurokTick {
    private long ticks;

    public TurokTick() {
        this.ticks = -1;
    }

    public void reset() {
        this.ticks = System.currentTimeMillis();
    }

    public void setTicks(long ticks) {
        this.ticks = ticks;
    }

    public long getTicks() {
        return ticks;
    }

    public float getCurrentTicks() {
        return System.currentTimeMillis() - this.ticks;
    }

    public int getCurrentTicksCount(double speed) {
        return (int) ((System.currentTimeMillis() - this.ticks) / speed);
    }

    public boolean isPassedMS(float ms) {
        return System.currentTimeMillis() - this.ticks >= ms;
    }

    public boolean isPassedSI(float si) {
        return System.currentTimeMillis() - this.ticks >= (si * 1000);
    }
}
