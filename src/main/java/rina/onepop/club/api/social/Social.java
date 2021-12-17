package rina.onepop.club.api.social;

import rina.onepop.club.api.social.type.SocialType;

/**
 * @author SrRina
 * @since 22/01/2021 at 17:09
 **/
public class Social {
    public String name;
    public SocialType type;

    public Social(String name) {
        this.name = name;
        this.type = SocialType.UNKNOWN;
    }

    public Social(String name, SocialType type) {
        this.name = name;
        this.type = type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setType(SocialType type) {
        this.type = type;
    }

    public SocialType getType() {
        return type;
    }
}