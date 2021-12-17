package rina.onepop.club.api.setting.value;

import rina.onepop.club.api.setting.Setting;

/**
 * @author SrRina
 * @since 20/01/2021 at 09:57
 **/
public class ValueString extends Setting {
    private String format;
    private String value;

    public ValueString(String name, String tag, String description, String value) {
        super(name, tag, description);

        this.format = "";
        this.value = value;
    }

    public ValueString addFormat(String format) {
        this.format = format;

        return this;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getFormat() {
        return format;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
