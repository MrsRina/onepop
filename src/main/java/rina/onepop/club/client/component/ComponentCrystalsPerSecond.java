package rina.onepop.club.client.component;

import com.mojang.realmsclient.gui.ChatFormatting;
import rina.onepop.club.api.component.Component;
import rina.onepop.club.api.component.StringType;
import rina.onepop.club.client.manager.world.BlockManager;

/**
 * @author SrRina
 * @since 20/06/2021 at 23:27
 **/
public class ComponentCrystalsPerSecond extends Component {
    public ComponentCrystalsPerSecond() {
        super("Crystals Per Second", "CrystalsPerSecond", "Counts crystals used by second.", StringType.USE);
    }

    @Override
    public void onRender(float partialTicks) {
        int crystalsPerSecond = BlockManager.getCrystalsPerSecond();

        final String text = "Crystals " + ChatFormatting.GRAY + crystalsPerSecond + "/s";

        render(text, 0, 0);

        this.rect.setWidth(this.getStringWidth(text));
        this.rect.setHeight(this.getStringHeight(text));
    }
}
