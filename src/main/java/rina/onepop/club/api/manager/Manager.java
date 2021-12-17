package rina.onepop.club.api.manager;

import rina.onepop.club.api.manager.impl.ManageStructure;

/**
 * @author SrRina
 * @since 04/02/2021 at 19:07
 **/
public class Manager implements ManageStructure {
    private String name;
    private String description;

    public Manager(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public void onUpdateAll() {

    }
}
