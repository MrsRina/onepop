package rina.onepop.club.client.gui.rocan;

import me.rina.turok.hardware.mouse.TurokMouse;
import me.rina.turok.render.font.TurokFont;
import me.rina.turok.render.font.management.TurokFontManager;
import me.rina.turok.render.opengl.TurokGL;
import me.rina.turok.render.opengl.TurokRenderGL;
import me.rina.turok.util.TurokMath;
import rina.onepop.club.api.gui.flag.Flag;
import rina.onepop.club.api.gui.widget.Widget;

import java.awt.*;

/**
 * @author SrRina
 * @since 12/06/2021 at 01:33
 **/
public class RocanButton extends Widget {
    private boolean isRendering = true;
    private boolean isDisabled;
    private boolean value;
    private boolean shadow = true;
    private boolean toggled;

    private String text;

    private float partialTicks;
    private float alphaEffectHighlight;
    private float alphaEffectHighlightOutline;

    /* The offset space. */
    private float offsetX;
    private float offsetY;

    private float size = 3;

    public int[] colorBackground = {255, 255, 255, 255};
    public int[] colorBackgroundOutline = {255, 255, 255, 100};
    public int[] colorHighlight = {255, 255, 255, 255};
    public int[] colorHighlightOutline = {255, 255, 255, 255};
    public int[] colorString = {0, 0, 0, 255};
    public int[] colorDisabledString = {255, 255, 255, 100};

    private boolean isMouseClickedLeft;
    private boolean isMouseClickedMiddle;
    private boolean isMouseClickedRight;

    private TurokFont fontRenderer;
    private TurokMouse mouse;

    public Flag flagMouse = Flag.MOUSE_NOT_OVER;

    public RocanButton(String name, TurokFont fontRenderer, TurokMouse mouse) {
        super(name);

        this.fontRenderer = fontRenderer;
        this.mouse = mouse;

        this.text = this.rect.getTag();
    }

    public void setSize(float size) {
        this.size = size;
    }

    public float getSize() {
        return size;
    }

    public void setMouse(TurokMouse mouse) {
        this.mouse = mouse;
    }

    public void setShadow(boolean shadow) {
        this.shadow = shadow;
    }

    public boolean isShadow() {
        return shadow;
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

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setValue(boolean value) {
        this.value = value;
    }

    public boolean getValue() {
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

    public boolean isMouseClickedLeft() {
        return isMouseClickedLeft;
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

    public void setToggled(boolean toggled) {
        this.toggled = toggled;
    }

    public boolean isToggled() {
        return toggled;
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
            if (this.flagMouse == Flag.MOUSE_OVER) {
                this.setValue(!this.getValue());
                this.setToggled(true);
            }

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

        this.text = this.rect.getTag();

        this.rect.setHeight(size + TurokFontManager.getStringHeight(this.fontRenderer, this.text) + size);

        this.alphaEffectHighlight = TurokMath.lerp(this.alphaEffectHighlight, this.flagMouse == Flag.MOUSE_OVER ? this.colorHighlight[3] : 0, this.partialTicks);
        this.alphaEffectHighlightOutline = TurokMath.lerp(this.alphaEffectHighlightOutline, this.flagMouse == Flag.MOUSE_OVER ? this.colorHighlightOutline[3] : 0, this.partialTicks);

        TurokRenderGL.color(this.colorHighlight[0], this.colorHighlight[1], this.colorHighlight[2], (int) this.alphaEffectHighlight);
        TurokRenderGL.drawSolidRect(this.rect);

        TurokRenderGL.color(this.colorBackground[0], this.colorBackground[1], this.colorBackground[2], this.colorBackground[3]);
        TurokRenderGL.drawSolidRect(this.rect);

        TurokRenderGL.color(this.colorHighlightOutline[0], this.colorHighlightOutline[1], this.colorHighlightOutline[2], (int) this.alphaEffectHighlightOutline);
        TurokRenderGL.drawOutlineRect(this.rect);

        TurokRenderGL.color(this.colorBackgroundOutline[0], this.colorBackgroundOutline[1], this.colorBackgroundOutline[2], this.colorBackgroundOutline[3]);
        TurokRenderGL.drawOutlineRect(this.rect);

        TurokGL.color(this.isDisabled ? this.colorDisabledString[0] : this.colorString[0], this.isDisabled ? this.colorDisabledString[1] : this.colorString[1], this.isDisabled ? this.colorDisabledString[2] : this.colorString[2], this.isDisabled ? this.colorDisabledString[3] : this.colorString[3]);
        TurokFontManager.render(this.fontRenderer, this.text, this.rect.getX() + this.offsetX, this.rect.getY() + this.size + this.offsetY, this.shadow, new Color(255, 255, 255));
    }

    @Override
    public void onCustomRender() {
    }
}
