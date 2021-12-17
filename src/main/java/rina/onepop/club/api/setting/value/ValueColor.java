package rina.onepop.club.api.setting.value;

import rina.onepop.club.api.setting.Setting;

import java.awt.*;

/**
 * @author SrRina
 * @since 26/02/2021 at 13:59
 **/
public class ValueColor extends Setting {
    private int r;
    private int g;
    private int b;
    private int a;

    private final Picker picker;

    private boolean value;
    private boolean isCycloned;

    private float saturation;
    private float brightness;

    public ValueColor(String name, String tag, String description, Color color) {
        super(name, tag, description);

        this.r = color.getRed();
        this.g = color.getGreen();
        this.b = color.getBlue();
        this.a = color.getAlpha();

        this.picker = Picker.NORMAL;
    }

    public ValueColor(String name, String tag, String description, boolean state, Color color) {
        super(name, tag, description);

        this.r = color.getRed();
        this.g = color.getGreen();
        this.b = color.getBlue();
        this.a = color.getAlpha();

        this.value = state;
        this.picker = Picker.BOOLEAN;
    }

    public void setValue(boolean value) {
        this.value = value;
    }

    public boolean getValue() {
        return value;
    }

    public void setSaturation(float saturation) {
        this.saturation = saturation;
    }

    public float getSaturation() {
        return saturation;
    }

    public void setBrightness(float brightness) {
        this.brightness = brightness;
    }

    public float getBrightness() {
        return brightness;
    }

    public void setCycloned(boolean cycloned) {
        isCycloned = cycloned;
    }

    public boolean isCycloned() {
        return isCycloned;
    }

    public void setR(int r) {
        this.r = r;
    }

    public int getR() {
        return r;
    }

    public void setG(int g) {
        this.g = g;
    }

    public int getG() {
        return g;
    }

    public void setB(int b) {
        this.b = b;
    }

    public int getB() {
        return b;
    }

    public void setA(int a) {
        this.a = a;
    }

    public int getA() {
        return a;
    }

    public Color getColor() {
        if (this.isCycloned) {
            return this.getCycleColors(this.a);
        }

        return new Color(this.r, this.g, this.b, this.a);
    }

    public Color getColor(int alpha) {
        if (this.isCycloned) {
            return this.getCycleColors(alpha);
        }

        return new Color(this.r, this.g, this.b, alpha);
    }

    public Color getCycleColors(int alpha) {
        float[] currentSystemCycle = {
                (System.currentTimeMillis() % (360 * 32)) / (360f * 32f)
        };

        int currentColorCycle = Color.HSBtoRGB(currentSystemCycle[0], this.getSaturation(), this.getBrightness());

        return new Color(((currentColorCycle >> 16) & 0xFF), ((currentColorCycle >> 8) & 0xFF), (currentColorCycle & 0xFF), alpha);
    }

    public Picker getPicker() {
        return picker;
    }
}
