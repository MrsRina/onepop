package rina.onepop.club.api.preset;

import rina.onepop.club.api.preset.impl.PresetValidator;
import rina.onepop.club.api.util.client.ByteManipulator;

/**
 * @author SrRina
 * @since 10/07/2021 at 13:51
 **/
public class Preset {
    private String name;
    private String data;

    private PresetValidator validator;

    public Preset(String name, String data) {
        this.name = name;
        this.data = data;

        this.validator = new PresetValidator(name + "-" + data, ByteManipulator.FALSE);
    }

    public void setTag(String name) {
        this.name = name;
    }

    public String getTag() {
        return name;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getData() {
        return data;
    }

    public void updateValidator() {
        this.validator.setMetaData(this.name + "-" + this.data);
    }

    public void setValidator() {
        this.validator.setCertifier(ByteManipulator.TRUE);
    }

    public void unsetValidator() {
        this.validator.setCertifier(ByteManipulator.FALSE);
    }

    public boolean isCurrent() {
        return ByteManipulator.intToBoolean(this.validator.getCertifier());
    }

    public byte getCertification() {
        return this.validator.getCertifier();
    }
}