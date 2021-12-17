package rina.onepop.club.client.module.render.esp.process;

import me.rina.turok.render.opengl.TurokGL;
import me.rina.turok.render.opengl.TurokRenderGL;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;
import rina.onepop.club.api.engine.opengl.Statement;
import rina.onepop.club.api.setting.value.ValueColor;
import rina.onepop.club.api.util.entity.EntityUtil;
import rina.onepop.club.api.util.entity.PlayerUtil;
import rina.onepop.club.api.util.math.PositionUtil;
import rina.onepop.club.client.event.render.RenderModelEvent;
import rina.onepop.club.client.module.render.ModuleBacktrack;
import rina.onepop.club.client.module.render.esp.impl.Mode;

import java.awt.*;

/**
 * @author SrRina
 * @since 11/07/2021 at 01:20
 **/
public class ProcessESP {
    public static void renderBacktrack(ModuleBacktrack.Clone clone, EntityPlayer player, float s, float o, float a, float l, boolean lineCull, boolean frustum, Color value, Mode mode) {
        float scale = s / 1000f;
        float offset = -(o / 1000f);

        float alpha = a;

        switch (mode) {
            case FILL: {
                TurokGL.pushMatrix();
                TurokGL.pushAttrib(GL11.GL_ALL_ATTRIB_BITS);

                Statement.unset(GL11.GL_LIGHTING);
                Statement.unset(GL11.GL_TEXTURE_2D);
                Statement.unset(GL11.GL_DEPTH_TEST);

                Statement.blend();
                Statement.scale(scale, scale, scale);
                Statement.translate(0, offset, 0);

                TurokGL.color(value);

                clone.render(player, 1f);

                TurokGL.color(Color.WHITE);

                Statement.set(GL11.GL_LIGHTING);
                Statement.set(GL11.GL_TEXTURE_2D);
                Statement.set(GL11.GL_DEPTH_TEST);

                TurokGL.popAttrib();
                TurokGL.popMatrix();

                break;
            }

            case SKIN: {
                TurokGL.pushMatrix();
                TurokGL.pushAttrib(GL11.GL_ALL_ATTRIB_BITS);

                Statement.set(GL11.GL_TEXTURE_2D);
                Statement.set(GL11.GL_POLYGON_OFFSET_FILL);

                GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
                GL11.glPolygonOffset(1.0f, -1100000.0f);

                Statement.blend();
                Statement.scale(scale, scale, scale);
                Statement.translate(0, offset, 0);

                TurokGL.color(value);

                clone.render(player, 1f);

                GL11.glPolygonOffset(1.0f, 1100000.0f);

                TurokGL.popAttrib();
                TurokGL.popMatrix();

                break;
            }

            case SMOOTH: {
                TurokGL.pushMatrix();
                TurokGL.pushAttrib(GL11.GL_ALL_ATTRIB_BITS);

                Statement.unset(GL11.GL_LIGHTING);
                Statement.unset(GL11.GL_TEXTURE_2D);

                Statement.set(GL11.GL_CULL_FACE);
                Statement.set(GL11.GL_POLYGON_OFFSET_FILL);

                GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
                GL11.glPolygonOffset(1.0f, -1100000.0f);

                Statement.blend();
                Statement.scale(scale, scale, scale);
                Statement.translate(0, offset, 0);

                TurokGL.color(value);

                clone.render(player, 1f);

                GL11.glPolygonOffset(1.0f, 1100000.0f);

                TurokGL.color(Color.WHITE);

                Statement.set(GL11.GL_LIGHTING);
                Statement.set(GL11.GL_TEXTURE_2D);

                Statement.set(GL11.GL_CULL_FACE);

                TurokGL.popAttrib();
                TurokGL.popMatrix();

                TurokGL.pushMatrix();
                TurokGL.pushAttrib(GL11.GL_ALL_ATTRIB_BITS);

                Statement.unset(GL11.GL_TEXTURE_2D);
                Statement.set(GL11.GL_POLYGON_OFFSET_LINE);

                if (lineCull) {
                    Statement.set(GL11.GL_CULL_FACE);
                }

                GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
                GL11.glPolygonOffset(1.0f, -1100000.0f);

                Statement.set(GL11.GL_LINE_SMOOTH);
                Statement.line(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST, l);

                Statement.blend();
                Statement.scale(scale, scale, scale);
                Statement.translate(0, offset, 0);

                TurokGL.color(value.getRed(), value.getGreen(), value.getBlue(), a);

                clone.render(player, 1f);

                GL11.glPolygonOffset(1.0f, 1100000.0f);

                if (lineCull) {
                    Statement.unset(GL11.GL_CULL_FACE);
                }

                TurokGL.popAttrib();
                TurokGL.popMatrix();
            }

            case LINE: {
                TurokGL.pushMatrix();
                TurokGL.pushAttrib(GL11.GL_ALL_ATTRIB_BITS);

                Statement.unset(GL11.GL_TEXTURE_2D);
                Statement.unset(GL11.GL_LIGHTING);
                Statement.set(GL11.GL_POLYGON_OFFSET_LINE);

                if (lineCull) {
                    Statement.set(GL11.GL_CULL_FACE);
                }

                Statement.set(GL11.GL_LINE_SMOOTH);
                Statement.line(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST, l);

                GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
                GL11.glPolygonOffset(1.0f, -1100000.0f);

                Statement.blend();
                Statement.scale(scale, scale, scale);
                Statement.translate(0, offset, 0);

                TurokGL.color(value);

                clone.render(player, 1f);

                GL11.glPolygonOffset(1.0f, 1100000.0f);

                if (lineCull) {
                    Statement.unset(GL11.GL_CULL_FACE);
                }

                Statement.set(GL11.GL_LIGHTING);
                Statement.set(GL11.GL_TEXTURE_2D);

                Statement.unset(GL11.GL_POLYGON_OFFSET_LINE);

                TurokGL.popAttrib();
                TurokGL.popMatrix();

                break;
            }

            case OUTLINE: {
                if (!lineCull) {
                    TurokGL.pushMatrix();
                    TurokGL.pushAttrib(GL11.GL_ALL_ATTRIB_BITS);

                    Statement.unset(GL11.GL_DEPTH_TEST);
                    Statement.set(GL11.GL_LINE_SMOOTH);
                    Statement.unset(GL11.GL_TEXTURE_2D);
                    Statement.unset(GL11.GL_LIGHTING);

                    Statement.set(GL11.GL_POLYGON_OFFSET_LINE);

                    GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
                    GL11.glPolygonOffset(1.0f, -1100000.0f);

                    Statement.line(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST, l);

                    Statement.blend();
                    Statement.scale(scale, scale, scale);
                    Statement.translate(0, offset, 0);

                    TurokGL.color(value);

                    clone.render(player, 1f);

                    GL11.glPolygonOffset(1.0f, 1100000.0f);

                    TurokGL.enable(GL11.GL_POLYGON_OFFSET_FILL);

                    Statement.set(GL11.GL_LIGHTING);
                    Statement.set(GL11.GL_TEXTURE_2D);

                    Statement.set(GL11.GL_TEXTURE_2D);
                    Statement.set(GL11.GL_DEPTH_TEST);
                    Statement.set(GL11.GL_LIGHTING);
                    Statement.set(GL11.GL_POLYGON_OFFSET_FILL);

                    GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
                    GL11.glPolygonOffset(1.0f, -1100000.0f);

                    Statement.blend();
                    Statement.scale(scale, scale, scale);
                    Statement.translate(0, offset, 0);

                    TurokGL.color(255, 255, 255, alpha);

                    clone.render(player, 1f);

                    GL11.glPolygonOffset(1.0f, 1100000.0f);

                    Statement.set(GL11.GL_LIGHTING);
                    Statement.set(GL11.GL_TEXTURE_2D);

                    TurokGL.popAttrib();
                    Statement.refresh();
                }

                if (lineCull) {
                    TurokGL.pushMatrix();
                    TurokGL.pushAttrib(GL11.GL_ALL_ATTRIB_BITS);

                    Statement.set(GL11.GL_TEXTURE_2D);
                    Statement.set(GL11.GL_LIGHTING);
                    Statement.set(GL11.GL_POLYGON_OFFSET_FILL);

                    GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
                    GL11.glPolygonOffset(1.0f, -1100000.0f);

                    Statement.blend();
                    Statement.scale(scale, scale, scale);
                    Statement.translate(0, offset, 0);

                    TurokGL.color(255, 255, 255, alpha);

                    clone.render(player, 1f);

                    GL11.glPolygonOffset(1.0f, 1100000.0f);

                    TurokGL.popAttrib();
                    TurokGL.popMatrix();

                    TurokGL.pushMatrix();
                    TurokGL.pushAttrib(GL11.GL_ALL_ATTRIB_BITS);

                    Statement.unset(GL11.GL_TEXTURE_2D);
                    Statement.unset(GL11.GL_LIGHTING);
                    Statement.set(GL11.GL_POLYGON_OFFSET_LINE);

                    Statement.set(GL11.GL_CULL_FACE);

                    GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
                    GL11.glPolygonOffset(1.0f, -1100000.0f);

                    Statement.set(GL11.GL_LINE_SMOOTH);
                    Statement.line(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST, l);

                    Statement.blend();
                    Statement.scale(scale, scale, scale);
                    Statement.translate(0, offset, 0);

                    TurokGL.color(value);

                    clone.render(player, 1f);

                    GL11.glPolygonOffset(1.0f, 1100000.0f);

                    Statement.set(GL11.GL_LIGHTING);
                    Statement.set(GL11.GL_TEXTURE_2D);

                    TurokGL.popAttrib();
                    TurokGL.popMatrix();
                }

                break;
            }
        }

        if (frustum && mode != Mode.SKIN && mode != Mode.OUTLINE) {
            TurokGL.pushMatrix();
            TurokGL.pushAttrib(GL11.GL_ALL_ATTRIB_BITS);

            Statement.set(GL11.GL_TEXTURE_2D);

            Statement.blend();
            Statement.scale(scale, scale, scale);
            Statement.translate(0, offset, 0);

            TurokGL.color(255, 255, 255, 255);

            clone.render(player, 1f);

            TurokGL.popAttrib();
            TurokGL.popMatrix();
        }
    }

