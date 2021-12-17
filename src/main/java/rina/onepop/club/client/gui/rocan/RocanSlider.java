package rina.onepop.club.client.gui.rocan;

import me.rina.turok.hardware.mouse.TurokMouse;
import me.rina.turok.render.font.TurokFont;
import me.rina.turok.render.opengl.TurokRenderGL;
import me.rina.turok.util.TurokMath;
import rina.onepop.club.api.gui.flag.Flag;
import rina.onepop.club.api.gui.widget.Widget;

/**
 * @author SrRina
 * @since 06/07/2021 at 14:52
 **/
public class RocanSlider extends Widget {
    private boolean isRendering = true;
    private boolean isDisabled;

    private float partialTicks;

    private float value;

    private float minimum;
    private float maximum;

    /* The offset space. */
    private float offsetX;
    private float offsetY;

    private float offsetW;
    private float offsetH;

    public int[] colorBackground = {255, 255, 255, 255};
    public int[] colorBackgroundSlider = {255, 0, 255, 255};

    private boolean isMouseClickedLeft;
    private boolean isMouseClickedMiddle;
    private boolean isMouseClickedRight;

    private Type type;

    private TurokFont fontRenderer;
    private TurokMouse mouse;

    public Flag flagMouse = Flag.MOUSE_NOT_OVER;

    public RocanSlider(String name, TurokFont fontRenderer, TurokMouse mouse) {
        super(name);

        this.fontRenderer = fontRenderer;
        this.mouse = mouse;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public void setMouse(TurokMouse mouse) {
        this.mouse = mouse;
    }

    public TurokMouse getMouse() {
        return mouse;
    }

    public void setFontRenderer(TurokFont fontRenderer) {
        this.fontRenderer = fontRenderer;
    }

    public TurokFont getFontRenderer() {
        return fontRenderer;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public float getValue() {
        return value;
    }

    public void setRendering(boolean rendering) {
        isRendering = rendering;
    }

    public boolean isRendering() {
        return isRendering;
    }

    public void setDisabled(boolean disabled) {
        isDisabled = disabled;
    }

    public boolean isDisabled() {
        return isDisabled;
    }

    public void setMouseClickedLeft(boolean mouseClickedLeft) {
        isMouseClickedLeft = mouseClickedLeft;
    }

    public boolean isMouseClickedLeft() {
        return isMouseClickedLeft;
    }

    public void setMouseClickedMiddle(boolean mouseClickedMiddle) {
        isMouseClickedMiddle = mouseClickedMiddle;
    }

    public boolean isMouseClickedMiddle() {
        return isMouseClickedMiddle;
    }

    public void setMouseClickedRight(boolean mouseClickedRight) {
        isMouseClickedRight = mouseClickedRight;
    }

    public void setPartialTicks(float partialTicks) {
        this.partialTicks = partialTicks;
    }

    public float getPartialTicks() {
        return partialTicks;
    }

    public void setOffsetX(float offsetX) {
        this.offsetX = offsetX;
    }

    public float getOffsetX() {
        return offsetX;
    }

    public void setOffsetY(float offsetY) {
        this.offsetY = offsetY;
    }

    public float getOffsetY() {
        return offsetY;
    }

    public void setMaximum(float maximum) {
        this.maximum = maximum;
    }

    public float getMaximum() {
        return maximum;
    }

    public void setMinimum(float minimum) {
        this.minimum = minimum;
    }

    public float getMinimum() {
        return minimum;
    }

    public void setOffsetW(float offsetW) {
        this.offsetW = offsetW;
    }

    public float getOffsetW() {
        return offsetW;
    }

    public void setOffsetH(float offsetH) {
        this.offsetH = offsetH;
    }

    public float getOffsetH() {
        return offsetH;
    }

    public void doMouseOver(TurokMouse mouse) {
        if (!this.isRendering) {
            return;
        }

        this.flagMouse = this.rect.collideWithMouse(mouse) ? Flag.MOUSE_OVER : Flag.MOUSE_NOT_OVER;
    }

    @Override
    public void onScreenClosed() {
        if (!this.isRendering) {
            return;
        }
    }

    @Override
    public void onCustomScreenClosed() {
        if (!this.isRendering) {
            return;
        }
    }

    @Override
    public void onScreenOpened() {
        if (!this.isRendering) {
            return;
        }
    }

    @Override
    public void onCustomScreenOpened() {
        if (!this.isRendering) {
            return;
        }
    }

    @Override
    public void onKeyboardPressed(char character, int key) {
        if (!this.isRendering) {
            return;
        }
    }

    @Override
    public void onCustomKeyboardPressed(char character, int key) {
        if (!this.isRendering) {
            return;
        }
    }

    @Override
    public void onMouseReleased(int button) {
        if (!this.isRendering) {
            return;
        }

        if (this.isMouseClickedLeft) {
            this.isMouseClickedLeft = false;
        }
    }

    @Override
    public void onCustomMouseReleased(int button) {
        if (!this.isRendering) {
            return;
        }
    }

    @Override
    public void onMouseClicked(int button) {
        if (!this.isRendering) {
            return;
        }

        if (this.flagMouse == Flag.MOUSE_OVER && !this.isDisabled()) {
            this.isMouseClickedLeft = button == 0;
        }
    }

    @Override
    public void onCustomMouseClicked(int button) {
        if (!this.isRendering) {
            return;
        }
    }

    @Override
    public void onRender() {
        if (!this.isRendering) {
            return;
        }

        TurokRenderGL.color(this.colorBackground[0], this.colorBackground[1], this.colorBackground[2], this.colorBackground[3]);
        TurokRenderGL.drawSolidRect(this.rect);

        double value = this.value;

        float maximum = this.maximum;
        float minimum = this.minimum;

        if (this.getType() == Type.UP) {
            TurokRenderGL.color(this.colorBackgroundSlider[0], this.colorBackgroundSlider[1], this.colorBackgroundSlider[2], this.colorBackgroundSlider[3]);
            TurokRenderGL.drawSolidRect(this.rect.getX(), this.rect.getY(), this.rect.getWidth(), this.offsetH);

            this.offsetW = 0;
            this.offsetH = (float) ((this.rect.getHeight()) * (value - minimum) / (maximum - minimum));
        } else {
            TurokRenderGL.color(this.colorBackgroundSlider[0], this.colorBackgroundSlider[1], this.colorBackgroundSlider[2], this.colorBackgroundSlider[3]);
            TurokRenderGL.drawSolidRect(this.rect.getX(), this.rect.getY(), this.offsetW, this.rect.getHeight());

            this.offsetW = (float) ((this.rect.getWidth()) * (value - minimum) / (maximum - minimum));
            this.offsetH = 0;
        }

        float mouse = Math.min(this.getType() == Type.UP ? this.rect.getHeight() : this.rect.getWidth(), Math.max(0, this.mouse.getX() - (this.getType() == Type.UP ? this.rect.getY() : this.rect.getX())));

        if (this.isMouseClickedLeft) {
            if (mouse == 0) {
                this.value = this.minimum;
            } else {
                this.value = (float) TurokMath.round(((mouse / (this.getType() == Type.UP ? this.rect.getHeight() : this.rect.getWidth())) * (maximum - minimum) + minimum));
            }
        }
    }

    @Override
    public void onCustomRender() {
    }
}
