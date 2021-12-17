package rina.onepop.club.api.setting.impl;

/**
 * @author SrRina
 * @since 20/01/2021 at 09:36
 **/
public interface SettingStructure {
    /**
     * Set the current name of setting.
     */
    public void setName(String name);

    /**
     * Returns the current name of setting.
     *
     * @return String name of setting.
     */
    public String getName();

    /**
     * Set the current tag of setting.
     */
    public void setTag(String tag);

    /**
     * Returns the current tag of setting.
     *
     * @return String tag of setting.
     */
    public String getTag();

    /**
     * Set the current description of setting.
     */
    public void setDescription(String description);

    /**
     * Returns the current description of setting.
     *
     * @return String description of setting.
     */
    public String getDescription();

    /**
     * Set operable setting value.
     */
    public void setEnabled(boolean enabled);

    /**
     * Returns if is a operable setting.
     *
     * @return boolean value of setting operable.
     */
    public boolean isEnabled();

    /**
     * Set old state enabled.
     *
     * @param enabled boolean state of old setting operable value.
     */
    public void setOld(boolean enabled);

    /**
     * Returns the old state enabled.
     *
     * @return the old state enabled.
     */
    public boolean getOld();
}
