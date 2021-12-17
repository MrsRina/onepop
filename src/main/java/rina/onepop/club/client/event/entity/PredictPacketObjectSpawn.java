package rina.onepop.club.client.event.entity;

import rina.onepop.club.api.event.Event;
import net.minecraft.network.play.server.SPacketSpawnObject;

import java.util.UUID;

/**
 * @author SrRina
 * @since 10/05/2021 at 00:12
 **/
public class PredictPacketObjectSpawn extends Event {
    private final int entityId;
    private final UUID uniqueId;
    private final double x;
    private final double y;
    private final double z;
    private final int speedX;
    private final int speedY;
    private final int speedZ;
    private final int pitch;
    private final int yaw;
    private final int type;
    private final int data;

    public PredictPacketObjectSpawn(SPacketSpawnObject packet) {
        this.entityId = packet.getEntityID();
        this.uniqueId = packet.getUniqueId();
        this.x = packet.getX();
        this.y = packet.getY();
        this.z = packet.getZ();
        this.speedX = packet.getSpeedX();
        this.speedY = packet.getSpeedY();
        this.speedZ = packet.getSpeedZ();
        this.pitch = packet.getPitch();
        this.yaw = packet.getYaw();
        this.type = packet.getType();
        this.data = packet.getData();
    }

    public int getEntityId() {
        return entityId;
    }

    public UUID getUniqueId() {
        return uniqueId;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public int getSpeedX() {
        return speedX;
    }

    public int getSpeedY() {
        return speedY;
    }

    public int getSpeedZ() {
        return speedZ;
    }

    public int getPitch() {
        return pitch;
    }

    public int getYaw() {
        return yaw;
    }

    public int getType() {
        return type;
    }

    public int getData() {
        return data;
    }
}
