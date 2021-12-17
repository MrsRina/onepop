package rina.onepop.club.client.module.client;

import com.mojang.realmsclient.gui.ChatFormatting;
import rina.onepop.club.Onepop;
import rina.onepop.club.api.ISLClass;
import rina.onepop.club.api.module.Module;
import rina.onepop.club.api.module.impl.ModuleCategory;
import rina.onepop.club.api.module.registry.Registry;
import rina.onepop.club.api.setting.value.ValueBoolean;
import rina.onepop.club.api.setting.value.ValueColor;
import rina.onepop.club.api.setting.value.ValueNumber;
import rina.onepop.club.api.setting.value.ValueString;
import rina.onepop.club.client.event.client.ClientTickEvent;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

import java.awt.*;

/**
 * @author SrRina
 * @since 07/12/20 at 12:48pm
 */
@Registry(name = "Click GUI", tag = "ClickGUI", description = "Open GUI to manage module, settings...", category =  ModuleCategory.CLIENT)
public class ModuleClickGUI extends Module {
    public static ModuleClickGUI INSTANCE;

    /* Misc. */
    public static ValueString settingFont = new ValueString("Font", "Font", "Re-open click GUI for update new font, else doesn't exist it sets to Whitney.", "Tahoma");
    public static ValueBoolean settingBackGround = new ValueBoolean("Background GUI", "BackgroundGUI", "Draws default background Minecraft GUI.", true);
    public static ValueNumber settingScrollHeight = new ValueNumber("Scroll Height", "ScrollHeight", "Makes the clamp for scroll.", 200, 200, 500);

    /* Base color. */
    public static ValueColor settingBase = new ValueColor("Base", "Base", "Sets base color.", new Color(255, 0, 255, 255));

    /* Background. */
    public static ValueColor settingBackground = new ValueColor("Background", "Background", "Sets background color.", new Color(0, 0, 0, 100));

    public ModuleClickGUI() {
        INSTANCE = this;
    }

    @Listener
    public void onListen(ClientTickEvent event) {
        // Its my brain.
        if (ISLClass.mc.currentScreen != Onepop.getModuleClick()) {
            ISLClass.mc.displayGuiScreen(Onepop.getModuleClick());
        }
    }

    @Override
    public void onEnable() {
        final String font = settingFont.getValue();

        if (this.isRequiredFontRefresh(font)) {
            final Font small = new Font(font, 0, 16);
            final Font normal = new Font(font, 0, 18);
            final Font big = new Font(font, 0, 24);

            Onepop.getWrapper().fontSmallWidget.setFont(small);
            Onepop.getWrapper().fontNormalWidget.setFont(normal);
            Onepop.getWrapper().fontBigWidget.setFont(big);

            if (!Onepop.getWrapper().fontSmallWidget.getFont().getFontName().equalsIgnoreCase(font)) {
                Onepop.getWrapper().fontSmallWidget.setFont(new Font("Tahoma", 0, 16));
                Onepop.getWrapper().fontNormalWidget.setFont(new Font("Tahoma", 0, 18));
                Onepop.getWrapper().fontBigWidget.setFont(new Font("Tahoma", 0, 24));

                settingFont.setFormat("Whitney");
                this.print(ChatFormatting.RED + "The font specified does not exist, font was set to default Tahoma");
            }
        }
    }

    @Override
    public void onDisable() {
        // Its my brain.
        if (ISLClass.mc.currentScreen == Onepop.getModuleClick()) {
            Onepop.getModuleClick().setClosingGUI(true);
        }
    }

    public boolean isRequiredFontRefresh(final String font) {
        boolean isRequired = !Onepop.getWrapper().fontSmallWidget.getFont().getFontName().equalsIgnoreCase(font);

        if (!Onepop.getWrapper().fontNormalWidget.getFont().getFontName().equalsIgnoreCase(font)) {
            isRequired = true;
        }

        if (!Onepop.getWrapper().fontBigWidget.getFont().getFontName().equalsIgnoreCase(font)) {
            isRequired = true;
        }

        return isRequired;
    }
}
