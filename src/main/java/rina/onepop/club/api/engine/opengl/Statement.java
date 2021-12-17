package rina.onepop.club.api.engine.opengl;

import org.lwjgl.opengl.GL11;

import java.awt.*;

/**
 * @author SrRina
 * @since 15/07/2021 at 00:00
 **/
public class Statement {
    public static void set(int state) {
        GL11.glEnable(state);
    }

    public static void unset(int state) {
        GL11.glDisable(state);
    }

    public static void matrix() {
        GL11.glPushMatrix();
    }

    public static void refresh() {
        GL11.glPopMatrix();
    }

    public static void blend() {
        set(GL11.GL_BLEND);

        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    }

    public static void line(int hint1, int hint2, float width) {
        GL11.glDisable(GL11.GL_LINE_WIDTH_RANGE);

        GL11.glHint(hint1, hint2);
        GL11.glLineWidth(width);
    }

    public static void setShaderModel(int mode) {
        GL11.glShadeModel(mode);
    }

    public static void setDepthMask() {
        GL11.glDepthMask(true);
    }

    public static void unsetDepthMask() {
        GL11.glDepthMask(false);
    }

    public static void color(Color color) {
        GL11.glColor4f(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);
    }

    public static void color(int r, int g, int b, int a) {
        GL11.glColor4f(r / 255f, g / 255f, b / 255f, a / 255f);
    }

    public static void rotate(float amount, float x, float y, float z) {
        GL11.glRotatef(amount, x, y, z);
    }

    public static void translate(double x, double y, double z) {
        GL11.glTranslated(x, y, z);
    }

    public static void scale(float s1, float s2, float s3) {
        GL11.glScaled(s1, s2, s3);
    }

    public static void setFrontCullFace() {
        GL11.glCullFace(GL11.GL_FRONT);
    }

    public static void setBackCullFace() {
        GL11.glCullFace(GL11.GL_BACK);
    }
}