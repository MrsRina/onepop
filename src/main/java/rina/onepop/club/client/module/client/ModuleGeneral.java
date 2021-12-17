package rina.onepop.club.client.module.client;

import rina.onepop.club.api.module.Module;
import rina.onepop.club.api.module.impl.ModuleCategory;
import rina.onepop.club.api.module.registry.Registry;
import rina.onepop.club.api.setting.value.ValueBoolean;
import rina.onepop.club.api.setting.value.ValueColor;
import rina.onepop.club.api.setting.value.ValueNumber;

import java.awt.*;

/**
 * @author SrRina
 * @since 03/05/2021 at 13:02
 **/
@Registry(name = "General", tag = "General", description = "General client settings!", category = ModuleCategory.CLIENT)
public class ModuleGeneral extends Module {
    /* Watermark Notify. */
    //public static ValueBoolean settingCompactWatermarkNotify = new ValueBoolean("Compact Watermark Notify", "CompactWatermarkNotify", "Compact chat watermark style!", true);

    /* Command. */
    public static ValueBoolean settingFriendedMessage = new ValueBoolean("Friended Message", "FriendedMessage", "Sends message for new friends!", true);
    public static ValueBoolean settingEnemyMessage = new ValueBoolean("Enemy Message", "EnemyMessage", "Sends message for new enemies!", false);

    /* Render & performance. */
    public static ValueColor settingFriendColor = new ValueColor("Friend Color", "FriendColor", "Sets friend color.", new Color(0, 255, 255, 100));
    public static ValueNumber settingVisualEffects = new ValueNumber("Visual Effects", "VisualEffects", "Sets level of visual effects.", 10f, 4f, 16f);

    /* Connection. */
    public static ValueNumber settingDelay = new ValueNumber("Packet Track. Delay", "PacketTrackerDelay", "Set delay of packets by track.", 0.5f, 0f, 1f);

    @Override
    public void onSetting() {
        if (!this.isEnabled()) {
            this.setEnabled();
        }
    }
}
