package rina.onepop.club.client.gui.module.setting;

import me.rina.turok.render.font.management.TurokFontManager;
import me.rina.turok.render.opengl.TurokShaderGL;
import me.rina.turok.util.TurokMath;
import rina.onepop.club.api.gui.flag.Flag;
import rina.onepop.club.api.gui.widget.Widget;
import rina.onepop.club.api.setting.value.ValueString;
import rina.onepop.club.client.gui.imperador.ImperadorEntryBox;
import rina.onepop.club.client.gui.module.ModuleClickGUI;
import rina.onepop.club.client.gui.module.category.CategoryFrame;
import rina.onepop.club.client.gui.module.module.container.ModuleScrollContainer;
import rina.onepop.club.client.gui.module.module.widget.ModuleWidget;

/**
 * @author SrRina
 * @since 28/11/20 at 01:27pm
 */
public class SettingEntryBoxWidget extends Widget {
    private ModuleClickGUI master;
    private CategoryFrame frame;

    private ModuleScrollContainer container;
    private ModuleWidget widget;

    private int offsetX;
    private int offsetY;

    private int offsetWidth;
    private int offsetHeight;

    private int animationX;
    private int animationY;

    private int animationApplierY;

    private ValueString setting;
    private ImperadorEntryBox entryBox;

    private boolean isStarted;
    protected boolean isUpdate;

    public Flag flagMouse;
    public Flag flagAnimation;

    public SettingEntryBoxWidget(ModuleClickGUI master, CategoryFrame frame, ModuleScrollContainer container, ModuleWidget widget, ValueString setting) {
        super(setting.getName());

        this.setting = setting;

        this.master = master;
        this.frame = frame;

        this.container = container;
        this.widget = widget;

        this.flagMouse = Flag.MOUSE_NOT_OVER;
        this.flagAnimation = Flag.ANIMATION_END;
    }

    public void init() {
        this.entryBox = new ImperadorEntryBox(this.master.fontWidgetModule, this.setting.getValue());

        this.rect.setWidth(this.container.getRect().getWidth() - (this.offsetX * 2));
        this.rect.setHeight(3 + TurokFontManager.getStringHeight(this.master.fontWidgetModule, this.rect.getTag()) + 3);
    }

    @Override
    public boolean isEnabled() {
        return this.setting.isEnabled();
    }

    public ValueString getSetting() {
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
        this.entryBox.onScreenClosed();
    }

    @Override
    public void onCustomScreenClosed() {

    }

    @Override
    public void onKeyboardPressed(char charCode, int keyCode) {
        this.entryBox.onKeyboardPressed(charCode, keyCode);
    }

    @Override
    public void onCustomKeyboardPressed(char charCode, int keyCode) {

    }

    @Override
    public void onMouseReleased(int button) {
        this.entryBox.onMouseReleased(button);
    }

    @Override
    public void onCustomMouseReleased(int button) {

    }

    @Override
    public void onMouseClicked(int button) {
        if (this.entryBox.isFocused() && !this.entryBox.isMouseOver()) {
            this.entryBox.setFocused(false);
        }
    }

    @Override
    public void onCustomMouseClicked(int button) {
        this.entryBox.onMouseClicked(button);
        this.entryBox.doSetIndexAB(this.master.getMouse());
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

        this.entryBox.getRect().copy(this.rect);
        this.entryBox.setOffsetY(3f);
        this.entryBox.setRendering(true);
        this.entryBox.setIsShadow(false);
        this.entryBox.setToDrawOutline(false);

        if (this.flagMouse == Flag.MOUSE_OVER && !this.entryBox.isFocused()) {
            this.master.refreshDescriptionViewer(this.setting.getDescription());
        }

        if (this.isStarted) {
            /*
             * We can not just modify the setting out of GUI,
             * we need sync entry box using format from setting.
             */
            if (!this.setting.getFormat().isEmpty()) {
                this.entryBox.setText(setting.getFormat());
                this.setting.setFormat("");
            } else {
                this.setting.setValue(this.entryBox.getText());
            }
        } else {
            this.entryBox.setText(this.setting.getValue());
            this.isStarted = true;
        }

        this.entryBox.getScissor().set(this.rect.getX() < this.frame.getRect().getX() ? this.frame.getRect().getX() : this.rect.getX(), this.frame.getRect().getY() + this.frame.getOffsetHeight(), (this.rect.getX() + this.rect.getWidth() >= this.master.getClosedWidth() ? this.master.getClosedWidth() - (this.rect.getX() + this.rect.getWidth()) : this.rect.getWidth()), this.frame.getRect().getHeight() - this.frame.getOffsetHeight());
        this.entryBox.setPartialTicks(this.master.getDisplay().getPartialTicks());

        this.offsetHeight = (int) TurokMath.lerp(this.offsetHeight, this.entryBox.isFocused() ? 255 : 0, this.master.getDisplay().getPartialTicks());

        this.entryBox.setFont(this.master.fontWidgetModule);

        this.entryBox.setMouseOver(this.flagMouse == Flag.MOUSE_OVER);
        this.entryBox.onRender();

        if (this.entryBox.isFocused()) {
            this.master.setCanceledCloseGUI(true);
            this.entryBox.doMouseScroll(this.master.getMouse());

            this.entryBox.string = new int[] {
                    0, 0, 0, 255
            };

            this.isUpdate = true;
        } else {
            this.entryBox.string = new int[] {
                    255, 255, 255, 255
            };

            if (this.isUpdate) {
                this.master.setCanceledCloseGUI(false);
                this.isUpdate = false;
            }
        }

        TurokShaderGL.drawScissor(this.frame.getRect().getX(), this.frame.getRect().getY() + this.frame.getOffsetHeight(), (this.frame.getRect().getX() + this.frame.getRect().getWidth() >= this.master.getClosedWidth() ? this.master.getClosedWidth() - (this.frame.getRect().getX() + this.frame.getRect().getWidth()) : this.frame.getRect().getWidth()), this.frame.getRect().getHeight() - this.frame.getOffsetHeight());
    }

    @Override
    public void onCustomRender() {
        this.flagMouse = this.rect.collideWithMouse(this.master.getMouse()) ? Flag.MOUSE_OVER : Flag.MOUSE_NOT_OVER;
    }
}