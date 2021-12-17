package rina.onepop.club.client.gui.module.module.widget;

import me.rina.turok.render.font.management.TurokFontManager;
import me.rina.turok.render.opengl.TurokRenderGL;
import me.rina.turok.render.opengl.TurokShaderGL;
import me.rina.turok.util.TurokMath;
import rina.onepop.club.Onepop;
import rina.onepop.club.api.gui.flag.Flag;
import rina.onepop.club.api.gui.widget.Widget;
import rina.onepop.club.api.module.Module;
import rina.onepop.club.api.setting.Setting;
import rina.onepop.club.api.setting.value.*;
import rina.onepop.club.client.Wrapper;
import rina.onepop.club.client.gui.module.ModuleClickGUI;
import rina.onepop.club.client.gui.module.category.CategoryFrame;
import rina.onepop.club.client.gui.module.module.container.ModuleScrollContainer;
import rina.onepop.club.client.gui.module.setting.*;

import java.awt.*;
import java.util.ArrayList;

/**
 * @author SrRina
 * @since 20/11/20 at 12:38pm
 */
public class ModuleWidget extends Widget {
    private ModuleClickGUI master;
    private CategoryFrame frame;

    private ModuleScrollContainer container;
    private Module module;

    private int offsetX;
    private int offsetY;

    private int offsetWidth;
    private int offsetHeight;

    private int animationX;
    private int animationY;

    private boolean isWidgetOpened;
    private int alphaAnimationPressed;

    private boolean isMouseClickedLeft;
    private boolean isMouseClickedRight;

    private ArrayList<Widget> loadedWidgetList;

    /*
     * Flags to widget.
     */
    public Flag flagMouse;

    public ModuleWidget(ModuleClickGUI master, CategoryFrame frame, ModuleScrollContainer container, Module module) {
        super(module.getName());

        this.master = master;
        this.module = module;

        this.container = container;
        this.frame = frame;

        this.rect.setWidth(this.container.getRect().getWidth() - (this.offsetX * 2));
        this.rect.setHeight(3 + TurokFontManager.getStringHeight(this.master.fontWidgetModule, this.rect.getTag()) + 3);

        this.flagMouse = Flag.MOUSE_NOT_OVER;

        this.init();
    }

    public void init() {
        this.loadedWidgetList = new ArrayList<>();
        this.offsetHeight = (int) (this.rect.getHeight() + 1);

        for (Setting settings : this.module.getSettingList()) {
            if (settings instanceof ValueBind) {
                SettingBindWidget settingBindWidget = new SettingBindWidget(this.master, this.frame, this.container, this, (ValueBind) settings);

                settingBindWidget.setOffsetY(this.offsetHeight);
                settingBindWidget.setAnimationX(2);

                this.loadedWidgetList.add(settingBindWidget);
                this.offsetHeight += settingBindWidget.getRect().getHeight() + 1;
            }

            if (settings instanceof ValueBoolean) {
                SettingBooleanWidget settingBooleanWidget = new SettingBooleanWidget(this.master, this.frame, this.container, this, (ValueBoolean) settings);

                settingBooleanWidget.setOffsetY(this.offsetHeight);
                settingBooleanWidget.setAnimationX(2);

                this.loadedWidgetList.add(settingBooleanWidget);
                this.offsetHeight += settingBooleanWidget.getRect().getHeight() + 1;
            }

            if (settings instanceof ValueNumber) {
                SettingNumberWidget settingNumberWidget = new SettingNumberWidget(this.master, this.frame, this.container, this, (ValueNumber) settings);

                settingNumberWidget.setOffsetY(this.offsetHeight);
                settingNumberWidget.setAnimationX(2);

                this.loadedWidgetList.add(settingNumberWidget);
                this.offsetHeight += settingNumberWidget.getRect().getHeight() + 1;
            }

            if (settings instanceof ValueEnum) {
                SettingEnumWidget settingEnumWidget = new SettingEnumWidget(this.master, this.frame, this.container, this, (ValueEnum) settings);

                settingEnumWidget.setOffsetY(this.offsetHeight);
                settingEnumWidget.setAnimationX(2);

                this.loadedWidgetList.add(settingEnumWidget);
                this.offsetHeight += settingEnumWidget.getRect().getHeight() + 1;
            }

            if (settings instanceof ValueString) {
                SettingEntryBoxWidget settingEntryBoxWidget = new SettingEntryBoxWidget(this.master, this.frame, this.container, this, (ValueString) settings);

                settingEntryBoxWidget.init();

                settingEntryBoxWidget.setOffsetY(this.offsetHeight);
                settingEntryBoxWidget.setAnimationX(2);

                this.loadedWidgetList.add(settingEntryBoxWidget);
                this.offsetHeight += settingEntryBoxWidget.getRect().getHeight() + 1;
            }

            if (settings instanceof ValueColor) {
                SettingColorPickerWidget settingColorPickerWidget = new SettingColorPickerWidget(this.master, this.frame, this.container, this, (ValueColor) settings);

                settingColorPickerWidget.setOffsetY(this.offsetHeight);
                settingColorPickerWidget.setAnimationX(2);

                this.loadedWidgetList.add(settingColorPickerWidget);
                this.offsetHeight += settingColorPickerWidget.getRect().getHeight() + 1;
            }
        }
    }

