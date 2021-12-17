package rina.onepop.club.client.module.misc;

import rina.onepop.club.api.module.Module;
import rina.onepop.club.api.module.impl.ModuleCategory;
import rina.onepop.club.api.module.registry.Registry;
import rina.onepop.club.api.setting.value.ValueString;
import rina.onepop.club.api.util.chat.ChatSuffixUtil;
import rina.onepop.club.client.event.network.PacketEvent;
import net.minecraft.network.play.client.CPacketChatMessage;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

/**
 * @author SrRina
 * @since 04/02/2021 at 00:28
 **/
@Registry(name = "Chat Suffix", tag = "ChatSuffix", description = "Send at end message the custom client suffix.", category = ModuleCategory.MISC)
public class ModuleChatSuffix extends Module {
    /* Misc. */
    public static ValueString settingIgnoredPrefixes = new ValueString("Ignored Prefixes", "IgnoredPrefixes", "Characters to ignore.", "/!;&$(\\:.@*#)");
    public static ValueString settingSuffix = new ValueString("Suffix", "Suffix", "The lower case suffix.", "onepop");

    @Listener
    public void onListen(PacketEvent.Send event) {
        if (!(event.getPacket() instanceof CPacketChatMessage)) {
            return;
        }

        CPacketChatMessage packet = (CPacketChatMessage) event.getPacket();
        String message = packet.getMessage();

        // We need verify if has prefix or no.
        boolean isContinuable = true;

        if (settingIgnoredPrefixes.isEnabled()) {
            for (String prefixes : settingIgnoredPrefixes.getValue().split("")) {
                if (message.startsWith(prefixes)) {
                    isContinuable = false;

                    break;
                }
            }
        }

        // DO NOT send if starts with prefix (when enabled setting and find).
        if (isContinuable) {
            message += " " + ChatSuffixUtil.hephaestus(settingSuffix.getValue());
        }

        packet.message = message;
    }
}
