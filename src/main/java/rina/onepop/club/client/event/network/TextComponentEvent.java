package rina.onepop.club.client.event.network;

import net.minecraft.util.text.ITextComponent;
import rina.onepop.club.api.event.Event;
import rina.onepop.club.api.event.impl.EventStage;

/**
 * @author SrRina
 * @since 01/07/2021 at 16:28
 **/
public class TextComponentEvent extends Event {
    public static class Disconnect extends TextComponentEvent {
        public Disconnect(EventStage stage, ITextComponent textComponent) {
            super(stage, textComponent);
        }
    }

    private ITextComponent textComponent;

    public TextComponentEvent(EventStage stage, ITextComponent textComponent) {
        super(stage);

        this.textComponent = textComponent;
    }

    public void setTextComponent(ITextComponent textComponent) {
        this.textComponent = textComponent;
    }

    public ITextComponent getTextComponent() {
        return textComponent;
    }
}
