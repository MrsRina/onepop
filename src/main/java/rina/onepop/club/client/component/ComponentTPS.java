package rina.onepop.club.client.component;

import com.mojang.realmsclient.gui.ChatFormatting;
import rina.onepop.club.Onepop;
import rina.onepop.club.api.component.Component;
import rina.onepop.club.api.component.StringType;

/**
 * @author SrRina
 * @since 06/04/2021 at 14:02
 **/
public class ComponentTPS extends Component {
    public ComponentTPS() {
        super("TPS", "TPS", "Shows the current TPS!!", StringType.USE);
    }

    @Override
    public void onRender(float partialTicks) {
        String tps = "TPS " + ChatFormatting.GRAY + String.format("%.1f", Onepop.getTPSManager().getTPS());

        this.render(tps, 0, 0);

        this.rect.setWidth(this.getStringWidth(tps));
        this.rect.setHeight(this.getStringHeight(tps));
    }
}