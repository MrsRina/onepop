package rina.onepop.club.client.event.client;

import rina.onepop.club.api.event.Event;
import rina.onepop.club.api.module.Module;

/**
 * @author SrRina
 * @since 08/05/2021 at 21:08
 **/
public class ModuleStatusEvent extends Event {
    private Module module;
    private boolean newStatus;

    public ModuleStatusEvent(Module module, boolean enabled) {
        super();

        this.module = module;
        this.newStatus = enabled;
    }

    public boolean isNewStatus() {
        return newStatus;
    }

    public Module getModule() {
        return module;
    }
}
