package rina.onepop.club.api.preset.impl;

import com.google.gson.JsonObject;

/**
 * @author SrRina
 * @since 10/07/2021 at 14:18
 **/
public class PresetState {
    protected JsonObject metaData;

    public PresetState() {

    }

    public void setMetaData(JsonObject metaData) {
        this.metaData = metaData;
    }

    public JsonObject getMetaData() {
        return metaData;
    }
}

