package rina.onepop.club.client.component;

import com.mojang.realmsclient.gui.ChatFormatting;
import rina.onepop.club.api.component.Component;
import net.minecraft.client.Minecraft;
import rina.onepop.club.api.component.StringType;

/**
 * @author SrRina
 * @since 06/04/2021 at 14:02
 **/
public class ComponentFPS extends Component {
    public ComponentFPS() {
        super("FPS", "FPS", "Shows your FPS!!", StringType.USE);
    }

    @Override
    public void onRender(float partialTicks) {
        String fps = "FPS " + ChatFormatting.GRAY + Minecraft.getDebugFPS();

        this.render(fps, 0, 0);

        this.rect.setWidth(this.getStringWidth(fps));
        this.rect.setHeight(this.getStringHeight(fps));
    }
}