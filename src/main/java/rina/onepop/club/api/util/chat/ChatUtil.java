package rina.onepop.club.api.util.chat;

import com.mojang.realmsclient.gui.ChatFormatting;
import rina.onepop.club.Onepop;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.util.text.TextComponentString;

/**
 * @author SrRina
 * @since 15/11/20 at 9:43pm
 */
public class ChatUtil {
    public static void print(String message) {
        if (Onepop.getMinecraft().ingameGUI == null) {
            return;
        }

        String formatedMessage = ChatFormatting.GRAY + message;

        Onepop.getMinecraft().ingameGUI.getChatGUI().printChatMessage(new TextComponentString(formatedMessage));
    }

    public static void refreshPrint(String message) {
        if (Onepop.getMinecraft().ingameGUI == null) {
            return;
        }

        String formatedMessage = ChatFormatting.GRAY + message;

        Onepop.getMinecraft().ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(new TextComponentString(formatedMessage), 69);
    }

    public static void malloc(String message) {
        if (Onepop.getMinecraft().ingameGUI == null) {
            return;
        }

        Onepop.getMinecraft().ingameGUI.getChatGUI().addToSentMessages(message);
    }

    public static void message(String message) {
        if (Onepop.getMinecraft().player == null) {
            return;
        }

        Onepop.getMinecraft().player.connection.sendPacket(new CPacketChatMessage(message));
    }
}
