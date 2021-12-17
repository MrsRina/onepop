package rina.onepop.club.api.engine.caller;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;

/**
 * @author SrRina
 * @since 15/07/2021 at 00:08
 **/
public class Processor {
    public static Tessellator INSTANCE;

    private BufferBuilder bufferMemory;

    public void setBuffer() {
        INSTANCE = Tessellator.getInstance();

        this.bufferMemory = INSTANCE.getBuffer();
    }

    public void unsetBuffer() {
        if (this.bufferMemory == null) {
            return;
        }

        INSTANCE.draw();
    }

    public BufferBuilder work() {
        return this.bufferMemory;
    }
}
