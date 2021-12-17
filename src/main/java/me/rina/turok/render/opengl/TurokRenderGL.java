package me.rina.turok.render.opengl;

import me.rina.turok.util.TurokRect;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.*;

import java.awt.*;

/**
 * @author SrRina
 * @since 26/09/20 at 1:33pm
 */
public class TurokRenderGL {
	public static void color(Color color) {
		TurokGL.color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
	}

	public static void color(int r, int g, int b, int a) {
		TurokGL.color(r + 0f, g + 0f, b + 0f, a + 0f);
	}

	public static void drawTexture(float x, float y, float width, float height) {
		TurokGL.prepare(GL11.GL_QUADS);
		{
			TurokGL.sewTexture(0, 0);
			TurokGL.addVertex(x, y);
			TurokGL.sewTexture(0, 1);
			TurokGL.addVertex(x, y + height);
			TurokGL.sewTexture(1, 1);
			TurokGL.addVertex(x + width, y + height);
			TurokGL.sewTexture(1, 0);
			TurokGL.addVertex(x + width, y);
		}

		TurokGL.release();
	}

	public static void drawTextureInterpolated(float x, float y, float xx, float yy, float width, float height, float ww, float hh) {
		TurokGL.prepare(GL11.GL_QUADS);
		{
			TurokGL.sewTexture(0 + xx, 0 + hh);
			TurokGL.addVertex(x, y);
			TurokGL.sewTexture(0 + xx, 1 + hh);
			TurokGL.addVertex(x, y + height);
			TurokGL.sewTexture(1 + ww, 1 + hh);
			TurokGL.addVertex(x + width, y + height);
			TurokGL.sewTexture(1 + ww, 0 + hh);
			TurokGL.addVertex(x + width, y);
		}

		TurokGL.release();
	}

	public static void drawUpTriangle(float x, float y, float width, float height, int offsetX) {
		TurokGL.enable(GL11.GL_BLEND);
		TurokGL.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		TurokGL.prepare(GL11.GL_TRIANGLE_FAN);
		{
			TurokGL.addVertex(x + width, y + height);
			TurokGL.addVertex(x + width, y);
			TurokGL.addVertex(x - offsetX, y);
		}

		TurokGL.release();
	}

	public static void drawDownTriangle(float x, float y, float width, float height, int offsetX) {
		TurokGL.enable(GL11.GL_BLEND);
		TurokGL.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		TurokGL.prepare(GL11.GL_TRIANGLE_FAN);
		{
			TurokGL.addVertex(x, y);
			TurokGL.addVertex(x, y + height);
			TurokGL.addVertex(x + width + offsetX, y + height);
		}

		TurokGL.release();
	}

	public static void drawArc(float cx, float cy, float r, float start_angle, float end_angle, float num_segments) {
		TurokGL.enable(GL11.GL_BLEND);
		TurokGL.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		TurokGL.prepare(GL11.GL_TRIANGLES);

		for (int i = (int) (num_segments / (360 / start_angle)) + 1; i <= num_segments / (360 / end_angle); i++) {
			float previousAngle = (float) (2 * Math.PI * (i - 1) / num_segments);
			float angle = (float) (2 * Math.PI * i / num_segments);

			TurokGL.addVertex(cx, cy);
			TurokGL.addVertex((float) (cx + Math.cos(angle) * r), (float) (cy + Math.sin(angle) * r));
			TurokGL.addVertex((float) (cx + Math.cos(previousAngle) * r), (float) (cy + Math.sin(previousAngle) * r));
		}

		TurokGL.release();
	}

	public static void drawArc(float x, float y, float radius) {
		drawArc(x, y, radius, 0, 360, 40);
	}

	public static void drawArcOutline(float cx, float cy, float r, float start_angle, float end_angle, float num_segments) {
		TurokGL.enable(GL11.GL_BLEND);
		TurokGL.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		TurokGL.prepare(GL11.GL_LINE_LOOP);

		for (int i = (int) (num_segments / (360 / start_angle)) + 1; i <= num_segments / (360 / end_angle); i++) {
			float angle = (float) (2 * Math.PI * i / num_segments);

			TurokGL.addVertex((float) (cx + Math.cos(angle) * r), (float) (cy + Math.sin(angle) * r));
		}

		TurokGL.release();
	}

