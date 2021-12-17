package rina.onepop.club.client.render;

import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.AxisAlignedBB;
import org.lwjgl.opengl.GL11;
import rina.onepop.club.Onepop;
import rina.onepop.club.api.engine.Engine;
import rina.onepop.club.api.engine.caller.Processor;
import rina.onepop.club.api.engine.opengl.Statement;
import rina.onepop.club.client.render.type.EnumCube;

import java.awt.*;

/**
 * @author SrRina
 * @since 15/07/2021 at 00:25
 **/
public class Cube {
    public static void prepareModernSetup(boolean depth, float line) {
        Statement.matrix();
        Statement.blend();

        Statement.set(GL11.GL_CULL_FACE);
        Statement.unset(GL11.GL_TEXTURE_2D);

        if (depth) {
            Statement.unset(GL11.GL_DEPTH_TEST);
        }

        if (line != -1) {
            Statement.set(GL11.GL_LINE_SMOOTH);
            Statement.line(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST, line);
        }

        Statement.setShaderModel(GL11.GL_SMOOTH);

        Statement.unsetDepthMask();
        Statement.setFrontCullFace();
    }

    public static void releaseModern() {
        Statement.setDepthMask();
        Statement.setBackCullFace();

        Statement.unset(GL11.GL_CULL_FACE);

        Statement.set(GL11.GL_DEPTH_TEST);
        Statement.set(GL11.GL_TEXTURE_2D);

        Statement.unset(GL11.GL_LINE_SMOOTH);
        Statement.refresh();
    }

    public static void prepareNormalSetup(boolean depth, float line) {
        Statement.matrix();
        Statement.blend();

        Statement.unset(GL11.GL_TEXTURE_2D);

        if (depth) {
            Statement.unset(GL11.GL_DEPTH_TEST);
        }

        if (line != -1) {
            Statement.set(GL11.GL_LINE_SMOOTH);
            Statement.line(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST, line);
        }

        Statement.unsetDepthMask();
    }

    public static void releaseNormal() {
        Statement.setDepthMask();

        Statement.set(GL11.GL_DEPTH_TEST);
        Statement.set(GL11.GL_TEXTURE_2D);

        Statement.unset(GL11.GL_LINE_SMOOTH);
        Statement.refresh();
    }

