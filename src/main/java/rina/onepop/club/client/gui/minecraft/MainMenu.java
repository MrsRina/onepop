package rina.onepop.club.client.gui.minecraft;

import net.minecraft.client.gui.GuiMainMenu;

/**
 * @author SrRina
 * @since 28/04/2021 at 23:55
 **/
public class MainMenu extends GuiMainMenu {


    @Override
    public void drawScreen(int mx, int my, float partialTicks) {
        super.drawScreen(mx, my, partialTicks);

        this.selectedButton = null;
    }
}
