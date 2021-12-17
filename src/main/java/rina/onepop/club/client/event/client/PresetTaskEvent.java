package rina.onepop.club.client.event.client;

import rina.onepop.club.api.event.Event;
import rina.onepop.club.api.event.impl.EventStage;
import rina.onepop.club.api.preset.Preset;

/**
 * @author SrRina
 * @since 12/06/2021 at 21:37
 **/
public class PresetTaskEvent extends Event {
    private final Preset preset;
    private boolean delete;

    private boolean save;
    private boolean load;

    public PresetTaskEvent(EventStage stage, Preset preset) {
        super(stage);

        this.preset = preset;

        this.save = stage == EventStage.PRE;
        this.load = stage == EventStage.POST;
    }

    public PresetTaskEvent(Preset preset) {
        super(EventStage.PRE);

        this.save = true;
        this.load = false;

        this.preset = preset;
        this.delete = true;
    }

    public Preset getPreset() {
        return preset;
    }

    public boolean isSave() {
        return save;
    }

    public boolean isLoad() {
        return load;
    }

    public boolean isDelete() {
        return delete;
    }
}