	public static void drawArcOutline(float x, float y, float radius) {
		drawArcOutline(x, y, radius, 0, 360, 40);
	}

	public static void drawOutlineRect(float x, float y, float width, float height, float l) {
		TurokGL.pushMatrix();
		TurokGL.enable(GL11.GL_BLEND);
		TurokGL.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		TurokGL.lineSize(l);

		TurokGL.prepare(GL11.GL_LINE_STRIP);
		{
			TurokGL.addVertex(x, y);
			TurokGL.addVertex(x,y + height);

			TurokGL.addVertex(x + width, y + height);
			TurokGL.addVertex(x + width, y);

			TurokGL.addVertex(x, y);
			TurokGL.addVertex(x, y);
		}

		TurokGL.release();
		TurokGL.popMatrix();
	}

	public static void drawOutlineRect(float x, float y, float width, float height) {
		TurokGL.pushMatrix();
		TurokGL.enable(GL11.GL_BLEND);
		TurokGL.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		TurokGL.lineSize(1.2f);

		TurokGL.prepare(GL11.GL_LINES);
		{
			TurokGL.addVertex(x, y);
			TurokGL.addVertex(x,y + height);

			TurokGL.addVertex(x,y + height);
			TurokGL.addVertex(x + width,y + height);

			TurokGL.addVertex(x + width, y + height);
			TurokGL.addVertex(x + width, y);

			TurokGL.addVertex(x + width, y);
			TurokGL.addVertex(x, y);
		}

		TurokGL.release();
		TurokGL.popMatrix();
	}

	public static void drawOutlineRect(TurokRect rect) {
		drawOutlineRect((float) rect.getX(), (float) rect.getY(), (float) (rect.getWidth()), (float) (rect.getHeight()));
	}

	public static void drawOutlineRoundedRect(float x, float y, float width, float height, float radius, float dR, float dG, float dB, float dA, float line_width) {
		drawRoundedRect(x, y, width, height, radius);

		TurokGL.color(dR, dG, dB, dA);

		drawRoundedRect(x + line_width, y + line_width, width - line_width * 2, height - line_width * 2, radius);
	}

	public static void drawRoundedRect(float x, float y, float width, float height, float radius) {
		TurokGL.pushMatrix();
		TurokGL.enable(GL11.GL_BLEND);
		TurokGL.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		drawArc((x + width - radius), (y + height - radius), radius, 0, 90, 16);
		drawArc((x + radius), (y + height - radius), radius, 90, 180, 16);
		drawArc(x + radius, y + radius, radius, 180, 270, 16);
		drawArc((x + width - radius), (y + radius), radius, 270, 360, 16);

		TurokGL.prepare(GL11.GL_TRIANGLES);
		{
			TurokGL.addVertex(x + width - radius, y);
			TurokGL.addVertex(x + radius, y);

			TurokGL.addVertex(x + width - radius, y + radius);
			TurokGL.addVertex(x + width - radius, y + radius);

			TurokGL.addVertex(x + radius, y);
			TurokGL.addVertex(x + radius, y + radius);

			TurokGL.addVertex(x + width, y + radius);
			TurokGL.addVertex(x, y + radius);

			TurokGL.addVertex(x, y + height - radius);
			TurokGL.addVertex(x + width, y + radius);

			TurokGL.addVertex(x, y + height-radius);
			TurokGL.addVertex(x + width, y + height - radius);

			TurokGL.addVertex(x + width - radius, y + height - radius);
			TurokGL.addVertex(x + radius, y + height - radius);

			TurokGL.addVertex(x + width - radius, y + height);
			TurokGL.addVertex(x + width - radius, y + height);

			TurokGL.addVertex(x + radius, y + height - radius);
			TurokGL.addVertex(x + radius, y + height);
		}

		TurokGL.release();
		TurokGL.popMatrix();
	}