    public static void drawBounding(final double[] position, final double[] size, Color topColor, Color bottomColor, int mode) {
        double offset = 0.011111111f;

        final double minX = position[0] + offset;
        final double minY = position[1] + offset;
        final double minZ = position[2] + offset;

        final double maxX = size[0] - offset;
        final double maxY = size[1] - offset;
        final double maxZ = size[2] - offset;

        final int rBottom = bottomColor.getRed();
        final int gBottom = bottomColor.getGreen();
        final int bBottom = bottomColor.getBlue();
        final int aBottom = bottomColor.getAlpha();

        final int rTop = topColor.getRed();
        final int gTop = topColor.getGreen();
        final int bTop = topColor.getBlue();
        final int aTop = topColor.getAlpha();

        final Processor theGPU = Engine.callGPU();
        final boolean flag = maxY > minY;

        theGPU.setBuffer();
        theGPU.work().begin(mode, DefaultVertexFormats.POSITION_COLOR);

        theGPU.work().pos(minX, minY, minZ).color(rBottom, gBottom, bBottom, 0.0f).endVertex();
        theGPU.work().pos(minX, minY, minZ).color(rBottom, gBottom, bBottom, aBottom).endVertex();
        theGPU.work().pos(minX, minY, minZ).color(rBottom, gBottom, bBottom, aBottom).endVertex();
        theGPU.work().pos(maxX, minY, minZ).color(rBottom, gBottom, bBottom, aBottom).endVertex();
        theGPU.work().pos(maxX, minY, maxZ).color(rBottom, gBottom, bBottom, aBottom).endVertex();
        theGPU.work().pos(minX, minY, maxZ).color(rBottom, gBottom, bBottom, aBottom).endVertex();
        theGPU.work().pos(minX, minY, minZ).color(rBottom, gBottom, bBottom, aBottom).endVertex();

        if (flag) {
            theGPU.work().pos(minX, maxY, minZ).color(rTop, gTop, bTop, aTop).endVertex();
            theGPU.work().pos(maxX, maxY, minZ).color(rTop, gTop, bTop, aTop).endVertex();
            theGPU.work().pos(maxX, maxY, maxZ).color(rTop, gTop, bTop, aTop).endVertex();
            theGPU.work().pos(minX, maxY, maxZ).color(rTop, gTop, bTop, aTop).endVertex();
            theGPU.work().pos(minX, maxY, minZ).color(rTop, gTop, bTop, aTop).endVertex();
            theGPU.work().pos(minX, maxY, maxZ).color(rTop, gTop, bTop, aTop).endVertex();
        }

        theGPU.work().pos(minX, minY, maxZ).color(rBottom, gBottom, bBottom, aBottom).endVertex();

        if (flag) {
            theGPU.work().pos(maxX, maxY, maxZ).color(rTop, gTop, bTop, 0.0f).endVertex();
        }

        theGPU.work().pos(maxX, minY, maxZ).color(rBottom, gBottom, bBottom, aBottom).endVertex();

        if (flag) {
            theGPU.work().pos(maxX, maxY, minZ).color(rTop, gTop, bTop, 0.0f).endVertex();
        }

        theGPU.work().pos(maxX, minY, minZ).color(rBottom, gBottom, bBottom, aBottom).endVertex();
        theGPU.work().pos(maxX, minY, minZ).color(rBottom, gBottom, bBottom, 0.0f).endVertex();

        theGPU.unsetBuffer();
    }

    public static void drawFilled(final double[] position, final double[] size, Color topColor, Color bottomColor, int mode) {
        double offset = 0.011111111f;

        final double minX = position[0] + offset;
        final double minY = position[1] + offset;
        final double minZ = position[2] + offset;

        final double maxX = size[0] - offset;
        final double maxY = size[1] - offset;
        final double maxZ = size[2] - offset;

        final int rBottom = bottomColor.getRed();
        final int gBottom = bottomColor.getGreen();
        final int bBottom = bottomColor.getBlue();
        final int aBottom = bottomColor.getAlpha();

        final int rTop = topColor.getRed();
        final int gTop = topColor.getGreen();
        final int bTop = topColor.getBlue();
        final int aTop = topColor.getAlpha();

        final Processor theGPU = Engine.callGPU();
        final boolean flag = maxY > minY;

        theGPU.setBuffer();
        theGPU.work().begin(mode, DefaultVertexFormats.POSITION_COLOR);

        if (mode == GL11.GL_LINES) {
            if (flag) {
                theGPU.work().pos(minX, minY, minZ).color(rBottom, gBottom, bBottom, aBottom).endVertex();
                theGPU.work().pos(minX, maxY, minZ).color(rTop, gTop, bTop, aTop).endVertex();

                theGPU.work().pos(minX, maxY, maxZ).color(rTop, gTop, bTop, aTop).endVertex();
                theGPU.work().pos(minX, maxY, minZ).color(rTop, gTop, bTop, aTop).endVertex();
            }

            theGPU.work().pos(minX, minY, minZ).color(rBottom, gBottom, bBottom, aBottom).endVertex();
            theGPU.work().pos(maxX, minY, minZ).color(rBottom, gBottom, bBottom, aBottom).endVertex();

            if (flag) {
                theGPU.work().pos(maxX, minY, minZ).color(rBottom, gBottom, bBottom, aBottom).endVertex();
                theGPU.work().pos(maxX, maxY, minZ).color(rTop, gTop, bTop, aTop).endVertex();

                theGPU.work().pos(minX, maxY, minZ).color(rTop, gTop, bTop, aTop).endVertex();
                theGPU.work().pos(maxX, maxY, minZ).color(rTop, gTop, bTop, aTop).endVertex();
            }

            theGPU.work().pos(maxX, minY, minZ).color(rBottom, gBottom, bBottom, aBottom).endVertex();
            theGPU.work().pos(maxX, minY, maxZ).color(rBottom, gBottom, bBottom, aBottom).endVertex();

            if (flag) {
                theGPU.work().pos(maxX, minY, maxZ).color(rBottom, gBottom, bBottom, aBottom).endVertex();
                theGPU.work().pos(maxX, maxY, maxZ).color(rTop, gTop, bTop, aTop).endVertex();

                theGPU.work().pos(maxX, maxY, minZ).color(rTop, gTop, bTop, aTop).endVertex();
                theGPU.work().pos(maxX, maxY, maxZ).color(rTop, gTop, bTop, aTop).endVertex();
            }

            theGPU.work().pos(maxX, minY, maxZ).color(rBottom, gBottom, bBottom, aBottom).endVertex();
            theGPU.work().pos(minX, minY, maxZ).color(rBottom, gBottom, bBottom, aBottom).endVertex();

            if (flag) {
                theGPU.work().pos(minX, minY, maxZ).color(rBottom, gBottom, bBottom, aBottom).endVertex();
                theGPU.work().pos(minX, maxY, maxZ).color(rTop, gTop, bTop, aTop).endVertex();

                theGPU.work().pos(maxX, maxY, maxZ).color(rTop, gTop, bTop, aTop).endVertex();
                theGPU.work().pos(minX, maxY, maxZ).color(rTop, gTop, bTop, aTop).endVertex();
            }

            theGPU.work().pos(minX, minY, maxZ).color(rBottom, gBottom, bBottom, aBottom).endVertex();
            theGPU.work().pos(minX, minY, minZ).color(rBottom, gBottom, bBottom, aBottom).endVertex();
        } else {
            RenderGlobal.addChainedFilledBoxVertices(theGPU.work(), minX, minY, minZ, maxX, maxY, maxZ, rBottom / 255f, gBottom / 255f, bBottom / 255f, aBottom / 255f);
        }

        theGPU.unsetBuffer();
    }

