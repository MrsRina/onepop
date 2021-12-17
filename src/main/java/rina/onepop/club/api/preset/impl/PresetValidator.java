package rina.onepop.club.api.preset.impl;

/**
 * @author SrRina
 * @since 10/07/2021 at 13:53
 **/
public class PresetValidator {
    private String metaData;
    private byte certifier;

    public PresetValidator(String metaData, byte certifier) {
        this.metaData = metaData;
        this.certifier = certifier;
    }

    public void setMetaData(String metaData) {
        this.metaData = metaData;
    }

    public String getMetaData() {
        return metaData;
    }

    public void setCertifier(byte certifier) {
        this.certifier = certifier;
    }

    public byte getCertifier() {
        return certifier;
    }
}
