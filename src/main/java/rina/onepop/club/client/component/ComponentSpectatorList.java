package rina.onepop.club.client.component;

import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.entity.player.EntityPlayer;
import rina.onepop.club.api.component.Component;
import rina.onepop.club.api.component.StringType;
import rina.onepop.club.api.component.impl.ComponentSetting;
import rina.onepop.club.api.social.management.SocialManager;
import rina.onepop.club.api.social.type.SocialType;
import rina.onepop.club.api.util.client.NullUtil;

/**
 * @author Rina
 * @since 12/10/2021 at 00:14pm
 **/
public class ComponentSpectatorList extends Component {
    public static ComponentSetting<Integer> settingRange = new ComponentSetting<>("Range", "Range", "Delimit a range.", 24, 4, 64);

    public ComponentSpectatorList() {
        super("Spectator List", "SpectatorList", "Render all spectator close of you.", StringType.USE);
    }

    private int lastTick;
    private String bufferedNamesSpectator;

    @Override
    public void onRender(float partialTicks) {
        if (NullUtil.isPlayerWorld()) {
            return;
        }

        if (this.lastTick >= 30) {
            this.clear();

            for (EntityPlayer entities : mc.world.playerEntities) {
                if (entities.isSpectator() && mc.player.getDistance(entities) <= settingRange.getValue()) {
                    this.add(SocialManager.is(entities.getName(), SocialType.FRIEND) ? ChatFormatting.AQUA + entities.getName() : entities.getName());
                }
            }

            this.lastTick = 0;
        }

        this.render(this.bufferedNamesSpectator, 0, 0);

        this.rect.setWidth(this.getStringWidth(this.bufferedNamesSpectator));
        this.rect.setHeight(this.getStringHeight(this.bufferedNamesSpectator));

        this.lastTick++;
    }

    public void clear() {
        this.bufferedNamesSpectator = "?";
    }

    public void add(String name) {
        this.bufferedNamesSpectator = this.bufferedNamesSpectator + ChatFormatting.RESET + "?" + name;
    }
}