    public static void playerESP(RenderModelEvent event, float s, float o, float a, float l, boolean lineCull, boolean frustum, ValueColor value, Mode mode) {
        float scale = s / 1000f;
        float offset = -(o / 1000f);

        float alpha = a;

        switch (mode) {
            case FILL: {
                TurokGL.pushMatrix();
                TurokGL.pushAttrib(GL11.GL_ALL_ATTRIB_BITS);

                Statement.unset(GL11.GL_LIGHTING);
                Statement.unset(GL11.GL_TEXTURE_2D);
                Statement.unset(GL11.GL_DEPTH_TEST);

                Statement.blend();
                Statement.scale(scale, scale, scale);
                Statement.translate(0, offset, 0);

                TurokGL.color(value.getColor());

                event.getModelBase().render(event.getEntity(), event.getLimbSwing(), event.getLimbSwingAmount(), event.getAgeInTicks(), event.getNetHeadYaw(), event.getHeadPitch(), event.getScaleFactor());

                TurokGL.color(Color.WHITE);

                Statement.set(GL11.GL_LIGHTING);
                Statement.set(GL11.GL_TEXTURE_2D);
                Statement.set(GL11.GL_DEPTH_TEST);

                TurokGL.popAttrib();
                TurokGL.popMatrix();

                break;
            }

            case SKIN: {
                TurokGL.pushMatrix();
                TurokGL.pushAttrib(GL11.GL_ALL_ATTRIB_BITS);

                Statement.set(GL11.GL_TEXTURE_2D);
                Statement.set(GL11.GL_POLYGON_OFFSET_FILL);

                GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
                GL11.glPolygonOffset(1.0f, -1100000.0f);

                Statement.blend();
                Statement.scale(scale, scale, scale);
                Statement.translate(0, offset, 0);

                TurokGL.color(value.getColor());

                event.getModelBase().render(event.getEntity(), event.getLimbSwing(), event.getLimbSwingAmount(), event.getAgeInTicks(), event.getNetHeadYaw(), event.getHeadPitch(), event.getScaleFactor());

                GL11.glPolygonOffset(1.0f, 1100000.0f);

                TurokGL.popAttrib();
                TurokGL.popMatrix();

                break;
            }

            case SMOOTH: {
                TurokGL.pushMatrix();
                TurokGL.pushAttrib(GL11.GL_ALL_ATTRIB_BITS);

                Statement.unset(GL11.GL_LIGHTING);
                Statement.unset(GL11.GL_TEXTURE_2D);

                Statement.set(GL11.GL_CULL_FACE);
                Statement.set(GL11.GL_POLYGON_OFFSET_FILL);

                GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
                GL11.glPolygonOffset(1.0f, -1100000.0f);

                Statement.blend();
                Statement.scale(scale, scale, scale);
                Statement.translate(0, offset, 0);

                TurokGL.color(value.getColor());

                event.getModelBase().render(event.getEntity(), event.getLimbSwing(), event.getLimbSwingAmount(), event.getAgeInTicks(), event.getNetHeadYaw(), event.getHeadPitch(), event.getScaleFactor());

                GL11.glPolygonOffset(1.0f, 1100000.0f);

                TurokGL.color(Color.WHITE);

                Statement.set(GL11.GL_LIGHTING);
                Statement.set(GL11.GL_TEXTURE_2D);

                Statement.set(GL11.GL_CULL_FACE);

                TurokGL.popAttrib();
                TurokGL.popMatrix();

                TurokGL.pushMatrix();
                TurokGL.pushAttrib(GL11.GL_ALL_ATTRIB_BITS);

                Statement.unset(GL11.GL_TEXTURE_2D);
                Statement.set(GL11.GL_POLYGON_OFFSET_LINE);

                if (lineCull) {
                    Statement.set(GL11.GL_CULL_FACE);
                }

                GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
                GL11.glPolygonOffset(1.0f, -1100000.0f);

                Statement.set(GL11.GL_LINE_SMOOTH);
                Statement.line(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST, l);

                Statement.blend();
                Statement.scale(scale, scale, scale);
                Statement.translate(0, offset, 0);

                TurokGL.color(value.getColor((int) alpha));

                event.getModelBase().render(event.getEntity(), event.getLimbSwing(), event.getLimbSwingAmount(), event.getAgeInTicks(), event.getNetHeadYaw(), event.getHeadPitch(), event.getScaleFactor());

                GL11.glPolygonOffset(1.0f, 1100000.0f);

                if (lineCull) {
                    Statement.unset(GL11.GL_CULL_FACE);
                }

                TurokGL.popAttrib();
                TurokGL.popMatrix();
            }

            case LINE: {
                TurokGL.pushMatrix();
                TurokGL.pushAttrib(GL11.GL_ALL_ATTRIB_BITS);

                Statement.unset(GL11.GL_TEXTURE_2D);
                Statement.unset(GL11.GL_LIGHTING);
                Statement.set(GL11.GL_POLYGON_OFFSET_LINE);

                if (lineCull) {
                    Statement.set(GL11.GL_CULL_FACE);
                }

                GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
                GL11.glPolygonOffset(1.0f, -1100000.0f);

                Statement.set(GL11.GL_LINE_SMOOTH);
                Statement.line(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST, l);

                Statement.blend();
                Statement.scale(scale, scale, scale);
                Statement.translate(0, offset, 0);

                TurokGL.color(value.getColor());

                event.getModelBase().render(event.getEntity(), event.getLimbSwing(), event.getLimbSwingAmount(), event.getAgeInTicks(), event.getNetHeadYaw(), event.getHeadPitch(), event.getScaleFactor());

                GL11.glPolygonOffset(1.0f, 1100000.0f);

                if (lineCull) {
                    Statement.unset(GL11.GL_CULL_FACE);
                }

                Statement.set(GL11.GL_LIGHTING);
                Statement.set(GL11.GL_TEXTURE_2D);

                TurokGL.popAttrib();
                TurokGL.popMatrix();

                break;
            }

            case OUTLINE: {
                if (!lineCull) {
                    TurokGL.pushMatrix();
                    TurokGL.pushAttrib(GL11.GL_ALL_ATTRIB_BITS);

                    Statement.unset(GL11.GL_DEPTH_TEST);
                    Statement.set(GL11.GL_LINE_SMOOTH);
                    Statement.unset(GL11.GL_TEXTURE_2D);
                    Statement.unset(GL11.GL_LIGHTING);

                    Statement.set(GL11.GL_POLYGON_OFFSET_LINE);

                    GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
                    GL11.glPolygonOffset(1.0f, -1100000.0f);

                    Statement.line(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST, l);

                    Statement.blend();
                    Statement.scale(scale, scale, scale);
                    Statement.translate(0, offset, 0);

                    TurokGL.color(value.getColor());

                    event.getModelBase().render(event.getEntity(), event.getLimbSwing(), event.getLimbSwingAmount(), event.getAgeInTicks(), event.getNetHeadYaw(), event.getHeadPitch(), event.getScaleFactor());

                    GL11.glPolygonOffset(1.0f, 1100000.0f);

                    TurokGL.enable(GL11.GL_POLYGON_OFFSET_FILL);

                    Statement.set(GL11.GL_LIGHTING);
                    Statement.set(GL11.GL_TEXTURE_2D);

                    Statement.set(GL11.GL_TEXTURE_2D);
                    Statement.set(GL11.GL_DEPTH_TEST);
                    Statement.set(GL11.GL_LIGHTING);
                    Statement.set(GL11.GL_POLYGON_OFFSET_FILL);

                    GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
                    GL11.glPolygonOffset(1.0f, -1100000.0f);

                    TurokGL.color(255, 255, 255, alpha);

                    event.getModelBase().render(event.getEntity(), event.getLimbSwing(), event.getLimbSwingAmount(), event.getAgeInTicks(), event.getNetHeadYaw(), event.getHeadPitch(), event.getScaleFactor());

                    GL11.glPolygonOffset(1.0f, 1100000.0f);

                    Statement.set(GL11.GL_LIGHTING);
                    Statement.set(GL11.GL_TEXTURE_2D);

                    TurokGL.popAttrib();
                    Statement.refresh();
                }

                if (lineCull) {
                    TurokGL.pushMatrix();
                    TurokGL.pushAttrib(GL11.GL_ALL_ATTRIB_BITS);

                    Statement.set(GL11.GL_TEXTURE_2D);
                    Statement.set(GL11.GL_LIGHTING);
                    Statement.set(GL11.GL_POLYGON_OFFSET_FILL);

                    GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
                    GL11.glPolygonOffset(1.0f, -1100000.0f);

                    Statement.blend();
                    Statement.scale(scale, scale, scale);
                    Statement.translate(0, offset, 0);

                    TurokGL.color(255, 255, 255, alpha);

                    event.getModelBase().render(event.getEntity(), event.getLimbSwing(), event.getLimbSwingAmount(), event.getAgeInTicks(), event.getNetHeadYaw(), event.getHeadPitch(), event.getScaleFactor());

                    GL11.glPolygonOffset(1.0f, 1100000.0f);

                    TurokGL.popAttrib();
                    TurokGL.popMatrix();

                    TurokGL.pushMatrix();
                    TurokGL.pushAttrib(GL11.GL_ALL_ATTRIB_BITS);

                    Statement.unset(GL11.GL_TEXTURE_2D);
                    Statement.unset(GL11.GL_LIGHTING);
                    Statement.set(GL11.GL_POLYGON_OFFSET_LINE);

                    Statement.set(GL11.GL_CULL_FACE);

                    GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
                    GL11.glPolygonOffset(1.0f, -1100000.0f);

                    Statement.set(GL11.GL_LINE_SMOOTH);
                    Statement.line(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST, l);

                    Statement.blend();
                    Statement.scale(scale, scale, scale);
                    Statement.translate(0, offset, 0);

                    TurokGL.color(value.getColor());

                    event.getModelBase().render(event.getEntity(), event.getLimbSwing(), event.getLimbSwingAmount(), event.getAgeInTicks(), event.getNetHeadYaw(), event.getHeadPitch(), event.getScaleFactor());

                    GL11.glPolygonOffset(1.0f, 1100000.0f);

                    Statement.set(GL11.GL_LIGHTING);
                    Statement.set(GL11.GL_TEXTURE_2D);

                    TurokGL.popAttrib();
                    TurokGL.popMatrix();
                }

                break;
            }
        }

        if (frustum && mode != Mode.SKIN && mode != Mode.OUTLINE) {
            TurokGL.pushMatrix();
            TurokGL.pushAttrib(GL11.GL_ALL_ATTRIB_BITS);

            Statement.set(GL11.GL_TEXTURE_2D);

            Statement.blend();
            Statement.scale(scale, scale, scale);
            Statement.translate(0, offset, 0);

            TurokGL.color(255, 255, 255, 255);

            event.getModelBase().render(event.getEntity(), event.getLimbSwing(), event.getLimbSwingAmount(), event.getAgeInTicks(), event.getNetHeadYaw(), event.getHeadPitch(), event.getScaleFactor());

            TurokGL.popAttrib();
            TurokGL.popMatrix();
        }
    }

