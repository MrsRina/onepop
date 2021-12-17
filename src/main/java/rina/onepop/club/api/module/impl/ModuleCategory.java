package rina.onepop.club.api.module.impl;

/**
 * @author SrRina
 * @since 15/11/20 at 4:51pm
 */
public enum ModuleCategory {
    COMBAT("Combat"), PLAYER("Player"), RENDER("Render"), EXPLOIT("Exploit"), MISC("Misc"), CLIENT("Client");

    private final String tag;

    ModuleCategory(String tag) {
        this.tag = tag;
    }

    public String getTag() {
        return tag;
    }
}