package rina.onepop.club.client.gui.module.setting;

import rina.onepop.club.api.gui.flag.Flag;
import rina.onepop.club.api.gui.widget.Widget;
import rina.onepop.club.api.setting.value.InputType;
import rina.onepop.club.api.setting.value.ValueBind;
import rina.onepop.club.client.gui.module.category.CategoryFrame;
import me.rina.turok.render.font.management.TurokFontManager;
import me.rina.turok.render.opengl.TurokRenderGL;
import me.rina.turok.util.TurokMath;
import rina.onepop.club.client.gui.module.ModuleClickGUI;
import rina.onepop.club.client.gui.module.module.container.ModuleScrollContainer;
import rina.onepop.club.client.gui.module.module.widget.ModuleWidget;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.awt.*;

/**
 * @author SrRina
 * @since 28/11/20 at 01:27pm
 */
public class SettingBindWidget extends Widget {
    private ModuleClickGUI master;
    private CategoryFrame frame;

    private ModuleScrollContainer container;
    private ModuleWidget widget;

    private int offsetX;
    private int offsetY;

    private int offsetWidth;
    private int offsetHeight;

    private int alphaAnimationPressed;

    private int animationX;
    private int animationY;

    private int animationApplierY;

    private boolean isMouseClickedLeft;
    private boolean isMouseClickedRight;

    private boolean isBinding;
    protected boolean isUpdate;

    private ValueBind setting;

    public Flag flagMouse;
    public Flag flagAnimation;

    public SettingBindWidget(ModuleClickGUI master, CategoryFrame frame, ModuleScrollContainer container, ModuleWidget widget, ValueBind setting) {
        super(setting.getName());

        this.setting = setting;

        this.master = master;
        this.frame = frame;

        this.container = container;
        this.widget = widget;

        this.rect.setWidth(this.container.getRect().getWidth() - (this.offsetX * 2));
        this.rect.setHeight(3 + TurokFontManager.getStringHeight(this.master.fontWidgetModule, this.rect.getTag()) + 3);

        this.flagMouse = Flag.MOUSE_NOT_OVER;
        this.flagAnimation = Flag.ANIMATION_END;
    }

    @Override
    public boolean isEnabled() {
        return this.setting.isEnabled();
    }

    public ValueBind getSetting() {
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
        switch (keyCode) {
            case Keyboard.KEY_ESCAPE: {
                if (this.isBinding) {
                    this.isBinding = false;
                }

                break;
            }
        }
    }

    @Override
    public void onCustomKeyboardPressed(char charCode, int keyCode) {
        if (this.isBinding) {
            switch (keyCode) {
                case Keyboard.KEY_ESCAPE: {
                    break;
                }

                case Keyboard.KEY_DELETE : {
                    this.isBinding = false;

                    this.setting.setKeyCode(-1);
                    this.setting.setInputType(InputType.KEYBOARD);

                    break;
                }

                default : {
                    this.isBinding = false;

                    this.setting.setKeyCode(keyCode);
                    this.setting.setInputType(InputType.KEYBOARD);

                    break;
                }
            }
        }
    }

    @Override
    public void onMouseReleased(int button) {
        if (this.isBinding && (button == 0 || button == 1 || button == 2) && this.flagMouse == Flag.MOUSE_NOT_OVER) {
            this.isBinding = false;
        }
    }

    @Override
    public void onCustomMouseReleased(int button) {
        if (this.flagMouse == Flag.MOUSE_OVER && button != 2 && this.isBinding) {
            this.setting.setKeyCode(button);
            this.setting.setInputType(InputType.MOUSE);

            this.isBinding = false;

            this.isMouseClickedLeft = false;
            this.isMouseClickedRight = false;
        } else {
            if (this.flagMouse == Flag.MOUSE_OVER) {
                if (this.isMouseClickedLeft) {
                    this.isBinding = true;
                    this.isMouseClickedLeft = false;
                }

                if (this.isMouseClickedRight) {
                    this.setting.setValue(!this.setting.getValue());
                    this.isMouseClickedRight = false;
                }
            } else {
                this.isMouseClickedLeft = false;
                this.isMouseClickedRight = false;
            }
        }
    }

    @Override
    public void onMouseClicked(int button) {
        if (this.isBinding && (button == 0 || button == 1 || button == 2) && this.flagMouse == Flag.MOUSE_NOT_OVER) {
            this.isBinding = false;
        }
    }

    @Override
    public void onCustomMouseClicked(int button) {
        if (this.flagMouse == Flag.MOUSE_OVER) {
            if (button == 0) {
                this.isMouseClickedLeft = true;
            }

            if (button == 1) {
                this.isMouseClickedRight = true;
            }
        }
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

        this.rect.setX(this.widget.getRect().getX() + this.animationX);
        this.rect.setY(this.container.getRect().getY() + this.widget.getOffsetY() + this.animationApplierY);

        this.rect.setWidth(this.container.getRect().getWidth() - this.offsetX);

        if (this.setting.getValue()) {
            this.alphaAnimationPressed = (int) TurokMath.lerp(this.alphaAnimationPressed, this.master.guiColor.base[3], this.master.getDisplay().getPartialTicks());
        } else {
            this.alphaAnimationPressed = (int) TurokMath.lerp(this.alphaAnimationPressed, 0, this.master.getDisplay().getPartialTicks());
        }

        if (this.isBinding) {
            this.master.setCanceledCloseGUI(true);
            this.isUpdate = true;
        } else {
            if (this.isUpdate) {
                this.master.setCanceledCloseGUI(false);
                this.isUpdate = false;
            }
        }

        TurokRenderGL.color(this.master.guiColor.base[0], this.master.guiColor.base[1], this.master.guiColor.base[2], this.alphaAnimationPressed);
        TurokRenderGL.drawSolidRect(this.rect);

        if (this.flagMouse == Flag.MOUSE_OVER) {
            TurokRenderGL.color(this.master.guiColor.highlight[0], this.master.guiColor.highlight[1], this.master.guiColor.highlight[2], this.master.guiColor.highlight[3]);
            TurokRenderGL.drawSolidRect(this.rect);

            this.master.refreshDescriptionViewer(this.setting.getDescription());
        }

        String keyCodeName = this.setting.getName() + " " + (this.isBinding ? "<Binding>" : (this.setting.getKeyCode() != -1 ? ("<" + (this.setting.getInputType() == InputType.KEYBOARD ? Keyboard.getKeyName(this.setting.getKeyCode()) : Mouse.getButtonName(this.setting.getKeyCode())) + ">") : "<NONE>"));

        /*
         * Render setting name.
         */
        TurokFontManager.render(this.master.fontWidgetModule, keyCodeName, this.rect.getX() + 1, this.rect.getY() + 3, true, new Color(255, 255, 255));
    }

    @Override
    public void onCustomRender() {
        this.flagMouse = this.rect.collideWithMouse(this.master.getMouse()) ? Flag.MOUSE_OVER : Flag.MOUSE_NOT_OVER;
    }
}