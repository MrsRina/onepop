package rina.onepop.club.api.setting;

import rina.onepop.club.api.setting.impl.SettingStructure;

/**
 * @author SrRina
 * @since 15/11/20 at 4:51pm
 */
public class Setting implements SettingStructure {
    private String name;
    private String tag;
    private String description;

    /**
     * Set if render or no the setting, when its faLse the setting won't be manageable,
     * to works you need refresh at GUI later changed.
     *
     * enabled -> shows if is enabled.
     * old -> old context for GUI.
     */
    private boolean enabled = true;
    private boolean old = true;

    public Setting(String name, String tag, String description) {
        this.name = name;
        this.tag = tag;
        this.description = description;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setTag(String tag) {
        this.tag = tag;
    }

    @Override
    public String getTag() {
        return tag;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setOld(boolean enabled) {
        this.old = enabled;
    }

    @Override
    public boolean getOld() {
        return old;
    }

    /**
     * Native method for update setting old state.
     */
    public void updateSetting() {
        this.old = this.isEnabled();
    }
}