package rina.onepop.club.client.gui.overlay.component.widget;

import rina.onepop.club.api.component.Component;
import rina.onepop.club.api.gui.flag.Flag;
import rina.onepop.club.api.gui.widget.Widget;
import me.rina.turok.render.font.management.TurokFontManager;
import me.rina.turok.render.opengl.TurokRenderGL;
import me.rina.turok.render.opengl.TurokShaderGL;
import me.rina.turok.util.TurokMath;
import rina.onepop.club.api.component.impl.ComponentSetting;
import rina.onepop.club.client.gui.overlay.ComponentClickGUI;
import rina.onepop.club.client.gui.overlay.component.frame.ComponentListFrame;
import rina.onepop.club.client.gui.overlay.setting.SettingBooleanWidget;
import rina.onepop.club.client.gui.overlay.setting.SettingEnumWidget;
import rina.onepop.club.client.gui.overlay.setting.SettingNumberWidget;

import java.awt.*;
import java.util.ArrayList;

/**
 * @author SrRina
 * @since 02/12/20 at 06:67pm
 */
public class ComponentWidget extends Widget {
    private ComponentClickGUI master;

    private ComponentListFrame frame;

    private rina.onepop.club.api.component.Component component;

    private int offsetX;
    private int offsetY;

    private int offsetWidth;
    private int offsetHeight;

    private int alphaAnimationPressed;

    private boolean isMouseClickedLeft;
    private boolean isMouseClickedRight;

    private boolean isWidgetOpened;

    private ArrayList<Widget> loadedWidgetList;

    /*
     * Flags to widget.
     */
    public Flag flagMouse;

    public ComponentWidget(ComponentClickGUI master, ComponentListFrame frame, rina.onepop.club.api.component.Component component) {
        super(component.getName());

        this.master = master;
        this.component = component;

        this.frame = frame;

        this.offsetX = 1;

        this.rect.setWidth(this.frame.getRect().getWidth() - (this.offsetX * 2));
        this.rect.setHeight(3 + TurokFontManager.getStringHeight(this.master.fontWidgetComponent, this.rect.getTag()) + 3);

        this.flagMouse = Flag.MOUSE_NOT_OVER;

        this.init();
    }