    public static void drawModern(final double[] position, final double[] size, Color topColor, Color bottomColor, int mode) {
        double offset = 0.011111111f;

        final double minX = position[0] + offset;
        final double minY = position[1] + offset;
        final double minZ = position[2] + offset;

        final double maxX = size[0] - offset;
        final double maxY = size[1] - offset;
        final double maxZ = size[2] - offset;

        final int rBottom = bottomColor.getRed();
        final int gBottom = bottomColor.getGreen();
        final int bBottom = bottomColor.getBlue();
        final int aBottom = bottomColor.getAlpha();

        final int rTop = topColor.getRed();
        final int gTop = topColor.getGreen();
        final int bTop = topColor.getBlue();
        final int aTop = topColor.getAlpha();

        final Processor theGPU = Engine.callGPU();
        final boolean flag = maxY > minY;

        theGPU.setBuffer();
        theGPU.work().begin(mode, DefaultVertexFormats.POSITION_COLOR);

        if (mode == GL11.GL_LINES) {
            if (flag) {
                theGPU.work().pos(minX, minY, minZ).color(rBottom, gBottom, bBottom, aBottom).endVertex();
                theGPU.work().pos(minX, maxY, minZ).color(rTop, gTop, bTop, aTop).endVertex();

                theGPU.work().pos(minX, maxY, maxZ).color(rTop, gTop, bTop, aTop).endVertex();
                theGPU.work().pos(minX, maxY, minZ).color(rTop, gTop, bTop, aTop).endVertex();
            }

            theGPU.work().pos(minX, minY, minZ).color(rBottom, gBottom, bBottom, aBottom).endVertex();
            theGPU.work().pos(maxX, minY, minZ).color(rBottom, gBottom, bBottom, aBottom).endVertex();

            if (flag) {
                theGPU.work().pos(maxX, minY, minZ).color(rBottom, gBottom, bBottom, aBottom).endVertex();
                theGPU.work().pos(maxX, maxY, minZ).color(rTop, gTop, bTop, aTop).endVertex();

                theGPU.work().pos(minX, maxY, minZ).color(rTop, gTop, bTop, aTop).endVertex();
                theGPU.work().pos(maxX, maxY, minZ).color(rTop, gTop, bTop, aTop).endVertex();
            }

            theGPU.work().pos(maxX, minY, minZ).color(rBottom, gBottom, bBottom, aBottom).endVertex();
            theGPU.work().pos(maxX, minY, maxZ).color(rBottom, gBottom, bBottom, aBottom).endVertex();

            if (flag) {
                theGPU.work().pos(maxX, minY, maxZ).color(rBottom, gBottom, bBottom, aBottom).endVertex();
                theGPU.work().pos(maxX, maxY, maxZ).color(rTop, gTop, bTop, aTop).endVertex();

                theGPU.work().pos(maxX, maxY, minZ).color(rTop, gTop, bTop, aTop).endVertex();
                theGPU.work().pos(maxX, maxY, maxZ).color(rTop, gTop, bTop, aTop).endVertex();
            }

            theGPU.work().pos(maxX, minY, maxZ).color(rBottom, gBottom, bBottom, aBottom).endVertex();
            theGPU.work().pos(minX, minY, maxZ).color(rBottom, gBottom, bBottom, aBottom).endVertex();

            if (flag) {
                theGPU.work().pos(minX, minY, maxZ).color(rBottom, gBottom, bBottom, aBottom).endVertex();
                theGPU.work().pos(minX, maxY, maxZ).color(rTop, gTop, bTop, aTop).endVertex();

                theGPU.work().pos(maxX, maxY, maxZ).color(rTop, gTop, bTop, aTop).endVertex();
                theGPU.work().pos(minX, maxY, maxZ).color(rTop, gTop, bTop, aTop).endVertex();
            }

            theGPU.work().pos(minX, minY, maxZ).color(rBottom, gBottom, bBottom, aBottom).endVertex();
            theGPU.work().pos(minX, minY, minZ).color(rBottom, gBottom, bBottom, aBottom).endVertex();
        } else {
            theGPU.work().pos(minX, minY, minZ).color(rBottom, gBottom, bBottom, aBottom).endVertex();
            theGPU.work().pos(maxX, minY, minZ).color(rBottom, gBottom, bBottom, aBottom).endVertex();
            theGPU.work().pos(maxX, minY, maxZ).color(rBottom, gBottom, bBottom, aBottom).endVertex();
            theGPU.work().pos(minX, minY, maxZ).color(rBottom, gBottom, bBottom, aBottom).endVertex();

            if (flag) {
                theGPU.work().pos(minX, maxY, minZ).color(rTop, gTop, bTop, aTop).endVertex();
                theGPU.work().pos(minX, maxY, maxZ).color(rTop, gTop, bTop, aTop).endVertex();
                theGPU.work().pos(maxX, maxY, maxZ).color(rTop, gTop, bTop, aTop).endVertex();
                theGPU.work().pos(maxX, maxY, minZ).color(rTop, gTop, bTop, aTop).endVertex();
            }

            theGPU.work().pos(minX, minY, minZ).color(rBottom, gBottom, bBottom, aBottom).endVertex();

            if (flag) {
                theGPU.work().pos(minX, maxY, minZ).color(rTop, gTop, bTop, aTop).endVertex();
                theGPU.work().pos(maxX, maxY, minZ).color(rTop, gTop, bTop, aTop).endVertex();
            }

            theGPU.work().pos(maxX, minY, minZ).color(rBottom, gBottom, bBottom, aBottom).endVertex();
            theGPU.work().pos(maxX, minY, minZ).color(rBottom, gBottom, bBottom, aBottom).endVertex();

            if (flag) {
                theGPU.work().pos(maxX, maxY, minZ).color(rTop, gTop, bTop, aTop).endVertex();
                theGPU.work().pos(maxX, maxY, maxZ).color(rTop, gTop, bTop, aTop).endVertex();
            }

            theGPU.work().pos(maxX, minY, maxZ).color(rBottom, gBottom, bBottom, aBottom).endVertex();
            theGPU.work().pos(minX, minY, maxZ).color(rBottom, gBottom, bBottom, aBottom).endVertex();
            theGPU.work().pos(maxX, minY, maxZ).color(rBottom, gBottom, bBottom, aBottom).endVertex();

            if (flag) {
                theGPU.work().pos(maxX, maxY, maxZ).color(rTop, gTop, bTop, aTop).endVertex();
                theGPU.work().pos(minX, maxY, maxZ).color(rTop, gTop, bTop, aTop).endVertex();
            }

            theGPU.work().pos(minX, minY, minZ).color(rBottom, gBottom, bBottom, aBottom).endVertex();
            theGPU.work().pos(minX, minY, maxZ).color(rBottom, gBottom, bBottom, aBottom).endVertex();

            if (flag) {
                theGPU.work().pos(minX, maxY, maxZ).color(rTop, gTop, bTop, aTop).endVertex();
                theGPU.work().pos(minX, maxY, minZ).color(rTop, gTop, bTop, aTop).endVertex();
            }
        }

        theGPU.unsetBuffer();
    }

