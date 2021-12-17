package rina.onepop.club.api.command.impl;

/**
 * @author SrRina
 * @since 16/11/20 at 02:03pm
 */
public class CommandPrefix {
    private String prefix;

    public CommandPrefix(String prefix) {
        this.prefix = prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getPrefix() {
        return prefix;
    }
}
