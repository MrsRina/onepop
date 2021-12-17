package rina.onepop.club.client.component;

import com.mojang.realmsclient.gui.ChatFormatting;
import rina.onepop.club.api.component.Component;
import rina.onepop.club.api.component.StringType;

/**
 * @author Manesko
 * @since 12:04am 14/4/2021
 */
public class ComponentDurability extends Component {
    public ComponentDurability() {
        super("Durability", "durability", "Shows the durability of an item", StringType.USE);
    }

    @Override
    public void onRender() {
        int d = mc.player.getHeldItemMainhand().getItemDamage() - mc.player.getHeldItemMainhand().getMaxDamage();

        String dura = "Durability " + ChatFormatting.WHITE + d;

        render(dura,0,0);

        this.rect.setWidth(getStringWidth(dura));
        this.rect.setHeight(getStringHeight(dura));
    }
}