    public static void render(ICamera frustum, double[] position, double[] size, float line, Color[] colors, boolean depth, EnumCube cube) {
        final AxisAlignedBB bb = new AxisAlignedBB(
                position[0] - Onepop.getMinecraft().getRenderManager().viewerPosX,
                position[1] - Onepop.getMinecraft().getRenderManager().viewerPosY,
                position[2] - Onepop.getMinecraft().getRenderManager().viewerPosZ,

                position[0] + size[0] - Onepop.getMinecraft().getRenderManager().viewerPosX,
                position[1] + size[1] - Onepop.getMinecraft().getRenderManager().viewerPosY,
                position[2] + size[2] - Onepop.getMinecraft().getRenderManager().viewerPosZ
        );

        if (Onepop.getMinecraft().getRenderViewEntity() == null) {
            return;
        }

        frustum.setPosition(Onepop.getMinecraft().getRenderViewEntity().posX, Onepop.getMinecraft().getRenderViewEntity().posY, Onepop.getMinecraft().getRenderViewEntity().posZ);

        if (frustum.isBoundingBoxInFrustum(new AxisAlignedBB(
                bb.minX + Onepop.getMinecraft().getRenderManager().viewerPosX,
                bb.minY + Onepop.getMinecraft().getRenderManager().viewerPosY,
                bb.minZ + Onepop.getMinecraft().getRenderManager().viewerPosZ,

                bb.maxX + Onepop.getMinecraft().getRenderManager().viewerPosX,
                bb.maxY + Onepop.getMinecraft().getRenderManager().viewerPosY,
                bb.maxZ + Onepop.getMinecraft().getRenderManager().viewerPosZ)))
        {
            switch (cube) {
                case MODERN: {
                    prepareModernSetup(depth, line == -1 ? 1 : line);

                    int mode = line == -1 ? GL11.GL_QUADS : GL11.GL_LINES;

                    drawModern(new double[]{bb.minX, bb.minY, bb.minZ}, new double[]{bb.maxX, bb.maxY, bb.maxZ}, colors[1], colors[0], mode);

                    releaseModern();

                    break;
                }

                case NORMAL: {
                    prepareNormalSetup(depth, line == -1 ? 1 : line);

                    int mode = line == -1 ? 5 : GL11.GL_LINES;

                    drawFilled(new double[]{bb.minX, bb.minY, bb.minZ}, new double[]{bb.maxX, bb.maxY, bb.maxZ}, colors[1], colors[0], mode);

                    releaseNormal();

                    break;
                }
            }
        }
    }
}
