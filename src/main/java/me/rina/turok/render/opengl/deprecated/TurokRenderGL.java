package me.rina.turok.render.opengl.deprecated;

import me.rina.turok.hardware.mouse.TurokMouse;
import me.rina.turok.util.TurokDisplay;
import me.rina.turok.util.TurokMath;
import me.rina.turok.util.TurokRect;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.*;

import java.awt.*;
import java.util.HashMap;

/**
 * @author SrRina
 * @since 26/09/20 at 1:33pm
 */
public class TurokRenderGL {
	private static final Minecraft mc = Minecraft.getMinecraft();

	private static TurokRenderGL INSTANCE;

	protected TurokDisplay display;
	protected TurokMouse mouse;

	protected int program;
	protected HashMap<String, Integer> uniforms;
	protected boolean isShaderInitializedWithoutErrors;

	public static final int TUROKGL_NULL = 0;
	public static final int TUROKGL_INIT = 1;
	public static final int TUROKGL_SHADER = 2;
	public static final int TUROKGL_UNIFORM_NULL = 0xFFFFFFFF;

	public static void init() {
		INSTANCE = new TurokRenderGL();
	}

	public static void init(Object object) {
		if (object instanceof TurokDisplay) {
			INSTANCE.display = (TurokDisplay) object;
		}

		if (object instanceof TurokMouse) {
			INSTANCE.mouse = (TurokMouse) object;
		}

		if (object instanceof Integer) {
			switch ((Integer) object) {
				case TUROKGL_NULL : {
					break;
				}

				case TUROKGL_INIT : {
					init();

					break;
				}

				case TUROKGL_SHADER: {
					INSTANCE.initializeShader();

					break;
				}
			}
		}
	}

	public void initializeShader() {
		this.program = GL20.glCreateProgram();
		this.uniforms = new HashMap<>();

		switch (INSTANCE.program) {
			case TUROKGL_NULL : {
				System.err.println("Turok: Shader creation failed, returned " + INSTANCE.program);

				isShaderInitializedWithoutErrors = false;

				break;
			}

			default : {
				isShaderInitializedWithoutErrors = true;

				break;
			}
		}
	}

	public static boolean isShaderProgramInitialized() {
		return INSTANCE.isShaderInitializedWithoutErrors;
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

		enable(GL11.GL_BLEND);
		blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		shaderMode(GL11.GL_SMOOTH);

		color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());

		lineSize(1.0f);

		prepare(GL11.GL_LINE_LOOP);
		{
			color(color.getRed(), color.getGreen(), color.getBlue(), valueAlpha - TurokMath.clamp(TurokMath.sqrt(vx * vx + vy * vy) / (radius / 100f), 0, valueAlpha));
			addVertex(x + offset, y);

			color(color.getRed(), color.getGreen(), color.getBlue(), valueAlpha - TurokMath.clamp(TurokMath.sqrt(vx * vx + vh * vh) / (radius / 100f), 0, valueAlpha));
			addVertex(x + offset,y + h + offset);

			color(color.getRed(), color.getGreen(), color.getBlue(), valueAlpha - TurokMath.clamp(TurokMath.sqrt(vw * vw + vh * vh) / (radius / 100f), 0, valueAlpha));
			addVertex(x + w, y + h);

			color(color.getRed(), color.getGreen(), color.getBlue(), valueAlpha - TurokMath.clamp(TurokMath.sqrt(vw * vw + vy * vy) / (radius / 100f), 0, valueAlpha));
			addVertex(x + w, y);
		}

		release();
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

		enable(GL11.GL_BLEND);
		blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		prepare(GL11.GL_QUADS);
		{
			color(color.getRed(), color.getGreen(), color.getBlue(), valueAlpha - TurokMath.clamp(TurokMath.sqrt(vx * vx + vy * vy) / (radius / 100f), 0, valueAlpha));
			addVertex(x, y);

			color(color.getRed(), color.getGreen(), color.getBlue(), valueAlpha - TurokMath.clamp(TurokMath.sqrt(vx * vx + vh * vh) / (radius / 100f), 0, valueAlpha));
			addVertex(x, y + h);

			color(color.getRed(), color.getGreen(), color.getBlue(), valueAlpha - TurokMath.clamp(TurokMath.sqrt(vw * vw + vh * vh) / (radius / 100f), 0, valueAlpha));
			addVertex(x + w, y + h);

			color(color.getRed(), color.getGreen(), color.getBlue(), valueAlpha - TurokMath.clamp(TurokMath.sqrt(vw * vw + vy * vy) / (radius / 100f), 0, valueAlpha));
			addVertex(x + w, y);
		}

