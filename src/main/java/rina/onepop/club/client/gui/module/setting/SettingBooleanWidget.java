package rina.onepop.club.client.gui.module.setting;

import rina.onepop.club.api.gui.flag.Flag;
import rina.onepop.club.api.gui.widget.Widget;
import rina.onepop.club.api.setting.value.ValueBoolean;
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
public class SettingBooleanWidget extends Widget {
    private ModuleClickGUI master;
    private CategoryFrame frame;

    private ModuleScrollContainer container;
    private ModuleWidget module;

    private int offsetX;
    private int offsetY;

    private int offsetWidth;
    private int offsetHeight;

    private int animationX;
    private int animationY;

    private int animationApplierY;

    private ValueBoolean setting;
    private int alphaAnimationPressed;

    private boolean isMouseClickedLeft;
    private boolean isRendering;

    public Flag flagMouse;

    public SettingBooleanWidget(ModuleClickGUI master, CategoryFrame frame, ModuleScrollContainer container, ModuleWidget module, final ValueBoolean setting) {
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

    public ValueBoolean getSetting() {
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
                this.setting.setValue(!this.setting.getValue());
                this.container.refresh(this.module.getModule(), this.setting);

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

        if (this.setting.getValue() && this.module.isWidgetOpened()) {
            this.alphaAnimationPressed = (int) TurokMath.lerp(this.alphaAnimationPressed, this.master.guiColor.base[3], this.master.getDisplay().getPartialTicks());
        } else {
            this.alphaAnimationPressed = (int) TurokMath.lerp(this.alphaAnimationPressed, 0, this.master.getDisplay().getPartialTicks());
        }

        TurokRenderGL.color(this.master.guiColor.base[0], this.master.guiColor.base[1], this.master.guiColor.base[2], this.alphaAnimationPressed);
        TurokRenderGL.drawSolidRect(this.rect);

        if (this.flagMouse == Flag.MOUSE_OVER) {
            TurokRenderGL.color(this.master.guiColor.highlight[0], this.master.guiColor.highlight[1], this.master.guiColor.highlight[2], this.master.guiColor.highlight[3]);
            TurokRenderGL.drawSolidRect(this.rect);

            this.master.refreshDescriptionViewer(this.setting.getDescription());
        }

        /*
         * Render module name.
         */
        TurokFontManager.render(this.master.fontWidgetModule, this.rect.getTag(), this.rect.getX() + 1, this.rect.getY() + 3, true, new Color(255, 255, 255));
    }

    @Override
    public void onCustomRender() {
        this.flagMouse = this.rect.collideWithMouse(this.master.getMouse()) ? Flag.MOUSE_OVER : Flag.MOUSE_NOT_OVER;
    }
}