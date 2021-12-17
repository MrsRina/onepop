package rina.onepop.club.api.strict.util;

import me.rina.turok.util.TurokTick;
import net.minecraft.client.Minecraft;

/**
 * @author SrRina
 * @since 06/07/2021 at 18:19
 **/
public class Rotation {
    public final Minecraft mc = Minecraft.getMinecraft();

    public float pitch, yaw;
    public boolean packet, stay;
    public TurokTick rotationStay = new TurokTick();
    public int time;
    public EnumRotation mode;

    public Rotation(float pitch, float yaw) {
        this.pitch = pitch;
        this.yaw = yaw;
        this.packet = true;
        this.stay = true;

        rotationStay.reset();
    }

    public Rotation(float pitch, float yaw, boolean packet) {
        this.pitch = pitch;
        this.yaw = yaw;
        this.packet = packet;
        this.stay = true;

        rotationStay.reset();
    }

    public Rotation(float pitch, float yaw, boolean packet, boolean stay, int time) {
        this.pitch = pitch;
        this.yaw = yaw;
        this.packet = packet;
        this.stay = stay;
        this.time = time;

        rotationStay.reset();
    }

    public Rotation(float yaw, float pitch, EnumRotation mode) {
        this.yaw = yaw;
        this.pitch = pitch;
        this.mode = mode;

        rotationStay.reset();
    }

    public void updateRotations() {
        try {
            if (this.packet) {
                mc.player.renderYawOffset = this.yaw;
                mc.player.rotationYawHead = this.yaw;
            } else {
                mc.player.rotationYaw = this.yaw;
                mc.player.rotationPitch = this.pitch;
            }
        } catch (Exception ignored) {

        }
    }

    public void cancel() {
        this.yaw = mc.player.rotationYaw;
        this.pitch = mc.player.rotationPitch;

        this.rotationStay.reset();
    }

    public float getPitch() {
        return this.pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public float getYaw() {
        return this.yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }
}
