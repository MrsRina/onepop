package rina.onepop.club.client.component;

import com.mojang.realmsclient.gui.ChatFormatting;
import rina.onepop.club.Onepop;
import rina.onepop.club.api.component.Component;
import rina.onepop.club.api.component.StringType;
import rina.onepop.club.api.component.impl.ComponentSetting;

/**
 * @author SrRina
 * @since 06/04/2021 at 14:02
 **/
public class ComponentWatermark extends Component {
    /* bebe. */
    private ComponentSetting<Boolean> settingVersion = new ComponentSetting<>("Version", "Version", "Shows the version!", true);

    public ComponentWatermark() {
        super("Watermark", "Watermark", "Show the client watermark!", StringType.USE);
    }

    @Override
    public void onRender(float partialTicks) {
        String watermark = Onepop.NAME + " " + ChatFormatting.GRAY + (this.settingVersion.getValue() ? Onepop.VERSION : "");

        this.render(watermark, 0, 0);

        this.rect.setWidth(this.getStringWidth(watermark));
        this.rect.setHeight(this.getStringHeight(watermark));
    }
}
