package rina.onepop.club.client.component;

import com.mojang.realmsclient.gui.ChatFormatting;
import rina.onepop.club.api.component.Component;
import rina.onepop.club.api.component.StringType;

/**
 * @author SrRina
 * @since 06/04/2021 at 14:02
 **/
public class ComponentPing extends Component {
    public ComponentPing() {
        super("Ping", "Ping", "Shows your ping!!", StringType.USE);
    }

    @Override
    public void onRender(float partialTicks) {
        if (mc.player == null) {
            return;
        }

        String ping = "Ping " + ChatFormatting.GRAY + this.getPing();

        this.render(ping, 0, 0);

        this.rect.setWidth(this.getStringWidth(ping));
        this.rect.setHeight(this.getStringHeight(ping));
    }

    public String getPing() {
        String ping = "?";

        try {
            ping = "" + mc.getConnection().getPlayerInfo(mc.player.getUniqueID()).getResponseTime();
        } catch (Exception e) {
            return "?";
        }

        return ping;
    }
}
