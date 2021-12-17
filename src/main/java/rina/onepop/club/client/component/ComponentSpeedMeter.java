package rina.onepop.club.client.component;

import com.mojang.realmsclient.gui.ChatFormatting;
import rina.onepop.club.api.component.Component;
import rina.onepop.club.api.component.StringType;
import rina.onepop.club.api.component.impl.ComponentSetting;
import rina.onepop.club.api.util.client.NullUtil;
import rina.onepop.club.api.util.entity.PlayerUtil;

/**
 * @author SrRina
 * @since 08/06/2021 at 01:24
 **/
public class ComponentSpeedMeter extends Component {
    public ComponentSetting<Meter> settingMeter = new ComponentSetting<>("Meter", "Meter", "Modes for meter.", Meter.KMH);

    public ComponentSpeedMeter() {
        super("Speed Meter", "SpeedMeter", "Shows your speed!", StringType.USE);
    }

    @Override
    public void onRender(float partialTicks) {
        if (NullUtil.isPlayerWorld()) {
            return;
        }

        final double bps = PlayerUtil.getBPS();
        final String speed = "Speed " + ChatFormatting.GRAY + String.format("%.1f", settingMeter.getValue() == Meter.KMH ? bps * 3.6f : bps) + "" + (settingMeter.getValue() == Meter.KMH ? "km/h" : "b/s");

        this.render(speed, 0, 0);

        this.rect.setWidth(this.getStringWidth(speed));
        this.rect.setHeight(this.getStringHeight(speed));
    }
}
