package rina.onepop.club.client.event.underground;

import rina.onepop.club.api.event.Event;
import rina.onepop.club.api.event.impl.EventStage;
import net.minecraft.client.gui.GuiScreen;

/**
 * @author SrRina
 * @since 07/06/2021 at 20:58
 **/
public class DisplayGuiScreenEvent extends Event {
    private GuiScreen guiScreen;

    public DisplayGuiScreenEvent(final GuiScreen guiScreen) {
        super(EventStage.POST);

        this.guiScreen = guiScreen;
    }

    public GuiScreen getGuiScreen() {
        return guiScreen;
    }

    public void setGuiScreen(GuiScreen guiScreen) {
        this.guiScreen = guiScreen;
    }
}
