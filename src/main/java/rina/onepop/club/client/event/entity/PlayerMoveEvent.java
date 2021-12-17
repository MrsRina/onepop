package rina.onepop.club.client.event.entity;

import rina.onepop.club.api.event.Event;
import rina.onepop.club.api.event.impl.EventStage;
import net.minecraft.entity.MoverType;

/**
 * @author SrRina
 * @since 23/02/2021 at 23:21
 **/
public class PlayerMoveEvent extends Event {
    /* Type of movement. */
    private MoverType type;

    /* Positions. */
    public double x;
    public double y;
    public double z;

    public PlayerMoveEvent(MoverType type, double x, double y, double z) {
        super(EventStage.PRE);

        this.type = type;

        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void setType(MoverType type) {
        this.type = type;
    }

    public MoverType getType() {
        return type;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getX() {
        return x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getY() {
        return y;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public double getZ() {
        return z;
    }
}