		release();
	}

	public static void disableState(int target) {
		disable(target);
	}

	public static void enableState(int target) {
		enable(target);
	}

	public static void drawScissor(float x, float y, float w, float h, TurokDisplay display) {
		int calculatedX = (int) x;
		int calculatedY = (int) y;

		int calculatedW = (int) (calculatedX + w);
		int calculatedH = (int) (calculatedY + h);

		GL11.glScissor((int) (calculatedX * display.getScaleFactor()), (int) (display.getHeight() - (calculatedH * display.getScaleFactor())), (int) ((calculatedW - calculatedX) * display.getScaleFactor()), (int) ((calculatedH - calculatedY) * display.getScaleFactor()));
	}

	public static void drawScissor(int x, int y, int w, int h) {
		int calculatedX = x;
		int calculatedY = y;

		int calculatedW = calculatedX + w;
		int calculatedH = calculatedY + h;

		GL11.glScissor((int) (calculatedX * INSTANCE.display.getScaleFactor()), (int) (INSTANCE.display.getHeight() - (calculatedH * INSTANCE.display.getScaleFactor())), (int) ((calculatedW - calculatedX) * INSTANCE.display.getScaleFactor()), (int) ((calculatedH - calculatedY) * INSTANCE.display.getScaleFactor()));
	}

	public static void drawTexture(float x, float y, float width, float height) {
		prepare(GL11.GL_QUADS);
		{
			sewTexture(0, 0);
			addVertex(x, y);
			sewTexture(0, 1);
			addVertex(x, y + height);
			sewTexture(1, 1);
			addVertex(x + width, y + height);
			sewTexture(1, 0);
			addVertex(x + width, y);
		}

		release();
	}

	public static void drawTextureInterpolated(float x, float y, float xx, float yy, float width, float height, float ww, float hh) {
		prepare(GL11.GL_QUADS);
		{
			sewTexture(0 + xx, 0 + hh);
			addVertex(x, y);
			sewTexture(0 + xx, 1 + hh);
			addVertex(x, y + height);
			sewTexture(1 + ww, 1 + hh);
			addVertex(x + width, y + height);
			sewTexture(1 + ww, 0 + hh);
			addVertex(x + width, y);
		}

		release();
	}

	public static void drawUpTriangle(float x, float y, float width, float height, int offsetX) {
		enable(GL11.GL_BLEND);
		blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		prepare(GL11.GL_TRIANGLE_FAN);
		{
			addVertex(x + width, y + height);
			addVertex(x + width, y);
			addVertex(x - offsetX, y);
		}

		release();
	}

	public static void drawDownTriangle(float x, float y, float width, float height, int offsetX) {
		enable(GL11.GL_BLEND);
		blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		prepare(GL11.GL_TRIANGLE_FAN);
		{
			addVertex(x, y);
			addVertex(x, y + height);
			addVertex(x + width + offsetX, y + height);
		}

		release();
	}

	public static void drawArc(float cx, float cy, float r, float start_angle, float end_angle, float num_segments) {
		prepare(GL11.GL_TRIANGLES);

		for (int i = (int) (num_segments / (360 / start_angle)) + 1; i <= num_segments / (360 / end_angle); i++) {
			double previousAngle = 2 * Math.PI * (i - 1) / num_segments;
			double angle = 2 * Math.PI * i / num_segments;

			addVertex(cx, cy);
			addVertex(cx + Math.cos(angle) * r, (cy + Math.sin(angle) * r));
			addVertex(cx + Math.cos(previousAngle) * r, cy + Math.sin(previousAngle) * r);
		}

		release();
	}

	public static void drawArc(float x, float y, float radius) {
		drawArc(x, y, radius, 0, 360, 40);
	}

	public static void drawArcOutline(float cx, float cy, float r, float start_angle, float end_angle, float num_segments) {
		prepare(GL11.GL_LINE_LOOP);

		for (int i = (int) (num_segments / (360 / start_angle)) + 1; i <= num_segments / (360 / end_angle); i++) {
			double angle = 2 * Math.PI * i / num_segments;

			addVertex( cx + Math.cos(angle) * r, cy + Math.sin(angle) * r);
		}

		release();
	}

	public static void drawArcOutline(float x, float y, float radius) {
		drawArcOutline(x, y, radius, 0, 360, 40);
	}

	public static void drawOutlineRect(float x, float y, float width, float height) {
		float offset = 0.5f;

		enable(GL11.GL_BLEND);
		blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		prepare(GL11.GL_LINE_LOOP);
		{
			addVertex(x + offset, y);
			addVertex(x + offset,y + height + offset);
			addVertex(x + width, y + height);
			addVertex(x + width, y);
		}

		release();
	}

	public static void drawOutlineRect(int x, int y, int width, int height) {
		drawOutlineRect((float) x, (float) y, (float) width, (float) height);
	}

	public static void drawOutlineRect(TurokRect rect) {
		drawOutlineRect((float) rect.getX(), (float) rect.getY(), (float) (rect.getWidth()), (float) (rect.getHeight()));
	}

	public static void drawOutlineRoundedRect(float x, float y, float width, float height, float radius, float dR, float dG, float dB, float dA, float line_width) {
		drawRoundedRect(x, y, width, height, radius);

		color(dR, dG, dB, dA);

		drawRoundedRect(x + line_width, y + line_width, width - line_width * 2, height - line_width * 2, radius);
	}

	public static void drawRoundedRect(float x, float y, float width, float height, float radius) {
		enable(GL11.GL_BLEND);

		blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		drawArc((x + width - radius), (y + height - radius), radius, 0, 90, 16);
		drawArc((x + radius), (y + height - radius), radius, 90, 180, 16);
		drawArc(x + radius, y + radius, radius, 180, 270, 16);
		drawArc((x + width - radius), (y + radius), radius, 270, 360, 16);

		prepare(GL11.GL_TRIANGLES);
		{
			addVertex(x + width - radius, y);
			addVertex(x + radius, y);

			addVertex(x + width - radius, y + radius);
			addVertex(x + width - radius, y + radius);

			addVertex(x + radius, y);
			addVertex(x + radius, y + radius);

			addVertex(x + width, y + radius);
			addVertex(x, y + radius);

			addVertex(x, y + height - radius);
			addVertex(x + width, y + radius);

			addVertex(x, y + height-radius);
			addVertex(x + width, y + height - radius);

			addVertex(x + width - radius, y + height - radius);
			addVertex(x + radius, y + height - radius);

			addVertex(x + width - radius, y + height);
			addVertex(x + width - radius, y + height);

			addVertex(x + radius, y + height - radius);
			addVertex(x + radius, y + height);
		}

		release();
	}

	public static void drawRoundedRect(TurokRect rect, float size) {
		drawRoundedRect((float) rect.getX(), (float) rect.getY(), (float) (rect.getWidth()), (float) (rect.getHeight()), size);
	}

	public static void drawSolidRect(float x, float y, float width, float height) {
		enable(GL11.GL_BLEND);
		blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		prepare(GL11.GL_QUADS);
		{
			addVertex(x, y);
			addVertex(x, y + height);
			addVertex(x + width, y + height);
			addVertex(x + width, y);
		}

		release();
	}

	public static void drawSolidRect(int x, int y, int width, int height) {
		drawSolidRect((float) x, (float) y, (float) width, (float) height);
	}

	public static void drawSolidRect(TurokRect rect) {
		drawSolidRect((float) rect.getX(), (float) rect.getY(), (float) (rect.getWidth()), (float) (rect.getHeight()));
	}

	public static void drawLine(int x, int y, int x1, int xy, float line) {
		enableAlphaBlend();

		lineSize(line);

		prepare(GL11.GL_LINE_SMOOTH);
		{
			addVertex(x, y);
			addVertex(x1, xy);
		}

		release();
	}

	public static void drawLine3D(double x, double y, double z, double x1, double y1, double z1, int r, int g, int b, int a, float line) {
		GlStateManager.pushMatrix();
		GlStateManager.disableTexture2D();
		GlStateManager.enableBlend();
		GlStateManager.disableAlpha();
		GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
		GlStateManager.shadeModel(GL11.GL_SMOOTH);

		lineSize(line);
		enable(GL11.GL_LINE_SMOOTH);
		hint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);

		GlStateManager.disableDepth();

		enable(GL32.GL_DEPTH_CLAMP);

		final Tessellator tessellator = Tessellator.getInstance();
		final BufferBuilder bufferBuilder = tessellator.getBuffer();

		bufferBuilder.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
		bufferBuilder.pos(x, y, z).color(r, g, b, a).endVertex();
		bufferBuilder.pos(x1, y1, z1).color(r, g, b, a).endVertex();

		tessellator.draw();

		GlStateManager.shadeModel(GL11.GL_FLAT);

		disable(GL11.GL_LINE_SMOOTH);

		GlStateManager.enableDepth();

		disable(GL32.GL_DEPTH_CLAMP);

		GlStateManager.disableBlend();
		GlStateManager.enableAlpha();
		GlStateManager.enableTexture2D();
		GlStateManager.popMatrix();
	}

	public static void autoScale() {
		/*
		 * We need fix the screen size for works great all renders.
		 */
		pushMatrix();
		translate(INSTANCE.display.getScaledWidth(), INSTANCE.display.getScaledHeight());
		scale(0.5f, 0.5f, 0.5f);
		popMatrix();
	}

	public static void addVertexShader(String text) {
		try {
			addProgram(text, GL20.GL_VERTEX_SHADER);
		} catch (Exception exc) {
			exc.printStackTrace();
		}

	}

	public static void addGeometryShader(String text) {
		try {
			addProgram(text, GL32.GL_GEOMETRY_SHADER);
		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}

	public static void addFragmentShader(String text) {
		try {
			addProgram(text, GL20.GL_FRAGMENT_SHADER);
		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}

	public static void addTessellationControlShader(String text) {
		try {
			addProgram(text, GL40.GL_TESS_CONTROL_SHADER);
		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}

	public static void addTessellationEvaluationShader(String text) {
		try {
			addProgram(text, GL40.GL_TESS_EVALUATION_SHADER);
		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}

	public void addComputeShader(String text) {
		try {
			addProgram(text, GL43.GL_COMPUTE_SHADER);
		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}

	public static void compileShader() throws Exception {
		GL20.glLinkProgram(INSTANCE.program);

		if (GL20.glGetProgrami(INSTANCE.program, GL20.GL_LINK_STATUS) != TUROKGL_NULL) {
			GL20.glValidateProgram(INSTANCE.program);

			if (GL20.glGetProgrami(INSTANCE.program, GL20.GL_VALIDATE_STATUS) == TUROKGL_NULL) {
				throw new Exception("Turok: Failed to compile shader, " + INSTANCE.getClass().getName() + " " + GL20.glGetProgramInfoLog(INSTANCE.program, 1024));
			}
		} else {
			throw new Exception("Turok: Failed to compile shader, " + INSTANCE.getClass().getName() + " " + GL20.glGetProgramInfoLog(INSTANCE.program, 1024));
		}
	}

	public static void addUniform(String uniform) throws Exception {
		int uniformLocation = GL20.glGetUniformLocation(INSTANCE.program, uniform);

		if (uniformLocation != TUROKGL_UNIFORM_NULL) {
			INSTANCE.uniforms.put(uniform, uniformLocation);
		} else {
			throw new Exception("Turok: Failed to load uniform.");
		}
	}

	public static void addProgram(String program, int type) throws Exception {
		int shader = GL20.glCreateShader(type);

		if (shader != TUROKGL_NULL) {
			GL20.glShaderSource(shader, program);
			GL20.glCompileShader(shader);

			if (GL20.glGetShaderi(shader, GL20.GL_COMPILE_STATUS) == TUROKGL_NULL) {
				GL20.glAttachShader(INSTANCE.program, shader);
			} else {
				throw new Exception("Turok: " + INSTANCE.getClass().getName() + " " + GL20.glGetShaderInfoLog(shader, 1024));
			}
		} else {
			throw new Exception("Turok: Failed to load shader.");
		}
	}

	public static void bind() {
		GL20.glUseProgram(INSTANCE.program);
	}

	public static void color(float r, float g, float b, float a) {
		GL11.glColor4f((float) r / 255, (float) g / 255, (float) b / 255, (float) a / 255);
	}

	public static void color(double r, double g, double b, double a) {
		GL11.glColor4f((float) r / 255, (float) g / 255, (float) b / 255, (float) a / 255);
	}

	public static void color(int r, int g, int b, int a) {
		GL11.glColor4f((float) r / 255, (float) g / 255, (float) b / 255, (float) a / 255);
	}

	public static void color(float r, float g, float b) {
		GL11.glColor3f((float) r / 255, (float) g / 255, (float) b / 255);
	}

	public static void color(double r, double g, double b) {
		GL11.glColor3f((float) r / 255, (float) g / 255, (float) b / 255);
	}

	public static void color(int r, int g, int b) {
		GL11.glColor3f((float) r / 255, (float) g / 255, (float) b / 255);
	}

	public static void prepare(int mode) {
		GL11.glBegin(mode);
	}

	public static void release() {
		GL11.glEnd();
	}

	public static void sewTexture(float s, float t, float r) {
		GL11.glTexCoord3f(s, t, r);
	}

	public static void sewTexture(float s, float t) {
		GL11.glTexCoord2f(s, t);
	}

	public static void sewTexture(float s) {
		GL11.glTexCoord1f(s);
	}

	public static void sewTexture(double s, double t, double r) {
		sewTexture((float) s, (float) t, (float) r);
	}

	public static void sewTexture(double s, double t) {
		sewTexture((float) s, (float) t);
	}

	public static void sewTexture(double s) {
		sewTexture((float) s);
	}

	public static void sewTexture(int s, int t, int r) {
		sewTexture((float) s, (float) t, (float) r);
	}

	public static void sewTexture(int s, int t) {
		sewTexture((float) s, (float) t);
	}

	public static void sewTexture(int s) {
		sewTexture((float) s);
	}

	public static void addVertex(float x, float y, float z) {
		sewTexture(x, y, z);
	}

	public static void addVertex(float x, float y) {
		GL11.glVertex2f(x, y);
	}

	public static void addVertex(double x, double y, double z) {
		addVertex((float) x, (float) y, (float) z);
	}

	public static void addVertex(double x, double y) {
		addVertex((float) x, (float) y);
	}

	public static void addVertex(int x, int y, int z) {
		addVertex((float) x, (float) y, (float) z);
	}

	public static void addVertex(int x, int y) {
		addVertex((float) x, (float) y);
	}

	public static void hint(int target, int target1) {
		GL11.glHint(target, target1);
	}

	public static void translate(float x, float y, float z) {
		GL11.glTranslated(x, y, z);
	}

	public static void translate(double x, double y, double z) {
		GL11.glTranslated(x, y, z);
	}

	public static void translate(int x, int y, int z) {
		GL11.glTranslated(x, y, z);
	}

	public static void translate(float x, float y) {
		GL11.glTranslated(x, y, 0);
	}

	public static void translate(double x, double y) {
		GL11.glTranslated(x, y, 0);
	}

	public static void translate(int x, int y) {
		GL11.glTranslated(x, y, 0);
	}

	public static void scale(float scaledPosX, float scaledPosY, float scaledPosZ) {
		GL11.glScaled(scaledPosX, scaledPosY, scaledPosZ);
	}

	public static void scale(double scaledPosX, double scaledPosY, double scaledPosZ) {
		GL11.glScaled(scaledPosX, scaledPosY, scaledPosZ);
	}

	public static void scale(int scaledPosX, int scaledPosY, int scaledPosZ) {
		GL11.glScaled(scaledPosX, scaledPosY, scaledPosZ);
	}

	public static void lineSize(float width) {
		GL11.glLineWidth(width);
	}

	public static void pushMatrix() {
		GL11.glPushMatrix();
	}

	public static void popMatrix() {
		GL11.glPopMatrix();
	}

	public static void enable(int glState) {
		GL11.glEnable(glState);
	}

	public static void disable(int glState) {
		GL11.glDisable(glState);
	}

	public static void blendFunc(int glState, int glState1) {
		GL11.glBlendFunc(glState, glState1);
	}

	public static void polygonOffset(float factor, float units) {
		GL11.glPolygonOffset(factor, units);
	}

	public static void polygonOffset(double factor, double units) {
		GL11.glPolygonOffset((float) factor, (float) units);
	}

	public static void polygonOffset(int factor, int units) {
		GL11.glPolygonOffset(factor, units);
	}

	public static void polygonMode(int face, int mode) {
		GL11.glPolygonMode(face, mode);
	}

	public static void shaderMode(int mode) {
		GL11.glShadeModel(mode);
	}

	public static void enableAlphaBlend() {
		enable(GL11.GL_BLEND);
		blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	}

	public static void disableAlphaBlend() {
		disable(GL11.GL_BLEND);
	}

	public static void prepareOverlay() {
		pushMatrix();

		enable(GL11.GL_TEXTURE_2D);
		enable(GL11.GL_BLEND);

		GlStateManager.enableBlend();

		popMatrix();
	}

	public static void releaseOverlay() {
		GlStateManager.enableCull();
		GlStateManager.depthMask(true);
		GlStateManager.enableTexture2D();
		GlStateManager.enableBlend();
		GlStateManager.enableDepth();
	}

	public static void prepare3D(float size) {
		blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

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