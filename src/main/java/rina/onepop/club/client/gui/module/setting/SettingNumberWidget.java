package rina.onepop.club.client.gui.module.setting;

import rina.onepop.club.api.gui.flag.Flag;
import rina.onepop.club.api.gui.widget.Widget;
import rina.onepop.club.api.setting.value.Smooth;
import rina.onepop.club.api.setting.value.ValueNumber;
import rina.onepop.club.client.gui.module.category.CategoryFrame;
import me.rina.turok.render.font.management.TurokFontManager;
import me.rina.turok.render.opengl.TurokRenderGL;
import me.rina.turok.util.TurokMath;
import rina.onepop.club.client.gui.module.ModuleClickGUI;
import rina.onepop.club.client.gui.module.module.container.ModuleScrollContainer;
import rina.onepop.club.client.gui.module.module.widget.ModuleWidget;

import java.awt.*;

/**
 * @author SrRina
 * @since 25/11/20 at 10:50pm
 */
public class SettingNumberWidget extends Widget {
    private ModuleClickGUI master;
    private CategoryFrame frame;

    private ModuleScrollContainer container;
    private ModuleWidget module;

    private int offsetX;
    private int offsetY;

    private double offsetWidth;
    private int offsetHeight;

    private int animationX;
    private int animationY;

    private int animationApplierY;

    private final ValueNumber setting;
    private int offsetAnimation;

    private boolean isMouseClickedLeft;
    private boolean isRendering;

    public Flag flagMouse;

    public SettingNumberWidget(ModuleClickGUI master, CategoryFrame frame, ModuleScrollContainer container, ModuleWidget module, final ValueNumber setting) {
        super(setting.getName());

        this.master = master;
        this.frame = frame;

        this.container = container;
        this.module = module;

        this.setting = setting;
        this.flagMouse = Flag.MOUSE_NOT_OVER;

        this.init();
    }

    public void init() {
        this.rect.setWidth(this.module.getRect().getWidth() - this.offsetX);
        this.rect.setHeight(3 + TurokFontManager.getStringHeight(this.master.fontWidgetModule, this.rect.getTag()) + 3);
    }

    @Override
    public boolean isEnabled() {
        return this.setting.isEnabled();
    }

    public ValueNumber getSetting() {
        return setting;
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

    public void setAnimationApplierY(int animationApplierY) {
        this.animationApplierY = animationApplierY;
    }

    public int getAnimationApplierY() {
        return animationApplierY;
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
        double diffValue = this.animationApplierY - this.animationY;
        double diffFinal = TurokMath.sqrt(diffValue * diffValue);

        if (diffFinal < 10f) {
            this.animationApplierY = this.animationY;
        } else {
            this.animationApplierY = (int) TurokMath.serp(this.animationApplierY, this.animationY, this.master.getDisplay().getPartialTicks());
        }

        this.offsetX = 2;

        this.rect.setX(this.module.getRect().getX() + this.animationX);
        this.rect.setY(this.container.getRect().getY() + this.module.getOffsetY() + this.animationApplierY);

        this.rect.setWidth(this.container.getRect().getWidth() - this.offsetX);

        double value = this.setting.getValue().floatValue();

        float maximum = this.setting.getMaximum().floatValue();
        float minimum = this.setting.getMinimum().floatValue();

        this.offsetWidth = ((this.rect.getWidth()) * (value - minimum) / (maximum - minimum));

        float mouse = Math.min(this.rect.getWidth(), Math.max(0, this.master.getMouse().getX() - this.rect.getX()));

        if (this.isMouseClickedLeft) {
            if (mouse == 0) {
                this.setting.setValue(this.setting.getMinimum());
            } else {
                if (this.setting.getValue() instanceof Integer) {
                    int roundedValue = (int) TurokMath.round(((mouse / this.rect.getWidth()) * (maximum - minimum) + minimum));

                    this.setting.setValue(roundedValue);
                } else if (this.setting.getValue() instanceof Double) {
                    double roundedValue = TurokMath.round(((mouse / this.rect.getWidth()) * (maximum - minimum) + minimum));

                    this.setting.setValue(roundedValue);
                } else if (this.setting.getValue() instanceof Float) {
                    float roundedValue = (float) TurokMath.round(((mouse / this.rect.getWidth()) * (maximum - minimum) + minimum));

                    this.setting.setValue(roundedValue);
                }
            }
        }

        TurokRenderGL.color(this.master.guiColor.base[0], this.master.guiColor.base[1], this.master.guiColor.base[2], this.master.guiColor.base[3]);
        TurokRenderGL.drawSolidRect(this.rect.getX(), this.rect.getY(), (float) this.offsetWidth, this.rect.getHeight());

        String currentSettingValue = this.setting.getSmooth() == Smooth.INTEGER ? ((int) (this.setting.getValue().floatValue())) + "" : this.setting.getValue().toString();
        String name = this.rect.getTag() + " | " + currentSettingValue;

        if (this.flagMouse == Flag.MOUSE_OVER) {
            TurokRenderGL.color(this.master.guiColor.highlight[0], this.master.guiColor.highlight[1], this.master.guiColor.highlight[2], this.master.guiColor.highlight[3]);
            TurokRenderGL.drawSolidRect(this.rect);

            this.master.refreshDescriptionViewer(this.setting.getDescription());
        }

        /*
         * Render module name.
         */
        TurokFontManager.render(this.master.fontWidgetModule, name, this.rect.getX() + 1 + this.offsetAnimation, this.rect.getY() + 3, true, new Color(255, 255, 255));
    }

    @Override
    public void onCustomRender() {
        this.flagMouse = this.rect.collideWithMouse(this.master.getMouse()) ? Flag.MOUSE_OVER : Flag.MOUSE_NOT_OVER;
    }
}
