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
 * @since 27/03/2021 at 00:13
 **/
@Registry(name = "HUD Editor", tag = "HUD", description = "Draws overlay component of client HUD.", category = ModuleCategory.CLIENT)
public class ModuleHUD extends Module {
    public static ModuleHUD INSTANCE;

    /* Misc. */
    public static ValueBoolean settingRender = new ValueBoolean("Render", "Render", "Render HUDs components.", true);
    public static ValueString settingFont = new ValueString("Font", "Font", "Re-open HUD click GUI for update new font, else doesn't exist it sets to Tahoma.", "Tahoma");
    public static ValueNumber settingSpeedHUE = new ValueNumber("Speed HUE", "SpeedHUE", "The speed of rainbow hue cycle!", 20, 0, 100);

    public static ValueColor settingColor = new ValueColor("HUD", "HUD", "The HUD color picker.", true, new Color(255, 255, 255, 255));

    public ModuleHUD() {
        INSTANCE = this;
    }

    public void onRefreshFont() {
        final String font = settingFont.getValue();

        if (!Onepop.getComponentManager().font.getFont().getFontName().equalsIgnoreCase(font)) {
            Onepop.getComponentManager().font.setFont(new Font(font, 0, 19));

            if (!Onepop.getComponentManager().font.getFont().getFontName().equalsIgnoreCase(font)) {
                Onepop.getComponentManager().font.setFont(new Font("Tahoma", 0, 19));

                settingFont.setFormat("Tahoma");
                this.print(ChatFormatting.RED + "The font specified does not exist, font was set to default Tahoma");
            }
        }
    }

    @Override
    public void onShutdown() {
        this.setDisabled();
    }

    @Listener
    public void onListen(ClientTickEvent event) {
        // Its my brain.
        if (ISLClass.mc.currentScreen != Onepop.getComponentClickGUI()) {
            ISLClass.mc.displayGuiScreen(Onepop.getComponentClickGUI());
        }
    }

    @Override
    public void onEnable() {
        this.onRefreshFont();
    }

    @Override
    public void onDisable() {
        // Its my brain.
        if (ISLClass.mc.currentScreen == Onepop.getComponentClickGUI()) {
            Onepop.getComponentClickGUI().setClosingGUI(false);
        }
    }
}