    public ArrayList<Widget> getLoadedWidgetList() {
        return loadedWidgetList;
    }

    public void refresh(Setting setting) {
        this.offsetHeight = (int) (this.rect.getHeight() + 1);
        this.module.onSetting();
        this.module.onSync();

        int counterFlag = 0;

        for (Widget widgets : this.loadedWidgetList) {
            if (widgets instanceof SettingBindWidget) {
                SettingBindWidget settingBindWidget = (SettingBindWidget) widgets;
                settingBindWidget.setAnimationY(this.offsetHeight);

                if (setting != null && settingBindWidget.getSetting().getOld() == settingBindWidget.getSetting().isEnabled()) {
                    settingBindWidget.setAnimationApplierY(this.offsetHeight);
                }

                int flag = Wrapper.FLAG_COMPONENT_CLOSED;

                if (settingBindWidget.getSetting().isEnabled()) {
                    settingBindWidget.getSetting().updateSetting();

                    this.offsetHeight += settingBindWidget.getRect().getHeight() + 1;

                    flag = Wrapper.FLAG_COMPONENT_OPENED;
                }

                settingBindWidget.setAnimationX(flag);
            }

            if (widgets instanceof SettingBooleanWidget) {
                SettingBooleanWidget settingBooleanWidget = (SettingBooleanWidget) widgets;
                settingBooleanWidget.setAnimationY(this.offsetHeight);

                if (setting != null && settingBooleanWidget.getSetting().getOld() == settingBooleanWidget.getSetting().isEnabled()) {
                    settingBooleanWidget.setAnimationApplierY(this.offsetHeight);
                }

                int flag = Wrapper.FLAG_COMPONENT_CLOSED;

                if (settingBooleanWidget.getSetting().isEnabled()) {
                    settingBooleanWidget.getSetting().updateSetting();

                    this.offsetHeight += settingBooleanWidget.getRect().getHeight() + 1;

                    flag = Wrapper.FLAG_COMPONENT_OPENED;
                }

                settingBooleanWidget.setAnimationX(flag);
            }

            if (widgets instanceof SettingNumberWidget) {
                SettingNumberWidget settingNumberWidget = (SettingNumberWidget) widgets;
                settingNumberWidget.setAnimationY(this.offsetHeight);

                if (setting != null && settingNumberWidget.getSetting().getOld() == settingNumberWidget.getSetting().isEnabled()) {
                    settingNumberWidget.setAnimationApplierY(this.offsetHeight);
                }

                int flag = Wrapper.FLAG_COMPONENT_CLOSED;

                if (settingNumberWidget.getSetting().isEnabled()) {
                    settingNumberWidget.getSetting().updateSetting();

                    this.offsetHeight += settingNumberWidget.getRect().getHeight() + 1;

                    flag = Wrapper.FLAG_COMPONENT_OPENED;
                }

                settingNumberWidget.setAnimationX(flag);
            }

            if (widgets instanceof SettingEnumWidget) {
                SettingEnumWidget settingEnumWidget = (SettingEnumWidget) widgets;
                settingEnumWidget.setAnimationY(this.offsetHeight);

                if (setting != null && settingEnumWidget.getSetting().getOld() == settingEnumWidget.getSetting().isEnabled()) {
                    settingEnumWidget.setAnimationApplierY(this.offsetHeight);
                }

                int flag = Wrapper.FLAG_COMPONENT_CLOSED;

                if (settingEnumWidget.getSetting().isEnabled()) {
                    settingEnumWidget.getSetting().updateSetting();

                    this.offsetHeight += settingEnumWidget.getRect().getHeight() + 1;

                    flag = Wrapper.FLAG_COMPONENT_OPENED;
                }

                settingEnumWidget.setAnimationX(flag);
            }

            if (widgets instanceof SettingEntryBoxWidget) {
                SettingEntryBoxWidget settingEntryBoxWidget = (SettingEntryBoxWidget) widgets;
                settingEntryBoxWidget.setAnimationY(this.offsetHeight);

                if (setting != null && settingEntryBoxWidget.getSetting().getOld() == settingEntryBoxWidget.getSetting().isEnabled()) {
                    settingEntryBoxWidget.setAnimationApplierY(this.offsetHeight);
                }

                int flag = Wrapper.FLAG_COMPONENT_CLOSED;

                if (settingEntryBoxWidget.getSetting().isEnabled()) {
                    settingEntryBoxWidget.getSetting().updateSetting();

                    this.offsetHeight += settingEntryBoxWidget.getRect().getHeight() + 1;

                    flag = Wrapper.FLAG_COMPONENT_OPENED;
                }

                settingEntryBoxWidget.setAnimationX(flag);
            }

            if (widgets instanceof SettingColorPickerWidget) {
                SettingColorPickerWidget settingColorPickerWidget = (SettingColorPickerWidget) widgets;
                settingColorPickerWidget.setAnimationY(this.offsetHeight);

                if (setting != null && settingColorPickerWidget.getSetting().getOld() == settingColorPickerWidget.getSetting().isEnabled()) {
                    settingColorPickerWidget.setAnimationApplierY(this.offsetHeight);
                }

                int flag = Wrapper.FLAG_COMPONENT_CLOSED;

                if (settingColorPickerWidget.getSetting().isEnabled()) {
                    settingColorPickerWidget.getSetting().updateSetting();

                    this.offsetHeight += settingColorPickerWidget.getRect().getHeight() + 1;

                    flag = Wrapper.FLAG_COMPONENT_OPENED;
                }

                settingColorPickerWidget.setAnimationX(flag);
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

    public Module getModule() {
        return module;
    }

    public void setAnimationX(int animationX) {
        this.animationX = animationX;
    }

    public int getAnimationX() {
        return animationX;
    }

    public void setAnimationY(int animationY) {
        this.animationY = animationY;
    }

    public int getAnimationY() {
        return animationY;
    }

    @Override
    public void onScreenOpened() {
        if (this.isWidgetOpened) {
            for (Widget widgets : this.loadedWidgetList) {
                if (widgets.isEnabled()) {
                    widgets.onScreenOpened();
                }
            }
        }
    }

    @Override
    public void onCustomScreenOpened() {
        if (this.isWidgetOpened) {
            for (Widget widgets : this.loadedWidgetList) {
                if (widgets.isEnabled()) {
                    widgets.onCustomScreenOpened();
                }
            }
        }
    }

    @Override
    public void onScreenClosed() {
        this.isMouseClickedLeft = false;

        if (this.isWidgetOpened) {
            for (Widget widgets : this.loadedWidgetList) {
                if (widgets.isEnabled()) {
                    widgets.onScreenClosed();
                }
            }
        }
    }

    @Override
    public void onCustomScreenClosed() {
        if (this.isWidgetOpened) {
            for (Widget widgets : this.loadedWidgetList) {
                if (widgets.isEnabled()) {
                    widgets.onCustomScreenClosed();
                }
            }
        }
    }

    @Override
    public void onKeyboardPressed(char charCode, int keyCode) {
        if (this.isWidgetOpened) {
            for (Widget widgets : this.loadedWidgetList) {
                if (widgets.isEnabled()) {
                    widgets.onKeyboardPressed(charCode, keyCode);
                }
            }
        }
    }

    @Override
    public void onCustomKeyboardPressed(char charCode, int keyCode) {
        if (this.isWidgetOpened) {
            for (Widget widgets : this.loadedWidgetList) {
                if (widgets.isEnabled()) {
                    widgets.onCustomKeyboardPressed(charCode, keyCode);
                }
            }
        }
    }

    @Override
    public void onMouseReleased(int button) {
        if (this.flagMouse == Flag.MOUSE_OVER) {
            if (this.isMouseClickedLeft) {
                this.module.toggle();

                this.isMouseClickedLeft = false;
            }

            if (this.isMouseClickedRight) {
                this.isWidgetOpened = !this.isWidgetOpened;

                this.container.refresh(this.getModule(), null);
                this.isMouseClickedRight = false;
            }
        } else {
            this.isMouseClickedLeft = false;
            this.isMouseClickedRight = false;
        }

        if (this.isWidgetOpened) {
            for (Widget widgets : this.loadedWidgetList) {
                if (widgets.isEnabled()) {
                    widgets.onMouseReleased(button);
                }
            }
        }
    }

    @Override
    public void onCustomMouseReleased(int button) {
        if (this.isWidgetOpened) {
            for (Widget widgets : this.loadedWidgetList) {
                if (widgets.isEnabled()) {
                    widgets.onCustomMouseReleased(button);
                }
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
                if (widgets.isEnabled()) {
                    widgets.onMouseClicked(button);
                }
            }
        }
    }

    @Override
    public void onCustomMouseClicked(int button) {
        if (this.isWidgetOpened) {
            for (Widget widgets : this.loadedWidgetList) {
                if (widgets.isEnabled()) {
                    widgets.onCustomMouseClicked(button);
                }
            }
        }
    }

    @Override
    public void onRender() {
        double diffValue = this.offsetY - this.animationY;
        double diffFinal = TurokMath.sqrt(diffValue * diffValue);

        if (diffFinal < 20f) {
            this.offsetY = this.animationY;
        } else {
            this.offsetY = (int) TurokMath.serp(this.offsetY, this.animationY, this.master.getDisplay().getPartialTicks());
        }

        this.rect.setX(this.container.getRect().getX() + this.offsetX);
        this.rect.setY(this.container.getRect().getY() + this.offsetY);

        this.rect.setWidth(this.container.getRect().getWidth() - (this.offsetX * 2));

        if (this.module.isEnabled()) {
            this.alphaAnimationPressed = (int) TurokMath.serp(this.alphaAnimationPressed, this.master.guiColor.base[3], this.master.getDisplay().getPartialTicks());
        } else {
            this.alphaAnimationPressed = (int) TurokMath.serp(this.alphaAnimationPressed, 0, this.master.getDisplay().getPartialTicks());
        }

        TurokRenderGL.color(this.master.guiColor.base[0], this.master.guiColor.base[1], this.master.guiColor.base[2], this.alphaAnimationPressed);
        TurokRenderGL.drawSolidRect(this.rect);

        if (this.flagMouse == Flag.MOUSE_OVER) {
            TurokRenderGL.color(this.master.guiColor.highlight[0], this.master.guiColor.highlight[1], this.master.guiColor.highlight[2], this.master.guiColor.highlight[3]);
            TurokRenderGL.drawSolidRect(this.rect);

            this.master.refreshDescriptionViewer(this.module.getDescription());
        }

        /*
         * Render module name.
         */
        TurokFontManager.render(Onepop.getWrapper().fontSmallWidget, this.rect.getTag(), this.rect.getX() + 1, this.rect.getY() + 3, true, new Color(255, 255, 255));

        if (this.isWidgetOpened) {
            TurokShaderGL.drawSolidRectFadingMouse(this.rect.getX(), this.rect.getY() + this.rect.getHeight() + 1, 1, this.offsetHeight - (this.rect.getHeight() + 2), 50, new Color(this.master.guiColor.base[0], this.master.guiColor.base[1], this.master.guiColor.base[2], 255));

            for (Widget widgets : this.loadedWidgetList) {
                widgets.onRender();

                if (widgets instanceof SettingBindWidget) {
                    ((SettingBindWidget) widgets).flagMouse = Flag.MOUSE_NOT_OVER;
                }

                if (widgets instanceof SettingBooleanWidget) {
                    ((SettingBooleanWidget) widgets).flagMouse = Flag.MOUSE_NOT_OVER;
                }

                if (widgets instanceof SettingNumberWidget) {
                    ((SettingNumberWidget) widgets).flagMouse = Flag.MOUSE_NOT_OVER;
                }

                if (widgets instanceof SettingEnumWidget) {
                    ((SettingEnumWidget) widgets).flagMouse = Flag.MOUSE_NOT_OVER;
                }

                if (widgets instanceof SettingEntryBoxWidget) {
                    ((SettingEntryBoxWidget) widgets).flagMouse = Flag.MOUSE_NOT_OVER;
                }

                if (widgets instanceof SettingColorPickerWidget) {
                    ((SettingColorPickerWidget) widgets).flagMouse = Flag.MOUSE_NOT_OVER;
                }
            }
        }
    }

    @Override
    public void onCustomRender() {
        this.flagMouse = this.rect.collideWithMouse(this.master.getMouse()) ? Flag.MOUSE_OVER : Flag.MOUSE_NOT_OVER;

        if (this.isWidgetOpened) {
            for (Widget widgets : this.loadedWidgetList) {
                if (widgets.isEnabled()) {
                    widgets.onCustomRender();
                }
            }
        }
    }
}
