package rina.onepop.club.api.setting.value;

import rina.onepop.club.api.setting.Setting;

/**
 * @author SrRina
 * @since 20/01/2021 at 09:34
 **/
public class ValueBoolean extends Setting {
    private boolean value;

    public ValueBoolean(String name, String tag, String description, boolean value) {
        super(name, tag, description);

        this.value = value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }

    public boolean getValue() {
        return this.value;
    }
}
