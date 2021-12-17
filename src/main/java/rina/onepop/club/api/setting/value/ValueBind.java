package rina.onepop.club.api.setting.value;

import rina.onepop.club.api.setting.Setting;

/**
 * @author SrRina
 * @since 01/02/2021 at 12:25
 **/
public class ValueBind extends Setting {
    private boolean value;
    private int keyCode;

    private InputType inputType;

    public ValueBind(String name, String tag, String description, int key) {
        super(name, tag, description);

        this.keyCode = key;
        this.inputType = InputType.KEYBOARD;
    }

    public void setInputType(InputType inputType) {
        this.inputType = inputType;
    }

    public InputType getInputType() {
        return inputType;
    }

    public void setKeyCode(int key) {
        this.keyCode = key;
    }

    public int getKeyCode() {
        return keyCode;
    }

    public void setValue(boolean value) {
        this.value = value;
    }

    public boolean getValue() {
        return value;
    }
}
