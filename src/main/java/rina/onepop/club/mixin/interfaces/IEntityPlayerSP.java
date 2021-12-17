package rina.onepop.club.mixin.interfaces;

/**
 * @author Scrim | Brennan
 *
 * - Scrim!, you are awesome, now kisses, let me skid it
 **/
public interface IEntityPlayerSP {
    boolean isInLiquid();
    boolean isOnLiquid();
    boolean isMoving();

    void setInPortal(boolean portal);
}
