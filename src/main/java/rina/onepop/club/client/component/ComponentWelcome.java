package rina.onepop.club.client.component;

import com.mojang.realmsclient.gui.ChatFormatting;
import rina.onepop.club.api.component.Component;
import rina.onepop.club.api.component.StringType;
import rina.onepop.club.api.component.impl.ComponentSetting;

import java.util.HashMap;

/**
 * @author SrRina
 * @since 06/04/2021 at 14:02
 **/
public class ComponentWelcome extends Component {
    final public HashMap<String, String> userWelcomeMap = new HashMap<>();

    /* bebe. */
    private ComponentSetting<Boolean> settingCustom = new ComponentSetting<>("Do you have 1POP beta?", "DoYouHave1POPBeta?", "If yes...", true);

    public ComponentWelcome() {
        super("Welcome", "Welcome", "Really cool welcomes!!", StringType.USE);

        this.userWelcomeMap.put("SRRINA", "You'll make go to canada!");
        this.userWelcomeMap.put("CHEROSIN", "Stop uses burrow and anime pfp," + ChatFormatting.DARK_RED + " I love you!");
        this.userWelcomeMap.put("FLLMALL", "Uses the new auto crystal, and" + ChatFormatting.DARK_RED + " I love you!");
        this.userWelcomeMap.put("HEROGLAUCOP", "My godinho lindo!" + ChatFormatting.DARK_RED + " I love you!");
        this.userWelcomeMap.put("MATHEUS1300", "Nigger! and balls and " + ChatFormatting.DARK_RED + " I love you!");
    }

    @Override
    public void onRender(float partialTicks) {
        String welcomes = "Welcome to" + ChatFormatting.GOLD + " 1pop" + ChatFormatting.RESET + " " + mc.player.getName() + " " + ((this.settingCustom.getValue() && this.userWelcomeMap.get(mc.player.getName().toUpperCase()) != null) ? this.userWelcomeMap.get(mc.player.getName().toUpperCase()) : "");

        this.render(welcomes, 0, 0);

        this.rect.setWidth(this.getStringWidth(welcomes));
        this.rect.setHeight(this.getStringHeight(welcomes));
    }
}