	public static void drawRoundedRect(TurokRect rect, float size) {
		drawRoundedRect((float) rect.getX(), (float) rect.getY(), (float) (rect.getWidth()), (float) (rect.getHeight()), size);
	}

	public static void drawSolidRect(float x, float y, float width, float height) {
		TurokGL.pushMatrix();
		TurokGL.enable(GL11.GL_BLEND);
		TurokGL.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		TurokGL.prepare(GL11.GL_QUADS);
		{
			TurokGL.addVertex(x, y);
			TurokGL.addVertex(x,y + height);

			TurokGL.addVertex(x + width, y + height);
			TurokGL.addVertex(x + width, y);
		}

		TurokGL.release();
		TurokGL.popMatrix();
	}

	public static void drawSolidRect(int x, int y, int width, int height) {
		drawSolidRect((float) x, (float) y, (float) width, (float) height);
	}

	public static void drawSolidRect(TurokRect rect) {
		drawSolidRect((float) rect.getX(), (float) rect.getY(), (float) (rect.getWidth()), (float) (rect.getHeight()));
	}

	public static void drawLine(int x, int y, int x1, int xy, float line) {
		TurokGL.enable(GL11.GL_BLEND);
		TurokGL.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		TurokGL.lineSize(line);

		TurokGL.prepare(GL11.GL_LINE_SMOOTH);
		{
			TurokGL.addVertex(x, y);
			TurokGL.addVertex(x1, xy);
		}

		TurokGL.release();
	}

	public static void drawLine3D(double x, double y, double z, double x1, double y1, double z1, int r, int g, int b, int a, float line) {
		GlStateManager.pushMatrix();
		GlStateManager.disableTexture2D();
		GlStateManager.enableBlend();
		GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
		GlStateManager.shadeModel(GL11.GL_SMOOTH);
		GlStateManager.enableAlpha();

		TurokGL.lineSize(line);
		GlStateManager.disableDepth();

		TurokGL.enable(GL11.GL_LINE_SMOOTH);
		TurokGL.enable(GL32.GL_DEPTH_CLAMP);

		final Tessellator tessellator = Tessellator.getInstance();
		final BufferBuilder bufferBuilder = tessellator.getBuffer();

		bufferBuilder.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
		bufferBuilder.pos(x, y, z).color(r, g, b, a).endVertex();
		bufferBuilder.pos(x1, y1, z1).color(r, g, b, a).endVertex();

		tessellator.draw();

		TurokGL.disable(GL11.GL_LINE_SMOOTH);

		GlStateManager.enableDepth();

		TurokGL.disable(GL32.GL_DEPTH_CLAMP);

		GlStateManager.enableAlpha();
		GlStateManager.enableTexture2D();
		GlStateManager.popMatrix();
	}

	public static void prepareOverlay() {
		TurokGL.pushMatrix();

		TurokGL.enable(GL11.GL_TEXTURE_2D);
		TurokGL.enable(GL11.GL_BLEND);

		TurokGL.enable(GL11.GL_BLEND);
		TurokGL.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		TurokGL.popMatrix();
	}

	public static void releaseOverlay() {
		GlStateManager.enableCull();
		GlStateManager.depthMask(true);
		GlStateManager.enableTexture2D();
		GlStateManager.enableBlend();
		GlStateManager.enableDepth();
	}

	public static void prepare3D(float size) {
		TurokGL.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		GlStateManager.glLineWidth(size);
		GlStateManager.disableTexture2D();
		GlStateManager.depthMask(false);
		GlStateManager.enableBlend();
		GlStateManager.disableDepth();
		GlStateManager.disableLighting();
		GlStateManager.disableCull();
		GlStateManager.enableAlpha();
		GlStateManager.color(1, 1, 1);
	}

	public static void release3D() {
		GlStateManager.enableCull();
		GlStateManager.depthMask(true);
		GlStateManager.enableTexture2D();
		GlStateManager.enableBlend();
		GlStateManager.enableDepth();
	}
}