package me.rina.turok.hardware.mouse;

import org.lwjgl.input.Mouse;

/**
 * @author Rina.
 * @since 02/10/2020.
 */
public class TurokMouse {
    public final static int BUTTON_LEFT   = 0;
    public final static int BUTTON_MIDDLE = 2;
    public final static int BUTTON_RIGHT  = 3;

    private int scroll;

    private int x;
    private int y;

    public TurokMouse() {}

    public void setPos(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setCursorPos(int x, int y) {
        this.x = x;
        this.y = y;

        Mouse.setCursorPosition(this.x, this.y);
    }

    public int[] getPos() {
        return new int[] {
                this.x, this.y
        };
    }

    public void setCursorX(int x) {
        this.x = x;

        Mouse.setCursorPosition(this.x, this.y);
    }

    public int getX() {
        return x;
    }

    public void setCursorY(int y) {
        this.y = y;

        Mouse.setCursorPosition(this.x, this.y);
    }

    public int getY() {
        return y;
    }

    public int getScroll() {
        return -(Mouse.getDWheel() / 10);
    }

    public boolean hasWheel() {
        return Mouse.hasWheel();
    }
}