    public static void renderPlayerESP(RenderModelEvent event, float s, float o, float line, int a, boolean applyOffset, boolean frustum, Mode mode, Color color) {
        float scale = s / 1000f;
        float offset = -(o / 1000f);

        int alpha = PositionUtil.collideBlockPos(EntityUtil.getFlooredEntityPosition(event.getEntity()), PlayerUtil.getBlockPos()) ? 50 : a;

        TurokGL.pushMatrix();
        TurokGL.pushAttrib(1048575);

        switch (mode) {
            case SMOOTH: {
                TurokGL.polygonMode(1028, 6913);
                TurokGL.disable(3553);
                TurokGL.disable(2896);
                TurokGL.disable(2929);
                TurokGL.enable(2848);
                TurokGL.enable(3042);

                TurokGL.enable(GL11.GL_POLYGON_OFFSET_FILL);
                TurokGL.disable(GL11.GL_TEXTURE_2D);
                TurokGL.disable(GL11.GL_LIGHTING);

                TurokGL.enable(GL11.GL_BLEND);
                TurokGL.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

                TurokGL.lineSize(line);
                TurokRenderGL.color(color);

                break;
            }

            case OUTLINE: {
                TurokGL.polygonMode(1028, 6913);
                TurokGL.disable(3553);
                TurokGL.disable(2896);
                TurokGL.disable(2929);
                TurokGL.enable(2848);
                TurokGL.enable(3042);

                TurokGL.enable(GL11.GL_POLYGON_OFFSET_FILL);
                TurokGL.disable(GL11.GL_TEXTURE_2D);
                TurokGL.disable(GL11.GL_LIGHTING);

                TurokGL.enable(GL11.GL_BLEND);
                TurokGL.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

                TurokGL.lineSize(line);
                TurokRenderGL.color(color);

                break;
            }

            case LINE: {
                TurokGL.polygonMode(1028, 6913);
                TurokGL.enable(GL11.GL_POLYGON_OFFSET_LINE);
                TurokGL.disable(GL11.GL_TEXTURE_2D);
                TurokGL.disable(GL11.GL_LIGHTING);

                TurokGL.enable(GL11.GL_BLEND);
                TurokGL.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

                TurokGL.lineSize(line);
                TurokRenderGL.color(color);

                break;
            }

            case FILL: {
                TurokGL.enable(GL11.GL_POLYGON_OFFSET_FILL);
                TurokGL.disable(GL11.GL_TEXTURE_2D);
                TurokGL.disable(GL11.GL_LIGHTING);

                TurokGL.enable(GL11.GL_BLEND);
                TurokGL.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

                TurokRenderGL.color(color);

                break;
            }

            case SKIN: {
                TurokGL.enable(GL11.GL_POLYGON_OFFSET_FILL);
                TurokGL.disable(GL11.GL_LIGHTING);

                TurokGL.enable(GL11.GL_BLEND);
                TurokGL.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

                TurokGL.color(255, 255, 255, alpha);

                break;
            }
        }

        TurokGL.translate(0, offset, 0);
        TurokGL.scale(scale, scale, scale);
        TurokGL.polygonOffset(1.0f, -1100000.0f);

        event.getModelBase().render(event.getEntity(), event.getLimbSwing(), event.getLimbSwingAmount(), event.getAgeInTicks(), event.getNetHeadYaw(), event.getHeadPitch(), event.getScaleFactor());

        // The offset.
        if (applyOffset) {
            TurokGL.polygonOffset(1.0f, 1100000.0f);
        }

        TurokGL.enable(GL11.GL_TEXTURE_2D);
        TurokGL.enable(GL11.GL_LIGHTING);
        TurokGL.enable(GL11.GL_POLYGON_OFFSET_FILL);

        TurokGL.color(255, 255, 255, 255);

        TurokGL.popAttrib();
        TurokGL.popMatrix();

        switch (mode) {
            case OUTLINE: {
                TurokGL.pushMatrix();
                TurokGL.pushAttrib(1048575);

                TurokGL.enable(GL11.GL_POLYGON_OFFSET_FILL);
                TurokGL.disable(GL11.GL_LIGHTING);

                TurokGL.enable(GL11.GL_BLEND);
                TurokGL.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

                TurokGL.color(255, 255, 255, alpha);

                TurokGL.translate(0, offset, 0);
                TurokGL.scale(scale, scale, scale);
                TurokGL.polygonOffset(1.0f, -1100000.0f);

                event.getModelBase().render(event.getEntity(), event.getLimbSwing(), event.getLimbSwingAmount(), event.getAgeInTicks(), event.getNetHeadYaw(), event.getHeadPitch(), event.getScaleFactor());

                // The offset.
                if (applyOffset) {
                    TurokGL.polygonOffset(1.0f, 1100000.0f);
                }

                TurokGL.enable(GL11.GL_TEXTURE_2D);
                TurokGL.enable(GL11.GL_LIGHTING);
                TurokGL.enable(GL11.GL_POLYGON_OFFSET_FILL);

                TurokGL.color(255, 255, 255, alpha);

                TurokGL.popAttrib();
                TurokGL.popMatrix();

                break;
            }
        }

        if (frustum && mode != Mode.OUTLINE && mode != Mode.SKIN) {
            TurokGL.pushMatrix();
            TurokGL.translate(0, offset, 0);
            TurokGL.scale(scale, scale, scale);

            // We render the player model!
            event.getModelBase().render(event.getEntity(), event.getLimbSwing(), event.getLimbSwingAmount(), event.getAgeInTicks(), event.getNetHeadYaw(), event.getHeadPitch(), event.getScaleFactor());

            TurokGL.popMatrix();
        }
    }

