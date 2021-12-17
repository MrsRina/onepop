package rina.onepop.club.api.setting.value;

import rina.onepop.club.api.setting.Setting;

/**
 * @author SrRina
 * @since 20/01/2021 at 09:53
 **/
public class ValueNumber extends Setting {
    private Number value;

    private Number minimum;
    private Number maximum;

    private Smooth smooth = Smooth.PRIMITIVE;

    public ValueNumber(String name, String tag, String description, int value, int minimum, int maximum) {
        super(name, tag, description);

        this.value = (float) value;

        this.minimum = (float) minimum;
        this.maximum = (float) maximum;

        this.smooth = Smooth.INTEGER;
    }

    public ValueNumber(String name, String tag, String description, double value, double minimum, double maximum) {
        super(name, tag, description);

        this.value = value;

        this.minimum = minimum;
        this.maximum = maximum;
    }

    public ValueNumber(String name, String tag, String description, float value, float minimum, float maximum) {
        super(name, tag, description);

        this.value = value;

        this.minimum = minimum;
        this.maximum = maximum;
    }

    public void setValue(Number value) {
        this.value = value;
    }

    public Number getValue() {
        return value;
    }

    public void setMinimum(Number minimum) {
        this.minimum = minimum;
    }

    public Number getMinimum() {
        return minimum;
    }

    public void setMaximum(Number maximum) {
        this.maximum = maximum;
    }

    public Number getMaximum() {
        return maximum;
    }

    public void setSmooth(Smooth smooth) {
        this.smooth = smooth;
    }

    public Smooth getSmooth() {
        return smooth;
    }
}
