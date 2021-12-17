package me.rina.turok.render.font.management;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.rina.turok.render.font.TurokFont;
import me.rina.turok.render.opengl.TurokGL;
import me.rina.turok.util.TurokMath;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import org.apache.commons.lang3.CharUtils;
import org.lwjgl.opengl.GL11;

import java.awt.*;

/**
 * @author SrRina
 * @since 26/09/20 at 1:23pm
 */
public class TurokFontManager {
	public static void render(TurokFont fontRenderer, String string, float x, float y, boolean shadow, int factor) {
		float[] currentColor360 = {
				(System.currentTimeMillis() % (360 * 32)) / (360f * 32)
		};

		int cycleColor = Color.HSBtoRGB(currentColor360[0], 1, 1);

		Color currentColor = new Color(((cycleColor >> 16) & 0xFF), ((cycleColor >> 8) & 0xFF), ((cycleColor) & 0xFF));

		float hueIncrement = 1.0f / factor;
		float currentHue = Color.RGBtoHSB(currentColor.getRed(), currentColor.getGreen(), currentColor.getBlue(), null)[0];
		float saturation = Color.RGBtoHSB(currentColor.getRed(), currentColor.getGreen(), currentColor.getBlue(), null)[1];
		float brightness = Color.RGBtoHSB(currentColor.getRed(), currentColor.getGreen(), currentColor.getBlue(), null)[2];

		float currentWidth = 0;

		boolean shouldRainbow = true;
		boolean shouldContinue = false;

		ChatFormatting colorCache = ChatFormatting.GRAY;

		for (int i = 0; i < string.length(); ++i) {
			char currentChar = string.charAt(i);
			char nextChar = string.charAt(TurokMath.clamp(i + 1, 0, string.length() - 1));

			String nextFormatting = (String.valueOf(currentChar) + nextChar);

			if (nextFormatting.equals("\u00a7r") && !shouldRainbow) {
				shouldRainbow = true;
			} else {
				if (String.valueOf(currentChar).equals("\u00a7")) {
					shouldRainbow = false;
				}
			}
			if (shouldContinue) {
				shouldContinue = false;

				continue;
			}

			if (String.valueOf(currentChar).equals("\u00a7")) {
				shouldContinue = true;

				colorCache = ChatFormatting.getByChar(CharUtils.toChar(nextFormatting.replaceAll("\u00a7", "")));

				continue;
			}

			render(fontRenderer, (!shouldRainbow ? colorCache : "") + String.valueOf(currentChar),x + currentWidth, y, shadow, currentColor);

			currentWidth += getStringWidth(fontRenderer, String.valueOf(currentChar));

			if (String.valueOf(currentChar).equals(" ")) {
				continue;
			}

			currentColor = new Color(Color.HSBtoRGB(currentHue, saturation, brightness));
			currentHue += hueIncrement;
		}
	}

