package rina.onepop.club.client.gui.overlay.setting;

import rina.onepop.club.api.gui.flag.Flag;
import rina.onepop.club.api.gui.widget.Widget;
import rina.onepop.club.client.gui.overlay.component.frame.ComponentListFrame;
import rina.onepop.club.client.gui.overlay.component.widget.ComponentWidget;
import me.rina.turok.render.font.management.TurokFontManager;
import me.rina.turok.render.opengl.TurokRenderGL;
import me.rina.turok.util.TurokMath;
import rina.onepop.club.api.component.impl.ComponentSetting;
import rina.onepop.club.client.gui.overlay.ComponentClickGUI;

import java.awt.*;
import java.util.ArrayList;

/**
 * @author SrRina
 * @since 25/11/20 at 10:50pm
 */
public class SettingEnumWidget extends Widget {
    private ComponentClickGUI master;
    private ComponentListFrame frame;

    private ComponentWidget component;

    private int offsetX;
    private int offsetY;

    private int offsetWidth;
    private int offsetHeight;

    private ComponentSetting<Enum> setting;

    private int alphaAnimationPressed;

    private ArrayList<Enum> enumList;

    private int index;

    private boolean isMouseClickedLeft;
    private boolean isRendering;

    private boolean isStarted;

    public Flag flagMouse;

    public SettingEnumWidget(ComponentClickGUI master, ComponentListFrame frame, ComponentWidget component, final ComponentSetting<Enum> setting) {
        super(setting.getName());

        this.master = master;
        this.frame = frame;

        this.component = component;

        this.setting = setting;

        this.flagMouse = Flag.MOUSE_NOT_OVER;

        this.init();
    }

    public void init() {
        this.offsetX = 2;

        this.rect.setWidth(this.component.getRect().getWidth() - this.offsetX);
        this.rect.setHeight(3 + TurokFontManager.getStringHeight(this.master.fontWidgetComponent, this.rect.getTag()) + 3);

        this.enumList = new ArrayList<>();

        for (Enum enums : this.setting.getValue().getClass().getEnumConstants()) {
            enumList.add(enums);
        }

        this.isStarted = true;
    }

    public void setOffsetX(int offsetX) {
        this.offsetX = offsetX;
    }

    public int getOffsetX() {
        return offsetX;
    }

    public void setOffsetY(int offsetY) {
        this.offsetY = offsetY;
    }

    public int getOffsetY() {
        return offsetY;
    }

    public void setOffsetWidth(int offsetWidth) {
        this.offsetWidth = offsetWidth;
    }

    public int getOffsetWidth() {
        return offsetWidth;
    }

    public void setOffsetHeight(int offsetHeight) {
        this.offsetHeight = offsetHeight;
    }

    public int getOffsetHeight() {
        return offsetHeight;
    }

    public void setRendering(boolean rendering) {
        isRendering = rendering;
    }

    public boolean isRendering() {
        return isRendering;
    }

    @Override
    public void onScreenOpened() {

    }

    @Override
    public void onCustomScreenOpened() {

    }

    @Override
    public void onScreenClosed() {
        this.isMouseClickedLeft = false;
    }

    @Override
    public void onCustomScreenClosed() {

    }

    @Override
    public void onKeyboardPressed(char charCode, int keyCode) {

    }

    @Override
    public void onCustomKeyboardPressed(char charCode, int keyCode) {

    }

    @Override
    public void onMouseReleased(int button) {
        if (this.flagMouse == Flag.MOUSE_OVER) {
            if (this.isMouseClickedLeft) {
                if (this.index >= this.enumList.size() - 1) {
                    this.index = 0;
                } else {
                    this.index++;
                }

                this.isMouseClickedLeft = false;
            }
        } else {
            this.isMouseClickedLeft = false;
        }
    }

    @Override
    public void onCustomMouseReleased(int button) {

    }

    @Override
    public void onMouseClicked(int button) {
        if (this.flagMouse == Flag.MOUSE_OVER) {
            if (button == 0) {
                this.isMouseClickedLeft = true;
            }
        }
    }

    @Override
    public void onCustomMouseClicked(int button) {

    }

    @Override
    public void onRender() {
        this.rect.setX(this.component.getRect().getX() + this.offsetX);
        this.rect.setY(this.frame.getRect().getY() + this.component.getOffsetY() + this.offsetY);

        this.rect.setWidth(this.component.getRect().getWidth() - this.offsetX);

        if (this.isMouseClickedLeft) {
            this.alphaAnimationPressed = (int) TurokMath.serp(this.alphaAnimationPressed, this.master.guiColor.base[3], this.master.getDisplay().getPartialTicks());
        } else {
            this.alphaAnimationPressed = (int) TurokMath.serp(this.alphaAnimationPressed, 0, this.master.getDisplay().getPartialTicks());
        }

        TurokRenderGL.color(this.master.guiColor.base[0], this.master.guiColor.base[1], this.master.guiColor.base[2], this.alphaAnimationPressed);
        TurokRenderGL.drawSolidRect(this.rect);

        if (this.flagMouse == Flag.MOUSE_OVER) {
            TurokRenderGL.color(this.master.guiColor.highlight[0], this.master.guiColor.highlight[1], this.master.guiColor.highlight[2], this.master.guiColor.highlight[3]);
            TurokRenderGL.drawSolidRect(this.rect);
        }

        if (this.isStarted) {
            this.index = this.enumList.indexOf(this.setting.getValue()) != -1 ? this.enumList.indexOf(this.setting.getValue()) : 0;

            this.setting.setValue(this.enumList.get(this.index));

            this.isStarted = false;
        } else {
            this.setting.setValue(this.enumList.get(this.index));
        }

        String name = this.rect.getTag() + ": " + this.setting.getValue().name();

        TurokFontManager.render(this.master.fontWidgetComponent, name, this.rect.getX() + 1, this.rect.getY() + 3, true, new Color(255, 255, 255));
    }

    @Override
    public void onCustomRender() {
        this.flagMouse = this.rect.collideWithMouse(this.master.getMouse()) ? Flag.MOUSE_OVER : Flag.MOUSE_NOT_OVER;
    }
}
