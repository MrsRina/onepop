package rina.onepop.club.client.component;

import com.mojang.realmsclient.gui.ChatFormatting;
import rina.onepop.club.api.component.Component;
import rina.onepop.club.api.component.StringType;

public class ComponentPlayerCount extends Component {

    public ComponentPlayerCount() {
        super("PlayerCount", "PlayerCount", "Shows the number of players on your screen", StringType.USE);
    }

    @Override
    public void onRender(float partialTicks) {
        String players = "Players " + ChatFormatting.WHITE + mc.player.connection.getPlayerInfoMap().size();;

        render(players,0,0);

        this.rect.setHeight(getStringHeight(players));
        this.rect.setWidth(getStringWidth(players));
    }
}
