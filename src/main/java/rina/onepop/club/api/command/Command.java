package rina.onepop.club.api.command;

import com.mojang.realmsclient.gui.ChatFormatting;
import rina.onepop.club.Onepop;
import rina.onepop.club.api.util.chat.ChatUtil;

/**
 * @author SrRina
 * @since 16/11/20 at 12:05pm
 */
public class Command {
    private String[] alias;
    private String description;

    public Command(String[] alias, String description) {
        this.alias = alias;
        this.description = description;
    }

    public void setAlias(String[] alias) {
        this.alias = alias;
    }

    public String[] getAlias() {
        return alias;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /*
     * Tools.
     */
    public void splash() {
        this.print(ChatFormatting.RED + setSyntax());
    }

    public void splash(String splash) {
        this.print(splash);
    }

    public void print(String message) {
        ChatUtil.print(Onepop.CHAT + message);
    }

    public boolean verify(String argument, String... possibles) {
        boolean isVerified = false;

        for (String strings : possibles) {
            if (argument.equalsIgnoreCase(strings)) {
                isVerified = true;

                break;
            }
        }

        return isVerified;
    }

    /*
     * Overrides.
     */
    public String setSyntax() {
        return null;
    }

    public void onCommand(String[] args) {}
}