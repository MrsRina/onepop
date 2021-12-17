package me.rina.turok.render.opengl;

import me.rina.turok.hardware.mouse.TurokMouse;
import me.rina.turok.util.TurokDisplay;
import me.rina.turok.util.TurokMath;
import me.rina.turok.util.TurokRect;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

import java.awt.*;

/**
 * @author SrRina
 * @since 09/01/2021 at 16:43
 **/
public class TurokShaderGL {
    private static TurokShaderGL INSTANCE;

    private TurokDisplay display;
    private TurokMouse mouse;

    /*
     * The Minecraft tessellator.
     */
    public static final Tessellator TESSELLATOR = Tessellator.getInstance();

    public static void init(TurokDisplay display, TurokMouse mouse) {
        INSTANCE = new TurokShaderGL();

        // Start the classes.
        INSTANCE.display = display;
        INSTANCE.mouse = mouse;
    }

    public static BufferBuilder start() {
        return TESSELLATOR.getBuffer();
    }

    public static void end() {
        TESSELLATOR.draw();
    }

    public static void drawOutlineRectFadingMouse(TurokRect rect, int radius, Color color) {
        drawOutlineRectFadingMouse((float) rect.getX(), (float) rect.getY(), (float) rect.getWidth(), (float) rect.getHeight(), radius, color);
    }

    public static void drawOutlineRectFadingMouse(float x, float y, float w, float h, int radius, Color color) {
        float offset = 0.5f;

        float vx = x - INSTANCE.mouse.getX();
        float vy = y - INSTANCE.mouse.getY();

        float vw = (x + w) - INSTANCE.mouse.getX();
        float vh = (y + h) - INSTANCE.mouse.getY();

        int valueAlpha = color.getAlpha();

        TurokGL.enable(GL11.GL_BLEND);
        TurokGL.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        TurokGL.shaderMode(GL11.GL_SMOOTH);
        TurokGL.color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
        TurokGL.lineSize(1.0f);

        TurokGL.prepare(GL11.GL_LINE_LOOP);
        {
            TurokGL.color(color.getRed(), color.getGreen(), color.getBlue(), valueAlpha - TurokMath.clamp(TurokMath.sqrt(vx * vx + vy * vy) / (radius / 100f), 0, valueAlpha));
            TurokGL.addVertex(x + offset, y);

            TurokGL.color(color.getRed(), color.getGreen(), color.getBlue(), valueAlpha - TurokMath.clamp(TurokMath.sqrt(vx * vx + vh * vh) / (radius / 100f), 0, valueAlpha));
            TurokGL.addVertex(x + offset,y + h + offset);

            TurokGL.color(color.getRed(), color.getGreen(), color.getBlue(), valueAlpha - TurokMath.clamp(TurokMath.sqrt(vw * vw + vh * vh) / (radius / 100f), 0, valueAlpha));
            TurokGL.addVertex(x + w, y + h);

            TurokGL.color(color.getRed(), color.getGreen(), color.getBlue(), valueAlpha - TurokMath.clamp(TurokMath.sqrt(vw * vw + vy * vy) / (radius / 100f), 0, valueAlpha));
            TurokGL.addVertex(x + w, y);
        }

        TurokGL.release();
    }

    public static void drawSolidRectFadingMouse(TurokRect rect, int radius, Color color) {
        drawSolidRectFadingMouse((float) rect.getX(), (float) rect.getY(), (float) rect.getWidth(), (float) rect.getHeight(), radius, color);
    }

    public static void drawSolidRectFadingMouse(float x, float y, float w, float h, int radius, Color color) {
        float vx = x - INSTANCE.mouse.getX();
        float vy = y - INSTANCE.mouse.getY();

        float vw = (x + w) - INSTANCE.mouse.getX();
        float vh = (y + h) - INSTANCE.mouse.getY();

        int valueAlpha = color.getAlpha();

        TurokGL.pushMatrix();
        TurokGL.enable(GL11.GL_BLEND);
        TurokGL.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        TurokGL.shaderMode(GL11.GL_SMOOTH);

        TurokGL.prepare(GL11.GL_QUADS);
        {
            TurokGL.color(color.getRed(), color.getGreen(), color.getBlue(), valueAlpha - TurokMath.clamp(TurokMath.sqrt(vx * vx + vy * vy) / (radius / 100f), 0, valueAlpha));
            TurokGL.addVertex(x, y);

            TurokGL.color(color.getRed(), color.getGreen(), color.getBlue(), valueAlpha - TurokMath.clamp(TurokMath.sqrt(vx * vx + vh * vh) / (radius / 100f), 0, valueAlpha));
            TurokGL.addVertex(x, y + h);

            TurokGL.color(color.getRed(), color.getGreen(), color.getBlue(), valueAlpha - TurokMath.clamp(TurokMath.sqrt(vw * vw + vh * vh) / (radius / 100f), 0, valueAlpha));
            TurokGL.addVertex(x + w, y + h);

            TurokGL.color(color.getRed(), color.getGreen(), color.getBlue(), valueAlpha - TurokMath.clamp(TurokMath.sqrt(vw * vw + vy * vy) / (radius / 100f), 0, valueAlpha));
            TurokGL.addVertex(x + w, y);
        }

        TurokGL.release();
        TurokGL.popMatrix();
    }