	public static void render(String string, float x, float y, boolean shadow, int factor) {
		float[] currentColor360 = {
				(System.currentTimeMillis() % (360 * 32)) / (360f * 32)
		};

		int cycleColor = Color.HSBtoRGB(currentColor360[0], 1, 1);

		Color currentColor = new Color(((cycleColor >> 16) & 0xFF), ((cycleColor >> 8) & 0xFF), ((cycleColor) & 0xFF));

		float hueIncrement = 1.0f / factor;
		float currentHue = Color.RGBtoHSB(currentColor.getRed(), currentColor.getGreen(), currentColor.getBlue(), null)[0];
		float saturation = Color.RGBtoHSB(currentColor.getRed(), currentColor.getGreen(), currentColor.getBlue(), null)[1];
		float brightness = Color.RGBtoHSB(currentColor.getRed(), currentColor.getGreen(), currentColor.getBlue(), null)[2];

		float currentWidth = 0;

		boolean shouldRainbow = true;
		boolean shouldContinue = false;

		ChatFormatting colorCache = ChatFormatting.GRAY;

		for (int i = 0; i < string.length(); ++i) {
			char currentChar = string.charAt(i);
			char nextChar = string.charAt(TurokMath.clamp(i + 1, 0, string.length() - 1));

			String nextFormatting = (String.valueOf(currentChar) + nextChar);

			if (nextFormatting.equals("\u00a7r") && !shouldRainbow) {
				shouldRainbow = true;
			} else {
				if (String.valueOf(currentChar).equals("\u00a7")) {
					shouldRainbow = false;
				}
			}
			if (shouldContinue) {
				shouldContinue = false;
				continue;
			}

			if (String.valueOf(currentChar).equals("\u00a7")) {
				shouldContinue = true;

				colorCache = ChatFormatting.getByChar(CharUtils.toChar(nextFormatting.replaceAll("\u00a7", "")));

				continue;
			}

			render((!shouldRainbow ? colorCache : "") + String.valueOf(currentChar),x + currentWidth, y, shadow, currentColor);

			currentWidth += getStringWidth(String.valueOf(currentChar));

			if (String.valueOf(currentChar).equals(" ")) {
				continue;
			}

			currentColor = new Color(Color.HSBtoRGB(currentHue, saturation, brightness));
			currentHue += hueIncrement;
		}
	}

	public static void render(String string, float x, float y, boolean shadow, Color color) {
		TurokGL.enable(GL11.GL_TEXTURE_2D);
		GlStateManager.enableBlend();

		if (shadow) {
			Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(string, (int) x, (int) y, color.getRGB());
		} else {
			Minecraft.getMinecraft().fontRenderer.drawString(string, (int) x, (int) y, color.getRGB());
		}

		TurokGL.disable(GL11.GL_TEXTURE_2D);
	}

	public static void renderNative(TurokFont fontRenderer, String string, float x, float y, boolean shadow, Color color) {
		if (shadow) {
			if (fontRenderer.isRenderingCustomFont()) {
				fontRenderer.drawStringWithShadow(string, x, y, color.getRGB());
			} else {
				Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(string, (int) x, (int) y, color.getRGB());
			}
		} else {
			if (fontRenderer.isRenderingCustomFont()) {
				fontRenderer.drawString(string, x, y, color.getRGB());
			} else {
				Minecraft.getMinecraft().fontRenderer.drawString(string, (int) x, (int) y, color.getRGB());
			}
		}
	}

	public static void render(TurokFont fontRenderer, String string, float x, float y, boolean shadow, Color color) {
		TurokGL.enable(GL11.GL_TEXTURE_2D);
		GlStateManager.enableBlend();
		GlStateManager.enableAlpha();

		TurokGL.color(color);

		if (shadow) {
			if (fontRenderer.isRenderingCustomFont()) {
				fontRenderer.drawStringWithShadow(string, x, y, color.getRGB());
			} else {
				Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(string, (int) x, (int) y, color.getRGB());
			}
		} else {
			if (fontRenderer.isRenderingCustomFont()) {
				fontRenderer.drawString(string, x, y, color.getRGB());
			} else {
				Minecraft.getMinecraft().fontRenderer.drawString(string, (int) x, (int) y, color.getRGB());
			}
		}

		TurokGL.disable(GL11.GL_TEXTURE_2D);
	}

	public static int getStringWidth(TurokFont fontRenderer, String string) {
		return fontRenderer.isRenderingCustomFont() ? (int) fontRenderer.getStringWidth(string) : Minecraft.getMinecraft().fontRenderer.getStringWidth(string);
	}

	public static int getStringWidth(String string) {
		return Minecraft.getMinecraft().fontRenderer.getStringWidth(string);
	}

	public static int getStringHeight(TurokFont fontRenderer, String string) {
		return fontRenderer.isRenderingCustomFont() ? (int) fontRenderer.getStringHeight(string) : Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT * fontRenderer.getFontSize();
	}

	public static int getStringHeight(String string) {
		return Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT;
	}
}
