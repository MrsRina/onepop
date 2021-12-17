package rina.onepop.club.api.util.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.opengl.GL11;
import rina.onepop.club.Onepop;
import rina.onepop.club.api.engine.opengl.Statement;

import java.awt.*;

/**
 * @author SrRina
 * @since 22/10/2020 at 1:44pm
 */
public class RenderUtil {
    public static Tessellator tessellator = Tessellator.getInstance();

    public static void glBillboard(final float x, final float y, final float z) {
        final Minecraft mc = Onepop.getMinecraft();

        final float scale = 0.02666667f;
        GlStateManager.translate(x - mc.getRenderManager().renderPosX, y - mc.getRenderManager().renderPosY, z - mc.getRenderManager().renderPosZ);
        GlStateManager.glNormal3f(0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(-mc.player.rotationYaw, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(mc.player.rotationPitch, (mc.gameSettings.thirdPersonView == 2) ? -1.0f : 1.0f, 0.0f, 0.0f);
        GlStateManager.scale(-scale, -scale, scale);
    }

    public static void glBillboardDistanceScaled(final float x, final float y, final float z, final EntityPlayer player, final float scale) {
        glBillboard(x, y, z);

        final int distance = (int) player.getDistance(x, y, z);
        float scaleDistance = distance / 2.0f / (2.0f + (2.0f - scale));

        if (scaleDistance < 1.0f) {
            scaleDistance = 1.0f;
        }

        GlStateManager.scale(scaleDistance, scaleDistance, scaleDistance);
    }

    public static void drawText(final BlockPos pos, final String text) {
        final Minecraft mc = Onepop.getMinecraft();

        GlStateManager.pushMatrix();
        glBillboardDistanceScaled(pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f, mc.player, 1.0f);

        GlStateManager.disableDepth();
        GlStateManager.translate(-(mc.fontRenderer.getStringWidth(text) / 2.0), 0.0, 0.0);

        mc.fontRenderer.drawStringWithShadow(text, 0.0f, 0.0f, -5592406);

        GlStateManager.popMatrix();
    }

    public static void prepare(float line) {
        Statement.matrix();
        Statement.blend();

        Statement.unset(GL11.GL_TEXTURE_2D);
        Statement.unset(GL11.GL_DEPTH_TEST);

        Statement.set(GL11.GL_LINE_SMOOTH);
        Statement.line(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST, line);

        Statement.unsetDepthMask();
    }

    public static void release() {
        Statement.setDepthMask();

        Statement.set(GL11.GL_DEPTH_TEST);
        Statement.set(GL11.GL_TEXTURE_2D);

        Statement.unset(GL11.GL_LINE_SMOOTH);
        Statement.refresh();
    }

    public static void drawSolidBlock(ICamera camera, BlockPos blockpos, Color color) {
        drawSolidBlock(camera, blockpos.x, blockpos.y, blockpos.z, 1, 1, 1, color);
    }

    public static void drawSolidBlock(ICamera camera, double x, double y, double z, Color color) {
        drawSolidBlock(camera, x, y, z, 1, 1, 1, color);
    }

    public static void drawSolidBlock(ICamera camera, double x, double y, double z, double offsetX, double offsetY, double offsetZ, Color color) {
        final AxisAlignedBB bb = new AxisAlignedBB(
            x - Onepop.getMinecraft().getRenderManager().viewerPosX,
            y - Onepop.getMinecraft().getRenderManager().viewerPosY,
            z - Onepop.getMinecraft().getRenderManager().viewerPosZ,

            x + offsetX - Onepop.getMinecraft().getRenderManager().viewerPosX,
            y + offsetY - Onepop.getMinecraft().getRenderManager().viewerPosY,
            z + offsetZ - Onepop.getMinecraft().getRenderManager().viewerPosZ
        );

        camera.setPosition(Onepop.getMinecraft().getRenderViewEntity().posX, Onepop.getMinecraft().getRenderViewEntity().posY, Onepop.getMinecraft().getRenderViewEntity().posZ);

        if (camera.isBoundingBoxInFrustum(new AxisAlignedBB(
            bb.minX + Onepop.getMinecraft().getRenderManager().viewerPosX,
            bb.minY + Onepop.getMinecraft().getRenderManager().viewerPosY,
            bb.minZ + Onepop.getMinecraft().getRenderManager().viewerPosZ,

            bb.maxX + Onepop.getMinecraft().getRenderManager().viewerPosX,
            bb.maxY + Onepop.getMinecraft().getRenderManager().viewerPosY,
            bb.maxZ + Onepop.getMinecraft().getRenderManager().viewerPosZ)))
        {
            prepare(1.0f);

            RenderGlobal.renderFilledBox(bb.minX, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ, color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, color.getAlpha() / 255.0f);

            release();
        }
    }

    public static void drawOutlineBlock(ICamera camera, BlockPos blockpos, Color color) {
        drawOutlineBlock(camera, blockpos.x, blockpos.y, blockpos.z, 1, 1, 1, 1.0f, color);
    }

    public static void drawOutlineBlock(ICamera camera, BlockPos blockpos, float line, Color color) {
        drawOutlineBlock(camera, blockpos.x, blockpos.y, blockpos.z, 1, 1, 1, line, color);
    }

    public static void drawOutlineBlock(ICamera camera, double x, double y, double z, Color color) {
        drawOutlineBlock(camera, x, y, z, 1, 1, 1, 1.0f, color);
    }

    public static void drawOutlineBlock(ICamera camera, double x, double y, double z, float line, Color color) {
        drawOutlineBlock(camera, x, y, z, 1, 1, 1, line, color);
    }

    public static void drawOutlineBlock(ICamera camera, double x, double y, double z, double offsetX, double offsetY, double offsetZ, Color color) {
        drawOutlineBlock(camera, x, y, z, offsetX, offsetY, offsetZ, 1.0f, color);
    }

    public static void drawOutlineBlock(ICamera camera, double x, double y, double z, double offsetX, double offsetY, double offsetZ, float line, Color color) {
        final AxisAlignedBB bb = new AxisAlignedBB(
                x - Onepop.getMinecraft().getRenderManager().viewerPosX,
                y - Onepop.getMinecraft().getRenderManager().viewerPosY,
                z - Onepop.getMinecraft().getRenderManager().viewerPosZ,

                x + offsetX - Onepop.getMinecraft().getRenderManager().viewerPosX,
                y + offsetY - Onepop.getMinecraft().getRenderManager().viewerPosY,
                z + offsetZ - Onepop.getMinecraft().getRenderManager().viewerPosZ
        );

        camera.setPosition(Onepop.getMinecraft().getRenderViewEntity().posX, Onepop.getMinecraft().getRenderViewEntity().posY, Onepop.getMinecraft().getRenderViewEntity().posZ);

        if (camera.isBoundingBoxInFrustum(new AxisAlignedBB(
                bb.minX + Onepop.getMinecraft().getRenderManager().viewerPosX,
                bb.minY + Onepop.getMinecraft().getRenderManager().viewerPosY,
                bb.minZ + Onepop.getMinecraft().getRenderManager().viewerPosZ,

                bb.maxX + Onepop.getMinecraft().getRenderManager().viewerPosX,
                bb.maxY + Onepop.getMinecraft().getRenderManager().viewerPosY,
                bb.maxZ + Onepop.getMinecraft().getRenderManager().viewerPosZ)))
        {
            prepare(line);

            RenderGlobal.drawBoundingBox(bb.minX, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ, color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, color.getAlpha() / 255.0f);

            release();
        }
    }

    public static void drawBB(ICamera camera, AxisAlignedBB bb, Color color) {
        camera.setPosition(Onepop.getMinecraft().getRenderViewEntity().posX, Onepop.getMinecraft().getRenderViewEntity().posY, Onepop.getMinecraft().getRenderViewEntity().posZ);

        if (camera.isBoundingBoxInFrustum(new AxisAlignedBB(
                bb.minX + Onepop.getMinecraft().getRenderManager().viewerPosX,
                bb.minY + Onepop.getMinecraft().getRenderManager().viewerPosY,
                bb.minZ + Onepop.getMinecraft().getRenderManager().viewerPosZ,

                bb.maxX + Onepop.getMinecraft().getRenderManager().viewerPosX,
                bb.maxY + Onepop.getMinecraft().getRenderManager().viewerPosY,
                bb.maxZ + Onepop.getMinecraft().getRenderManager().viewerPosZ)))
        {
            prepare(1f);

            RenderGlobal.drawBoundingBox(bb.minX, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ, color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, color.getAlpha() / 255.0f);

            release();
        }
    }

    public static void drawOutlineLegacyBlock(ICamera camera, BlockPos blockpos, Color color) {
        drawOutlineLegacyBlock(camera, blockpos.x, blockpos.y, blockpos.z, 1, 1, 1, 1.0f, color);
    }

    public static void drawOutlineLegacyBlock(ICamera camera, BlockPos blockpos, float line, Color color) {
        drawOutlineLegacyBlock(camera, blockpos.x, blockpos.y, blockpos.z, 1, 1, 1, line, color);
    }

    public static void drawOutlineLegacyBlock(ICamera camera, double x, double y, double z, Color color) {
        drawOutlineLegacyBlock(camera, x, y, z, 1, 1, 1, 1.0f, color);
    }

    public static void drawOutlineLegacyBlock(ICamera camera, double x, double y, double z, float line, Color color) {
        drawOutlineLegacyBlock(camera, x, y, z, 1, 1, 1, line, color);
    }

    public static void drawOutlineLegacyBlock(ICamera camera, double x, double y, double z, double offsetX, double offsetY, double offsetZ, Color color) {
        drawOutlineLegacyBlock(camera, x, y, z, offsetX, offsetY, offsetZ, 1.0f, color);
    }

    public static void drawOutlineLegacyBlock(ICamera camera, double minX, double minY, double minZ, double maxX, double maxY, double maxZ, float line, Color color) {
        final AxisAlignedBB bb = new AxisAlignedBB(
                minX - Onepop.getMinecraft().getRenderManager().viewerPosX,
                minY - Onepop.getMinecraft().getRenderManager().viewerPosY,
                minZ - Onepop.getMinecraft().getRenderManager().viewerPosZ,

                minX + maxX - Onepop.getMinecraft().getRenderManager().viewerPosX,
                minY + maxY - Onepop.getMinecraft().getRenderManager().viewerPosY,
                minZ + maxZ - Onepop.getMinecraft().getRenderManager().viewerPosZ
        );

        camera.setPosition(Onepop.getMinecraft().getRenderViewEntity().posX, Onepop.getMinecraft().getRenderViewEntity().posY, Onepop.getMinecraft().getRenderViewEntity().posZ);

        if (camera.isBoundingBoxInFrustum(new AxisAlignedBB(
                bb.minX + Onepop.getMinecraft().getRenderManager().viewerPosX,
                bb.minY + Onepop.getMinecraft().getRenderManager().viewerPosY,
                bb.minZ + Onepop.getMinecraft().getRenderManager().viewerPosZ,

                bb.maxX + Onepop.getMinecraft().getRenderManager().viewerPosX,
                bb.maxY + Onepop.getMinecraft().getRenderManager().viewerPosY,
                bb.maxZ + Onepop.getMinecraft().getRenderManager().viewerPosZ)))
        {

            float red = color.getRed() / 255f;
            float green = color.getGreen() / 255f;
            float blue = color.getBlue() / 255f;
            float alpha = color.getAlpha() / 255f;

            prepare(line);

            GL11.glEnable(GL11.GL_BLEND);

            BufferBuilder bufferBuilder = tessellator.getBuffer();
            bufferBuilder.begin(GL11.GL_LINE_LOOP, DefaultVertexFormats.POSITION_COLOR);

            bufferBuilder.pos(bb.minX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
            bufferBuilder.pos(bb.minX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
            bufferBuilder.pos(bb.maxX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
            bufferBuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
            bufferBuilder.pos(bb.minX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
            bufferBuilder.pos(bb.minX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
            bufferBuilder.pos(bb.minX, bb.maxY / 2, bb.minZ).color(red, green, blue, alpha).endVertex();
            bufferBuilder.pos(bb.minX, bb.maxY, bb.minZ).color(red, green, blue, 0).endVertex();
            bufferBuilder.pos(bb.maxX, bb.maxY, bb.minZ).color(red, green, blue, 0f).endVertex();
            bufferBuilder.pos(bb.maxX, bb.maxY, bb.maxZ).color(red, green, blue, 0f).endVertex();
            bufferBuilder.pos(bb.minX, bb.maxY, bb.maxZ).color(red, green, blue, 0f).endVertex();
            bufferBuilder.pos(bb.minX, bb.maxY, bb.minZ).color(red, green, blue, 0f).endVertex();
            bufferBuilder.pos(bb.minX, bb.maxY, bb.maxZ).color(red, green, blue, 0f).endVertex();
            bufferBuilder.pos(bb.minX, bb.minY, bb.maxZ).color(red, green, blue, 0f).endVertex();
            bufferBuilder.pos(bb.maxX, bb.maxY, bb.maxZ).color(red, green, blue, 0f).endVertex();
            bufferBuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(red, green, blue, 0f).endVertex();
            bufferBuilder.pos(bb.maxX, bb.maxY, bb.minZ).color(red, green, blue, 0f).endVertex();
            bufferBuilder.pos(bb.maxX, bb.minY, bb.minZ).color(red, green, blue, 0f).endVertex();
            bufferBuilder.pos(bb.maxX, bb.minY, bb.minZ).color(red, green, blue, 0f).endVertex();

            tessellator.draw();

            release();
        }
    }

    public static void drawSolidLegacyBlock(ICamera camera, BlockPos blockpos, Color color) {
        drawSolidLegacyBlock(camera, blockpos.x, blockpos.y, blockpos.z, 1, 1, 1, color);
    }

    public static void drawSolidLegacyBlock(ICamera camera, BlockPos blockpos, double offsetX, double offsetY, double offsetZ, Color color) {
        drawSolidLegacyBlock(camera, blockpos.x, blockpos.y, blockpos.z, offsetX, offsetY, offsetZ, color);
    }

    public static void drawSolidLegacyBlock(ICamera camera, double x, double y, double z, Color color) {
        drawSolidLegacyBlock(camera, x, y, z, 1, 1, 1, color);
    }

    public static void drawSolidLegacyBlock(ICamera camera, double minX, double minY, double minZ, double maxX, double maxY, double maxZ, Color color) {
        final AxisAlignedBB bb = new AxisAlignedBB(
                minX - Onepop.getMinecraft().getRenderManager().viewerPosX,
                minY - Onepop.getMinecraft().getRenderManager().viewerPosY,
                minZ - Onepop.getMinecraft().getRenderManager().viewerPosZ,

                minX + maxX - Onepop.getMinecraft().getRenderManager().viewerPosX,
                minY + maxY - Onepop.getMinecraft().getRenderManager().viewerPosY,
                minZ + maxZ - Onepop.getMinecraft().getRenderManager().viewerPosZ
        );

        camera.setPosition(Onepop.getMinecraft().getRenderViewEntity().posX, Onepop.getMinecraft().getRenderViewEntity().posY, Onepop.getMinecraft().getRenderViewEntity().posZ);

        if (camera.isBoundingBoxInFrustum(new AxisAlignedBB(
                bb.minX + Onepop.getMinecraft().getRenderManager().viewerPosX,
                bb.minY + Onepop.getMinecraft().getRenderManager().viewerPosY,
                bb.minZ + Onepop.getMinecraft().getRenderManager().viewerPosZ,

                bb.maxX + Onepop.getMinecraft().getRenderManager().viewerPosX,
                bb.maxY + Onepop.getMinecraft().getRenderManager().viewerPosY,
                bb.maxZ + Onepop.getMinecraft().getRenderManager().viewerPosZ)))
        {

            float red = color.getRed() / 255f;
            float green = color.getGreen() / 255f;
            float blue = color.getBlue() / 255f;
            float alpha = color.getAlpha() / 255f;

            prepare(1f);

            BufferBuilder bufferBuilder = tessellator.getBuffer();
            bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);

            bufferBuilder.pos(minX, minY, minZ).color(red, green, blue, 0.0F).endVertex();
            bufferBuilder.pos(minX, minY, minZ).color(red, green, blue, alpha).endVertex();
            bufferBuilder.pos(maxX, minY, minZ).color(red, green, blue, alpha).endVertex();
            bufferBuilder.pos(maxX, minY, maxZ).color(red, green, blue, alpha).endVertex();
            bufferBuilder.pos(minX, minY, maxZ).color(red, green, blue, alpha).endVertex();
            bufferBuilder.pos(minX, minY, minZ).color(red, green, blue, alpha).endVertex();
            bufferBuilder.pos(minX, maxY, minZ).color(red, green, blue, alpha).endVertex();
            bufferBuilder.pos(maxX, maxY, minZ).color(red, green, blue, alpha).endVertex();
            bufferBuilder.pos(maxX, maxY, maxZ).color(red, green, blue, alpha).endVertex();
            bufferBuilder.pos(minX, maxY, maxZ).color(red, green, blue, alpha).endVertex();
            bufferBuilder.pos(minX, maxY, minZ).color(red, green, blue, alpha).endVertex();
            bufferBuilder.pos(minX, maxY, maxZ).color(red, green, blue, 0.0F).endVertex();
            bufferBuilder.pos(minX, minY, maxZ).color(red, green, blue, alpha).endVertex();
            bufferBuilder.pos(maxX, maxY, maxZ).color(red, green, blue, 0.0F).endVertex();
            bufferBuilder.pos(maxX, minY, maxZ).color(red, green, blue, alpha).endVertex();
            bufferBuilder.pos(maxX, maxY, minZ).color(red, green, blue, 0.0F).endVertex();
            bufferBuilder.pos(maxX, minY, minZ).color(red, green, blue, alpha).endVertex();
            bufferBuilder.pos(maxX, minY, minZ).color(red, green, blue, 0.0F).endVertex();

            tessellator.draw();

            release();
        }
    }
}