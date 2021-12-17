package rina.onepop.club.client.gui.imperador;

import me.rina.turok.hardware.mouse.TurokMouse;
import me.rina.turok.render.font.TurokFont;
import me.rina.turok.render.font.management.TurokFontManager;
import me.rina.turok.render.opengl.TurokGL;
import me.rina.turok.render.opengl.TurokRenderGL;
import me.rina.turok.render.opengl.TurokShaderGL;
import me.rina.turok.util.TurokMath;
import me.rina.turok.util.TurokRect;
import org.lwjgl.opengl.GL11;
import rina.onepop.club.api.gui.widget.Widget;

import java.awt.*;

/**
 * @author SrRina
 * @since 13/08/2021 at 20:38
 **/
public class ImperadorButton extends Widget {
    private String text;

    public int[] string = new int[] {255, 255, 255, 255};
    public int[] pressed = new int[] {255, 255, 255, 150};
    public int[] highlight = new int[] {255, 255, 255, 200};
    public int[] outline = new int[] {255, 255, 255, 200};
    public int[] background = new int[] {0, 0, 0, 0};

    public float pressedAlpha;
    public float lastTickPressedAlpha;

    public float highlightAlpha;
    public float lastTickHighlightAlpha;

    public float outlineAlpha;
    public float lastTickOutlineAlpha;

    private float partialTicks = 1f;

    private boolean isShadow;
    private boolean isRendering;
    private boolean isToDrawOutline;

    private boolean isPressed;
    private boolean isReleased;
    private boolean isMouseOver;

    private float offsetX;
    private float offsetY;

    private float offsetW;
    private float offsetH;

    private TurokFont font;
    private final TurokRect scissor = new TurokRect(0, 0);

    public ImperadorButton(TurokFont font, String text) {
        super("Imperador:Button");

        this.font = font;
        this.text = text;

        this.setIsShadow(true);
    }

    public TurokRect getScissor() {
        return scissor;
    }

    public void setPartialTicks(float partialTicks) {
        this.partialTicks = partialTicks;
    }

    public float getPartialTicks() {
        return partialTicks;
    }

    public void setFont(TurokFont font) {
        this.font = font;
    }

    public TurokFont getFont() {
        return font;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setMouseOver(boolean isMouseOver) {
        this.isMouseOver = isMouseOver;
    }

    public boolean isMouseOver() {
        return isMouseOver;
    }

    public void setIsShadow(boolean isShadow) {
        this.isShadow = isShadow;
    }

    public boolean getIsShadow() {
        return isShadow;
    }

    public void setRendering(boolean rendering) {
        isRendering = rendering;
    }

    public boolean isRendering() {
        return isRendering;
    }

    public void setPressed(boolean pressed) {
        this.isPressed = pressed;
    }

    public boolean isPressed() {
        return isPressed;
    }

    public void setReleased(boolean released) {
        isReleased = released;
    }

    public boolean isReleased() {
        return isReleased;
    }

    public void setToDrawOutline(boolean toDrawOutline) {
        isToDrawOutline = toDrawOutline;
    }

    public boolean isToDrawOutline() {
        return isToDrawOutline;
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

    public void scissor() {
        this.scissor.copy(this.rect);
    }

    public void center() {
        this.offsetX = (this.rect.getWidth() / 2f) - (TurokFontManager.getStringWidth(this.font, this.text) / 2f);
    }

    public void left(float difference) {
        this.offsetX = difference;
    }

    public void right(float difference) {
        this.offsetX = this.rect.getWidth() - TurokFontManager.getStringWidth(this.font, this.text) - difference;
    }

    public void doMouseOver(TurokMouse mouse) {
        this.isMouseOver = this.rect.collideWithMouse(mouse) && this.isRendering();
    }

    @Override
    public void onScreenClosed() {

    }

    @Override
    public void onCustomScreenClosed() {

    }

    @Override
    public void onScreenOpened() {

    }

    @Override
    public void onCustomScreenOpened() {

    }

    @Override
    public void onKeyboardPressed(char character, int key) {

    }

    @Override
    public void onCustomKeyboardPressed(char character, int key) {

    }

    @Override
    public void onMouseReleased(int button) {
        if (!this.isRendering()) {
            return;
        }

        if (this.isPressed()) {
            this.setReleased(this.isMouseOver());
            this.setPressed(false);
        }
    }

    @Override
    public void onCustomMouseReleased(int button) {

    }

    @Override
    public void onMouseClicked(int button) {
        if (!this.isRendering()) {
            return;
        }

        if (this.isMouseOver() && (button == 0 || button == 1)) {
            this.setPressed(true);
        }
    }

    @Override
    public void onCustomMouseClicked(int button) {

    }

    @Override
    public void onRender() {
        if (!this.isRendering()) {
            return;
        }

        this.highlightAlpha = this.isMouseOver() ? this.highlight[3] : 0f;
        this.pressedAlpha = this.isPressed() ? this.pressed[3] : 0f;
        this.outlineAlpha = this.outline[3];

        this.lastTickHighlightAlpha = TurokMath.lerp(this.lastTickHighlightAlpha, this.highlightAlpha, this.getPartialTicks());
        this.lastTickPressedAlpha = TurokMath.lerp(this.lastTickPressedAlpha, this.pressedAlpha, this.getPartialTicks());
        this.lastTickOutlineAlpha = TurokMath.lerp(this.lastTickOutlineAlpha, this.outlineAlpha, this.getPartialTicks());

        final boolean flag = !GL11.glIsEnabled(GL11.GL_SCISSOR_TEST);

        TurokGL.color(TurokGL.arrayColorToColorClass(this.background));
        TurokRenderGL.drawSolidRect(this.rect);

        if (this.lastTickOutlineAlpha != 0f && this.isToDrawOutline()) {
            TurokGL.color(this.outline[0], this.outline[1], this.outline[2], this.lastTickOutlineAlpha);
            TurokRenderGL.drawOutlineRect(this.rect);
        }

        if (this.lastTickHighlightAlpha != 0f) {
            TurokGL.color(this.highlight[0], this.highlight[1], this.highlight[2], this.lastTickHighlightAlpha);
            TurokRenderGL.drawSolidRect(this.rect);
        }

        if (this.lastTickPressedAlpha != 0f) {
            TurokGL.color(this.pressed[0], this.pressed[1], this.pressed[2], this.lastTickPressedAlpha);
            TurokRenderGL.drawSolidRect(this.rect);
        }

        if (flag) {
            GL11.glEnable(GL11.GL_SCISSOR_TEST);
        }

        final Color color = TurokGL.arrayColorToColorClass(this.string);

        TurokShaderGL.drawScissor(this.scissor);
        TurokFontManager.render(this.font, this.text, this.rect.getX() + this.getOffsetX(), this.rect.getY() + this.getOffsetY(), this.getIsShadow(), color);

        if (flag) {
            GL11.glDisable(GL11.GL_SCISSOR_TEST);
        }
    }

    @Override
    public void onCustomRender() {

    }
}