    public static void drawLine(float x, float y, float x1, float y1, float w, int[] c) {
        Color color = new Color(TurokMath.clamp(c[0], 0, 255), TurokMath.clamp(c[1], 0, 255), TurokMath.clamp(c[2], 0, 255), TurokMath.clamp(c[3], 0, 255));

        float r = (float) (color.getRGB() >> 16 & 255) / 255.0f;
        float g = (float) (color.getRGB() >> 8 & 255) / 255.0f;
        float b = (float) (color.getRGB() & 255) / 255.0f;
        float a = (float) (color.getRGB() >> 24 & 255) / 255.0f;

        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

        GlStateManager.glLineWidth(w);

        BufferBuilder bufferBuilder = start();

        bufferBuilder.begin(GL11.GL_LINE, DefaultVertexFormats.POSITION_COLOR);
        bufferBuilder.pos(x, y, 0).color(r, g, b, a).endVertex();
        bufferBuilder.pos(x1, y1, 0).color(r, g, b, a).endVertex();

        end();

        GlStateManager.disableBlend();
    }

    public static void drawSolidRect(TurokRect rect, int[] color) {
        drawSolidRect(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight(), color);
    }

    public static void drawSolidRect(float x, float y, float w, float h, int[] c) {
        Color color = new Color(TurokMath.clamp(c[0], 0, 255), TurokMath.clamp(c[1], 0, 255), TurokMath.clamp(c[2], 0, 255), TurokMath.clamp(c[3], 0, 255));

        float r = (float) (color.getRGB() >> 16 & 255) / 255.0f;
        float g = (float) (color.getRGB() >> 8 & 255) / 255.0f;
        float b = (float) (color.getRGB() & 255) / 255.0f;
        float a = (float) (color.getRGB() >> 24 & 255) / 255.0f;

        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.shadeModel(GL11.GL_SMOOTH);

        BufferBuilder bufferBuilder = start();

        bufferBuilder.begin(GL11.GL_POLYGON, DefaultVertexFormats.POSITION_COLOR);
        bufferBuilder.pos(x, y, 0).color(r, g, b, a).endVertex();
        bufferBuilder.pos(x, y + h, 0).color(r, g, b, a).endVertex();
        bufferBuilder.pos(x + w, y + h, 0).color(r, g, b, a).endVertex();
        bufferBuilder.pos(x + w,  y, 0).color(r, g, b, a).endVertex();
        bufferBuilder.pos(x, y, 0).color(r, g, b, a).endVertex();
        bufferBuilder.pos(x, y, 0).color(r, g, b, a).endVertex();

        end();

        GlStateManager.popMatrix();
    }

    public static void drawOutlineRect(TurokRect rect, int[] color) {
        drawOutlineRect(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight(), color);
    }

    public static void drawOutlineRect(float x, float y, float w, float h, int[] c) {
        Color color = new Color(TurokMath.clamp(c[0], 0, 255), TurokMath.clamp(c[1], 0, 255), TurokMath.clamp(c[2], 0, 255), TurokMath.clamp(c[3], 0, 255));

        float r = (float) (color.getRGB() >> 16 & 255) / 255.0f;
        float g = (float) (color.getRGB() >> 8 & 255) / 255.0f;
        float b = (float) (color.getRGB() & 255) / 255.0f;
        float a = (float) (color.getRGB() >> 24 & 255) / 255.0f;

        GlStateManager.pushMatrix();
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.glLineWidth(0.5f);
        GlStateManager.shadeModel(GL11.GL_SMOOTH);

        BufferBuilder bufferBuilder = start();

        bufferBuilder.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
        bufferBuilder.pos(x, y, 0).color(r, g, b, a).endVertex();
        bufferBuilder.pos(x, y + h, 0).color(r, g, b, a).endVertex();
        bufferBuilder.pos(x + w, y + h, 0).color(r, g, b, a).endVertex();
        bufferBuilder.pos(x + w,  y, 0).color(r, g, b, a).endVertex();
        bufferBuilder.pos(x, y, 0).color(r, g, b, a).endVertex();
        bufferBuilder.pos(x, y, 0).color(r, g, b, a).endVertex();

        end();

        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    public static void pushScissor() {
        TurokGL.enable(GL11.GL_SCISSOR_TEST);
    }

    public static void pushScissorMatrix() {
        TurokGL.pushMatrix();
        TurokGL.enable(GL11.GL_SCISSOR_TEST);
    }

    public static void pushScissorAttrib() {
        TurokGL.pushAttrib(GL11.GL_SCISSOR_BIT);
        TurokGL.enable(GL11.GL_SCISSOR_TEST);
    }

    public static void drawScissor(TurokRect rect) {
        drawScissor(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());
    }

    public static void drawScissor(float x, float y, float w, float h) {
        int calculatedW = (int) (x + w);
        int calculatedH = (int) (y + h);

        TurokGL.scissor((int) (x * INSTANCE.display.getScaleFactor()), (int) (INSTANCE.display.getHeight() - (calculatedH * INSTANCE.display.getScaleFactor())), (int) ((calculatedW - x) * INSTANCE.display.getScaleFactor()), (int) ((calculatedH - y) * INSTANCE.display.getScaleFactor()));
    }

    public static void popScissor() {
        TurokGL.disable(GL11.GL_SCISSOR_TEST);
    }

    public static void popScissorMatrix() {
        TurokGL.disable(GL11.GL_SCISSOR_TEST);
        TurokGL.popMatrix();
    }

    public static void popScissorAttrib() {
        TurokGL.disable(GL11.GL_SCISSOR_TEST);
        TurokGL.popAttrib();
    }
}

