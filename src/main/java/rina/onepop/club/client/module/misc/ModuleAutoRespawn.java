package rina.onepop.club.client.module.misc;

import com.mojang.realmsclient.gui.ChatFormatting;
import rina.onepop.club.api.ISLClass;
import rina.onepop.club.api.module.Module;
import rina.onepop.club.api.module.impl.ModuleCategory;
import rina.onepop.club.api.module.registry.Registry;
import rina.onepop.club.api.setting.value.ValueBoolean;
import rina.onepop.club.api.setting.value.ValueNumber;
import rina.onepop.club.api.util.client.NullUtil;
import rina.onepop.club.api.util.entity.Dimension;
import rina.onepop.club.api.util.entity.PlayerUtil;
import rina.onepop.club.client.event.client.RunTickEvent;
import rina.onepop.club.client.event.underground.DisplayGuiScreenEvent;
import me.rina.turok.util.TurokTick;
import net.minecraft.client.gui.GuiGameOver;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

/**
 * @author SrRina
 * @since 02/02/2021 at 00:07
 **/
@Registry(name = "Auto-Respawn", tag = "AutoRespawn", description = "Automatically respawn after you die.", category = ModuleCategory.MISC)
public class ModuleAutoRespawn extends Module {
    /* Misc. */
    public static ValueNumber settingDelay = new ValueNumber("Delay", "Delay", "The seconds delay for respawn.", 0, 0, 10);
    public static ValueBoolean settingDeathPosition = new ValueBoolean("Death Position", "DeathPosition", "Notify your last position before you die.", true);
    public static ValueBoolean settingSync = new ValueBoolean("Sync", "Sync", "Respawn only if death GUI screen is shown.", true);

    private boolean hasSentMessage;
    private final TurokTick tick = new TurokTick();

    @Override
    public void onSetting() {
        settingSync.setEnabled(false);
    }

    @Listener
    public void onDisplayGuiScreen(DisplayGuiScreenEvent event) {
    }

    @Listener
    public void onListen(RunTickEvent event) {
        if (NullUtil.isPlayerWorld()) {
            return;
        }

        boolean flag = mc.currentScreen instanceof GuiGameOver;

        if (flag) {
            if (this.tick.isPassedMS(settingDelay.getValue().floatValue() * 1000)) {
                this.doRequest();
            }
        } else {
            this.hasSentMessage = true;

            this.tick.reset();
        }
    }

    public void doRequest() {
        if (settingDeathPosition.getValue()) {
            double[] pos = PlayerUtil.getPos();

            // We need set the color for the last position, to sync dimension with color.
            String position = "[" + ((int) pos[0]) + ", " + ((int) pos[1]) + ", " + ((int) pos[2]) + "]";

            // Don't fucking repeat the fucking message.
            if (this.hasSentMessage) {
                this.print("You died at " + this.getColorBasedDimension() + position);

                // Don't fucking repeat the fucking message.
                this.hasSentMessage = false;
            }
        }

        // What does it do.
        ISLClass.mc.player.respawnPlayer();
    }

    // Based.
    public String getColorBasedDimension() {
        String string = "";

        if (PlayerUtil.getCurrentDimension() == Dimension.WORLD) {
            string = ("" + ChatFormatting.GREEN);
        }

        if (PlayerUtil.getCurrentDimension() == Dimension.NETHER) {
            string = ("" + ChatFormatting.RED);
        }

        if (PlayerUtil.getCurrentDimension() == Dimension.END) {
            string = ("" + ChatFormatting.BLUE);
        }

        // Can't speak.
        return string;
    }
}