    public void init() {
        this.loadedWidgetList = new ArrayList<>();

        this.offsetHeight = (int) (this.rect.getHeight() + 1);

        for (ComponentSetting<?> settings : this.component.getSettingList()) {
            if (settings.getValue() instanceof Boolean) {
                ComponentSetting<Boolean> componentSetting = (ComponentSetting<Boolean>) settings;

                SettingBooleanWidget settingBooleanWidget = new SettingBooleanWidget(this.master, this.frame, this, componentSetting);
                settingBooleanWidget.setOffsetY(offsetHeight);

                this.loadedWidgetList.add(settingBooleanWidget);
                this.offsetHeight += settingBooleanWidget.getRect().getHeight() + 1;
            }

            if (settings.getValue() instanceof Number) {
                ComponentSetting<Number> componentSetting = (ComponentSetting<Number>) settings;

                SettingNumberWidget settingBooleanWidget = new SettingNumberWidget(this.master, this.frame, this, componentSetting);
                settingBooleanWidget.setOffsetY(offsetHeight);

                this.loadedWidgetList.add(settingBooleanWidget);
                this.offsetHeight += settingBooleanWidget.getRect().getHeight() + 1;
            }

            if (settings.getValue() instanceof Enum) {
                ComponentSetting<Enum> componentSetting = (ComponentSetting<Enum>) settings;

                SettingEnumWidget settingBooleanWidget = new SettingEnumWidget(this.master, this.frame, this, componentSetting);
                settingBooleanWidget.setOffsetY(offsetHeight);

                this.loadedWidgetList.add(settingBooleanWidget);
                this.offsetHeight += settingBooleanWidget.getRect().getHeight() + 1;
            }
        }
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

    public void setWidgetOpened(boolean widgetOpened) {
        isWidgetOpened = widgetOpened;
    }

    public boolean isWidgetOpened() {
        return isWidgetOpened;
    }

    public Component getComponent() {
        return component;
    }

    @Override
    public void onScreenOpened() {
        if (this.isWidgetOpened) {
            for (Widget widgets : this.loadedWidgetList) {
                widgets.onScreenOpened();
            }
        }
    }

    @Override
    public void onCustomScreenOpened() {
        if (this.isWidgetOpened) {
            for (Widget widgets : this.loadedWidgetList) {
                widgets.onCustomScreenOpened();
            }
        }
    }

    @Override
    public void onScreenClosed() {
        this.isMouseClickedLeft = false;

        if (this.isWidgetOpened) {
            for (Widget widgets : this.loadedWidgetList) {
                widgets.onScreenClosed();
            }
        }
    }

    @Override
    public void onCustomScreenClosed() {
        if (this.isWidgetOpened) {
            for (Widget widgets : this.loadedWidgetList) {
                widgets.onCustomScreenClosed();
            }
        }
    }

    @Override
    public void onMouseReleased(int button) {
        if (this.flagMouse == Flag.MOUSE_OVER) {
            if (this.isMouseClickedLeft) {
                this.component.setEnabled(!this.component.isEnabled());

                this.isMouseClickedLeft = false;
            }

            if (this.isMouseClickedRight) {
                this.isWidgetOpened = !this.isWidgetOpened;

                this.frame.refresh();

                this.isMouseClickedRight = false;
            }
        } else {
            this.isMouseClickedLeft = false;
            this.isMouseClickedRight = false;
        }

        if (this.isWidgetOpened) {
            for (Widget widgets : this.loadedWidgetList) {
                widgets.onMouseReleased(button);
            }
        }
    }

    @Override
    public void onCustomMouseReleased(int button) {
        if (this.isWidgetOpened) {
            for (Widget widgets : this.loadedWidgetList) {
                widgets.onCustomMouseReleased(button);
            }
        }
    }

    @Override
    public void onMouseClicked(int button) {
        if (this.flagMouse == Flag.MOUSE_OVER) {
            if (button == 0) {
                this.isMouseClickedLeft = true;
            }

            if (button == 1) {
                this.isMouseClickedRight = true;
            }
        }

        if (this.isWidgetOpened) {
            for (Widget widgets : this.loadedWidgetList) {
                widgets.onMouseClicked(button);
            }
        }
    }

    @Override
    public void onCustomMouseClicked(int button) {
        if (this.isWidgetOpened) {
            for (Widget widgets : this.loadedWidgetList) {
                widgets.onCustomMouseClicked(button);
            }
        }
    }

    @Override
    public void onRender() {
        this.rect.setX(this.frame.getRect().getX() + this.offsetX);
        this.rect.setY(this.frame.getRect().getY() + this.offsetY);

        this.rect.setWidth(this.frame.getRect().getWidth() - (this.offsetX * 2));

        if (this.component.isEnabled()) {
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

        if (this.isWidgetOpened) {
            for (Widget widgets : this.loadedWidgetList) {
                widgets.onRender();
            }

            // The setting opens...
            TurokShaderGL.drawSolidRectFadingMouse(this.rect.getX(), this.rect.getY() + this.rect.getHeight() + 1, 1, this.offsetHeight - (this.rect.getHeight() + 2), 50, new Color(this.master.guiColor.base[0], this.master.guiColor.base[1], this.master.guiColor.base[2], 255));
        }

        TurokFontManager.render(this.master.fontWidgetComponent, this.rect.getTag(), this.rect.getX() + 1, this.rect.getY() + 3, true, new Color(255, 255, 255, 255));
    }

    @Override
    public void onCustomRender() {
        this.flagMouse = this.rect.collideWithMouse(this.master.getMouse()) ? Flag.MOUSE_OVER : Flag.MOUSE_NOT_OVER;

        if (this.isWidgetOpened) {
            for (Widget widgets : this.loadedWidgetList) {
                widgets.onCustomRender();
            }
        }
    }
}