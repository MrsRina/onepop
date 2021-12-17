package rina.onepop.club.client.gui.imperador;

import me.rina.turok.render.font.TurokFont;
import me.rina.turok.render.font.management.TurokFontManager;
import me.rina.turok.render.opengl.TurokGL;
import me.rina.turok.render.opengl.TurokRenderGL;
import me.rina.turok.render.opengl.TurokShaderGL;
import me.rina.turok.util.TurokRect;
import org.lwjgl.opengl.GL11;
import rina.onepop.club.api.gui.widget.Widget;

import java.awt.*;

/**
 * @author SrRina
 * @since 13/08/2021 at 00:58
 **/
public class ImperadorLabel extends Widget {
    private String text;

    public int[] background = new int[] {0, 0, 0, 0};
    public int[] string = new int[] {255, 255, 255, 255};

    private boolean isShadow;
    private boolean isRendering;

    private float offsetX;
    private float offsetY;

    private float offsetW;
    private float offsetH;

    private TurokFont font;
    private final TurokRect scissor = new TurokRect(0, 0);

    public ImperadorLabel(TurokFont font, String text) {
        super("Imperador:Label");

        this.font = font;
        this.text = text;

        this.setShadow(true);
        this.setOffsetY(3f);
    }

    public TurokRect getScissor() {
        return scissor;
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

    public void setShadow(boolean shadow) {
        this.isShadow = shadow;
    }

    public void setRendering(boolean rendering) {
        isRendering = rendering;
    }

    public boolean isRendering() {
        return isRendering;
    }

    public boolean isShadow() {
        return isShadow;
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

    }

    @Override
    public void onCustomMouseReleased(int button) {

    }

    @Override
    public void onMouseClicked(int button) {

    }

    @Override
    public void onCustomMouseClicked(int button) {

    }

    @Override
    public void onRender() {
        if (this.isRendering()) {
            return;
        }

        final boolean flag = !GL11.glIsEnabled(GL11.GL_SCISSOR_TEST);

        TurokGL.color(TurokGL.arrayColorToColorClass(this.background));
        TurokRenderGL.drawSolidRect(this.rect);

        if (flag) {
            GL11.glEnable(GL11.GL_SCISSOR_TEST);
        }

        final Color color = TurokGL.arrayColorToColorClass(this.string);

        TurokShaderGL.drawScissor(this.scissor);
        TurokFontManager.render(this.font, this.text, this.rect.getX() + this.getOffsetX(), this.rect.getY() + this.getOffsetY(), this.isShadow(), color);

        if (flag) {
            GL11.glDisable(GL11.GL_SCISSOR_TEST);
        }
    }

    @Override
    public void onCustomRender() {

    }
}