    public static void entityESP(RenderModelEvent event, float s, float o, float a, float l, boolean lineCull, boolean frustum, ValueColor value, Mode mode) {
        float scale = s / 1000f;
        float offset = -(o / 1000f);

        float alpha = a;

        switch (mode) {
            case FILL: {
                TurokGL.pushMatrix();
                TurokGL.pushAttrib(GL11.GL_ALL_ATTRIB_BITS);

                Statement.unset(GL11.GL_LIGHTING);
                Statement.unset(GL11.GL_TEXTURE_2D);
                Statement.unset(GL11.GL_DEPTH_TEST);

                Statement.blend();
                Statement.scale(scale, scale, scale);
                Statement.translate(0, offset, 0);

                TurokGL.color(value.getColor());

                event.getModelBase().render(event.getEntity(), event.getLimbSwing(), event.getLimbSwingAmount(), event.getAgeInTicks(), event.getNetHeadYaw(), event.getHeadPitch(), event.getScaleFactor());

                TurokGL.color(Color.WHITE);

                Statement.set(GL11.GL_LIGHTING);
                Statement.set(GL11.GL_TEXTURE_2D);
                Statement.set(GL11.GL_DEPTH_TEST);

                TurokGL.popAttrib();
                TurokGL.popMatrix();

                break;
            }

            case SKIN: {
                TurokGL.pushMatrix();
                TurokGL.pushAttrib(GL11.GL_ALL_ATTRIB_BITS);

                Statement.set(GL11.GL_TEXTURE_2D);
                Statement.set(GL11.GL_POLYGON_OFFSET_FILL);

                GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
                GL11.glPolygonOffset(1.0f, -1100000.0f);

                Statement.blend();
                Statement.scale(scale, scale, scale);
                Statement.translate(0, offset, 0);

                TurokGL.color(value.getColor());

                event.getModelBase().render(event.getEntity(), event.getLimbSwing(), event.getLimbSwingAmount(), event.getAgeInTicks(), event.getNetHeadYaw(), event.getHeadPitch(), event.getScaleFactor());

                GL11.glPolygonOffset(1.0f, 1100000.0f);

                TurokGL.popAttrib();
                TurokGL.popMatrix();

                break;
            }

            case SMOOTH: {
                TurokGL.pushMatrix();
                TurokGL.pushAttrib(GL11.GL_ALL_ATTRIB_BITS);

                Statement.blend();
                Statement.scale(scale, scale, scale);
                Statement.translate(0, offset, 0);

                Statement.unset(GL11.GL_LIGHTING);
                Statement.unset(GL11.GL_TEXTURE_2D);

                Statement.set(GL11.GL_CULL_FACE);
                Statement.set(GL11.GL_POLYGON_OFFSET_FILL);

                GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
                GL11.glPolygonOffset(1.0f, -1100000.0f);

                Statement.blend();
                Statement.scale(scale, scale, scale);
                Statement.translate(0, offset, 0);

                TurokGL.color(value.getColor());

                event.getModelBase().render(event.getEntity(), event.getLimbSwing(), event.getLimbSwingAmount(), event.getAgeInTicks(), event.getNetHeadYaw(), event.getHeadPitch(), event.getScaleFactor());

                GL11.glPolygonOffset(1.0f, 1100000.0f);

                TurokGL.color(Color.WHITE);

                Statement.set(GL11.GL_LIGHTING);
                Statement.set(GL11.GL_TEXTURE_2D);

                Statement.set(GL11.GL_CULL_FACE);

                TurokGL.popAttrib();
                TurokGL.popMatrix();

                TurokGL.pushMatrix();
                TurokGL.pushAttrib(GL11.GL_ALL_ATTRIB_BITS);

                Statement.unset(GL11.GL_TEXTURE_2D);
                Statement.set(GL11.GL_POLYGON_OFFSET_LINE);

                if (lineCull) {
                    Statement.set(GL11.GL_CULL_FACE);
                }

                Statement.blend();
                Statement.scale(scale, scale, scale);
                Statement.translate(0, offset, 0);

                GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
                GL11.glPolygonOffset(1.0f, -1100000.0f);

                Statement.set(GL11.GL_LINE_SMOOTH);
                Statement.line(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST, l);

                TurokGL.color(value.getColor((int) alpha));

                event.getModelBase().render(event.getEntity(), event.getLimbSwing(), event.getLimbSwingAmount(), event.getAgeInTicks(), event.getNetHeadYaw(), event.getHeadPitch(), event.getScaleFactor());

                GL11.glPolygonOffset(1.0f, 1100000.0f);

                if (lineCull) {
                    Statement.unset(GL11.GL_CULL_FACE);
                }

                TurokGL.popAttrib();
                TurokGL.popMatrix();
            }

            case LINE: {
                TurokGL.pushMatrix();
                TurokGL.pushAttrib(GL11.GL_ALL_ATTRIB_BITS);

                Statement.unset(GL11.GL_TEXTURE_2D);
                Statement.unset(GL11.GL_LIGHTING);
                Statement.set(GL11.GL_POLYGON_OFFSET_LINE);

                if (lineCull) {
                    Statement.set(GL11.GL_CULL_FACE);
                }

                GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
                GL11.glPolygonOffset(1.0f, -1100000.0f);

                Statement.set(GL11.GL_LINE_SMOOTH);
                Statement.line(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST, l);

                Statement.blend();
                Statement.scale(scale, scale, scale);
                Statement.translate(0, offset, 0);

                TurokGL.color(value.getColor());

                event.getModelBase().render(event.getEntity(), event.getLimbSwing(), event.getLimbSwingAmount(), event.getAgeInTicks(), event.getNetHeadYaw(), event.getHeadPitch(), event.getScaleFactor());

                GL11.glPolygonOffset(1.0f, 1100000.0f);

                if (lineCull) {
                    Statement.unset(GL11.GL_CULL_FACE);
                }

                Statement.set(GL11.GL_LIGHTING);
                Statement.set(GL11.GL_TEXTURE_2D);

                TurokGL.popAttrib();
                TurokGL.popMatrix();

                break;
            }

            case OUTLINE: {
                if (!lineCull) {
                    TurokGL.pushMatrix();
                    TurokGL.pushAttrib(GL11.GL_ALL_ATTRIB_BITS);

                    Statement.unset(GL11.GL_DEPTH_TEST);
                    Statement.set(GL11.GL_LINE_SMOOTH);
                    Statement.unset(GL11.GL_TEXTURE_2D);
                    Statement.unset(GL11.GL_LIGHTING);

                    Statement.set(GL11.GL_POLYGON_OFFSET_LINE);

                    GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
                    GL11.glPolygonOffset(1.0f, -1100000.0f);

                    Statement.line(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST, l);

                    Statement.blend();
                    Statement.scale(scale, scale, scale);
                    Statement.translate(0, offset, 0);

                    TurokGL.color(value.getColor());

                    event.getModelBase().render(event.getEntity(), event.getLimbSwing(), event.getLimbSwingAmount(), event.getAgeInTicks(), event.getNetHeadYaw(), event.getHeadPitch(), event.getScaleFactor());

                    GL11.glPolygonOffset(1.0f, 1100000.0f);

                    TurokGL.enable(GL11.GL_POLYGON_OFFSET_FILL);

                    Statement.set(GL11.GL_LIGHTING);
                    Statement.set(GL11.GL_TEXTURE_2D);

                    Statement.set(GL11.GL_TEXTURE_2D);
                    Statement.set(GL11.GL_DEPTH_TEST);
                    Statement.set(GL11.GL_LIGHTING);
                    Statement.set(GL11.GL_POLYGON_OFFSET_FILL);

                    GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
                    GL11.glPolygonOffset(1.0f, -1100000.0f);

                    Statement.blend();
                    Statement.scale(scale, scale, scale);
                    Statement.translate(0, offset, 0);

                    TurokGL.color(255, 255, 255, alpha);

                    event.getModelBase().render(event.getEntity(), event.getLimbSwing(), event.getLimbSwingAmount(), event.getAgeInTicks(), event.getNetHeadYaw(), event.getHeadPitch(), event.getScaleFactor());

                    GL11.glPolygonOffset(1.0f, 1100000.0f);

                    Statement.set(GL11.GL_LIGHTING);
                    Statement.set(GL11.GL_TEXTURE_2D);

                    TurokGL.popAttrib();
                    Statement.refresh();
                }

                if (lineCull) {
                    TurokGL.pushMatrix();
                    TurokGL.pushAttrib(GL11.GL_ALL_ATTRIB_BITS);

                    Statement.set(GL11.GL_TEXTURE_2D);
                    Statement.set(GL11.GL_LIGHTING);
                    Statement.set(GL11.GL_POLYGON_OFFSET_FILL);

                    GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
                    GL11.glPolygonOffset(1.0f, -1100000.0f);

                    Statement.blend();
                    Statement.scale(scale, scale, scale);
                    Statement.translate(0, offset, 0);

                    TurokGL.color(255, 255, 255, alpha);

                    event.getModelBase().render(event.getEntity(), event.getLimbSwing(), event.getLimbSwingAmount(), event.getAgeInTicks(), event.getNetHeadYaw(), event.getHeadPitch(), event.getScaleFactor());

                    GL11.glPolygonOffset(1.0f, 1100000.0f);

                    TurokGL.popAttrib();
                    TurokGL.popMatrix();

                    TurokGL.pushMatrix();
                    TurokGL.pushAttrib(GL11.GL_ALL_ATTRIB_BITS);

                    Statement.unset(GL11.GL_TEXTURE_2D);
                    Statement.unset(GL11.GL_LIGHTING);
                    Statement.set(GL11.GL_POLYGON_OFFSET_LINE);

                    Statement.set(GL11.GL_CULL_FACE);

                    GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
                    GL11.glPolygonOffset(1.0f, -1100000.0f);

                    Statement.set(GL11.GL_LINE_SMOOTH);
                    Statement.line(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST, l);

                    Statement.blend();
                    Statement.scale(scale, scale, scale);
                    Statement.translate(0, offset, 0);

                    TurokGL.color(value.getColor());

                    event.getModelBase().render(event.getEntity(), event.getLimbSwing(), event.getLimbSwingAmount(), event.getAgeInTicks(), event.getNetHeadYaw(), event.getHeadPitch(), event.getScaleFactor());

                    GL11.glPolygonOffset(1.0f, 1100000.0f);

                    Statement.set(GL11.GL_LIGHTING);
                    Statement.set(GL11.GL_TEXTURE_2D);

                    TurokGL.popAttrib();
                    TurokGL.popMatrix();
                }

                break;
            }
        }

        if (frustum && mode != Mode.SKIN && mode != Mode.OUTLINE) {
            TurokGL.pushMatrix();
            TurokGL.pushAttrib(GL11.GL_ALL_ATTRIB_BITS);

            Statement.set(GL11.GL_TEXTURE_2D);

            Statement.blend();
            Statement.scale(scale, scale, scale);
            Statement.translate(0, offset, 0);

            TurokGL.color(255, 255, 255, 255);

            event.getModelBase().render(event.getEntity(), event.getLimbSwing(), event.getLimbSwingAmount(), event.getAgeInTicks(), event.getNetHeadYaw(), event.getHeadPitch(), event.getScaleFactor());

            TurokGL.popAttrib();
            TurokGL.popMatrix();
        }
    }
}
