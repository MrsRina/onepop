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

/**
 * @author SrRina
 * @since 25/11/20 at 10:50pm
 */
public class SettingNumberWidget extends Widget {
    private ComponentClickGUI master;
    private ComponentListFrame frame;

    private ComponentWidget component;

    private int offsetX;
    private int offsetY;

    private double offsetWidth;
    private int offsetHeight;

    private double minimum;
    private double maximum;

    private double value;

    private ComponentSetting<Number> setting;

    private int alphaAnimationPressed;

    private boolean isMouseClickedLeft;
    private boolean isRendering;

    public Flag flagMouse;

    public SettingNumberWidget(ComponentClickGUI master, ComponentListFrame frame, ComponentWidget module, final ComponentSetting<Number> setting) {
        super(setting.getName());

        this.master = master;
        this.frame = frame;

        this.component = module;

        this.setting = setting;

        this.flagMouse = Flag.MOUSE_NOT_OVER;

        this.init();
    }

    public void init() {
        this.offsetX = 2;

        this.rect.setWidth(this.component.getRect().getWidth() - this.offsetX);
        this.rect.setHeight(3 + TurokFontManager.getStringHeight(this.master.fontWidgetComponent, this.rect.getTag()) + 3);
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

    public void setOffsetWidth(double offsetWidth) {
        this.offsetWidth = offsetWidth;
    }

    public double getOffsetWidth() {
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

        this.value = this.setting.getValue().doubleValue();

        this.maximum = this.setting.getMaximum().doubleValue();
        this.minimum = this.setting.getMinimum().doubleValue();

        this.offsetWidth = ((this.rect.getWidth()) * (this.value - this.minimum) / (this.maximum - this.minimum));

        double mouse = Math.min(this.rect.getWidth(), Math.max(0, this.master.getMouse().getX() - this.rect.getX()));

        if (this.isMouseClickedLeft) {
            if (mouse == 0) {
                this.setting.setValue(this.setting.getMinimum());
            } else {
                if (this.setting.getValue().getClass() == Integer.class) {
                    double rounded = TurokMath.round(((mouse / this.rect.getWidth()) * (this.maximum - this.minimum) + this.minimum));

                    Integer decimal = (int) rounded;

                    this.setting.setValue(decimal);
                } else if (this.setting.getValue().getClass() == Double.class) {
                    double rounded = TurokMath.round(((mouse / this.rect.getWidth()) * (this.maximum - this.minimum) + this.minimum));

                    Double decimal = (double) rounded;

                    this.setting.setValue(decimal);
                } else if (this.setting.getValue().getClass() == Float.class) {
                    double rounded = TurokMath.round(((mouse / this.rect.getWidth()) * (this.maximum - this.minimum) + this.minimum));

                    Float decimal = (float) rounded;

                    this.setting.setValue(decimal);
                } else if (this.setting.getValue().getClass() == Long.class) {
                    double rounded = TurokMath.round(((mouse / this.rect.getWidth()) * (this.maximum - this.minimum) + this.minimum));

                    Long decimal = (long) rounded;

                    this.setting.setValue(decimal);
                }
            }
        }

        TurokRenderGL.color(this.master.guiColor.base[0], this.master.guiColor.base[1], this.master.guiColor.base[2], this.master.guiColor.base[3]);
        TurokRenderGL.drawSolidRect(this.rect.getX(), this.rect.getY(), (float) this.offsetWidth, this.rect.getHeight());

        String name = this.rect.getTag() + " | " + this.setting.getValue().toString();

        if (this.flagMouse == Flag.MOUSE_OVER) {
            TurokRenderGL.color(this.master.guiColor.highlight[0], this.master.guiColor.highlight[1], this.master.guiColor.highlight[2], this.master.guiColor.highlight[3]);
            TurokRenderGL.drawSolidRect(this.rect);
        }

        TurokFontManager.render(this.master.fontWidgetComponent, name, this.rect.getX() + 1, this.rect.getY() + 3, true, new Color(255, 255, 255));
    }

    @Override
    public void onCustomRender() {
        this.flagMouse = this.rect.collideWithMouse(this.master.getMouse()) ? Flag.MOUSE_OVER : Flag.MOUSE_NOT_OVER;
    }
}
