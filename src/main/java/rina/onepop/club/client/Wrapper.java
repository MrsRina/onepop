package rina.onepop.club.client;

import rina.onepop.club.client.module.client.ModuleClickGUI;
import me.rina.turok.render.font.TurokFont;

import java.awt.*;

/**
 * @author SrRina
 * @since 07/12/20 at 02:58pm
 */
public class Wrapper {
    public static final int FLAG_COMPONENT_CLOSED = -200;
    public static int FLAG_COMPONENT_OPENED = 2;

    public int[] background = {0, 0, 0, 0};
    public int[] base = {0, 0, 0, 0};
    public int[] highlight = {0, 0, 0, 0};

    public int clampScrollHeight = 200;

    public TurokFont fontBigWidget = new TurokFont(new Font("Whitney", 0, 24), true, true);
    public TurokFont fontNormalWidget = new TurokFont(new Font("Whitney", 0, 19), true, true);
    public TurokFont fontSmallWidget = new TurokFont(new Font("Whitney", 0, 16), true, true);
    public TurokFont fontNameTags = new TurokFont(new Font("Whitney", 0, 19), true, true);

    public void onUpdateColor() {
        final Color baseColor = ModuleClickGUI.settingBase.getColor();
        final Color backgroundColor = ModuleClickGUI.settingBackground.getColor();

        this.clampScrollHeight = ModuleClickGUI.settingScrollHeight.getValue().intValue();

        this.background = new int[] {
                backgroundColor.getRed(), backgroundColor.getGreen(), backgroundColor.getBlue(), 100
        };

        this.base = new int[] {
                baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), 150
        };

        this.highlight = new int[] {
                baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), 50
        };
    }
}