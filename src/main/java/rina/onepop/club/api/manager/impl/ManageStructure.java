package rina.onepop.club.api.manager.impl;

import rina.onepop.club.Onepop;
import net.minecraft.client.Minecraft;

/**
 * @author SrRina
 * @since 14/02/2021 at 11:12
 **/
public interface ManageStructure {
    Minecraft mc = Onepop.getMinecraft();

    /**
     * Called in ClientTickEvent forge.
     */
    public void onUpdateAll();
}
