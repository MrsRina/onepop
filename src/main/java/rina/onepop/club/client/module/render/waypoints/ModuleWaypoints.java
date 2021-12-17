package rina.onepop.club.client.module.render.waypoints;

import me.rina.turok.render.font.management.TurokFontManager;
import me.rina.turok.render.opengl.TurokGL;
import me.rina.turok.render.opengl.TurokRenderGL;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.opengl.GL11;
import rina.onepop.club.Onepop;
import rina.onepop.club.api.ISLClass;
import rina.onepop.club.api.event.impl.EventStage;
import rina.onepop.club.api.module.Module;
import rina.onepop.club.api.module.impl.ModuleCategory;
import rina.onepop.club.api.module.registry.Registry;
import rina.onepop.club.api.setting.value.*;
import rina.onepop.club.api.util.client.NullUtil;
import rina.onepop.club.api.util.entity.EntityUtil;
import rina.onepop.club.client.event.client.RunTickEvent;
import rina.onepop.club.client.event.network.StateServerPlayerEvent;
import rina.onepop.club.client.module.render.waypoints.impl.Render;
import rina.onepop.club.client.module.render.waypoints.impl.Waypoint;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @author SrRina
 * @since 13/07/2021 at 01:27
 **/
@Registry(name = "Waypoints", tag = "Waypoints", description = "Show log player position.", category = ModuleCategory.RENDER)
public class ModuleWaypoints extends Module {
    // Misc.
    public static ValueBoolean settingChunkReset = new ValueBoolean("Chunk Reset", "ChunkReset", "Reset by chunk.", true);
    public static ValueBoolean settingSync = new ValueBoolean("Sync", "Sync", "Sync the last disconnect point.", true);
    public static ValueColor settingPlayer = new ValueColor("Player", "Player", "Set color and enable.", new Color(255, 0, 255, 100));
    public static ValueNumber settingMinutes = new ValueNumber("Minutes",  "Minutes", "Minutes for a waypoint stay alive.", 3, 1, 10);
    public static ValueColor settingBackground = new ValueColor("Background", "Background", "Sets background color.", new Color(0, 0, 0, 100));
    public static ValueNumber settingOffsetY = new ValueNumber("Offset Y", "OffsetY", "Sets offset y.", 100, 0, 100);
    public static ValueNumber settingScale = new ValueNumber("Scale", "Scale", "The scale of render.", 25, 1, 1000);
    public static ValueString settingTag = new ValueString("Tag", "Tag", "Tags display.", "<player> disconnected at <pos>");
    public static ValueEnum settingMode = new ValueEnum("Mode", "Mode", "Render mode.", Render.OUTLINE);
    public static ValueBind settingCleanUp = new ValueBind("Clean Up", "CleanUp", "Forces a clean.",  -1);

    private final Map<String, Waypoint> waypointMap = new HashMap<>();
    private final Map<String, EntityPlayer> concurrentPlayerMap = new HashMap<>();

    private float scaled;

    @Override
    public void onSetting() {
        if (settingCleanUp.getValue()) {
            this.waypointMap.clear();

            settingCleanUp.setValue(false);
        }
    }

    @Override
    public void onRender3D() {
        if (NullUtil.isPlayerWorld()) {
            return;
        }

        switch ((Render) settingMode.getValue()) {
            case OUTLINE: {
                for (Map.Entry<String, Waypoint> map : this.waypointMap.entrySet()) {
                    this.doRenderNameTag(map.getValue());
                }

                break;
            }

            case AABB: {
                for (Map.Entry<String, Waypoint> map : this.waypointMap.entrySet()) {
                    this.doRenderNameTag(map.getValue());
                }

                break;
            }

            case TAG: {
                for (Map.Entry<String, Waypoint> map : this.waypointMap.entrySet()) {
                    this.doRenderNameTag(map.getValue());
                }

                break;
            }
        }
    }

    @Listener
    public void onStatePlayer(StateServerPlayerEvent event) {
        if (NullUtil.isPlayerWorld() || mc.getConnection() == null) {
            return;
        }

        final NetworkPlayerInfo info = event.getPlayer();

        if (info == null || info.getDisplayName() == null) {
            return;
        }

        final String name = info.getDisplayName().getFormattedText();

        if (event.getStage() == EventStage.PRE && settingSync.getValue() && this.waypointMap.containsKey(name)) {
            this.waypointMap.remove(name);

            return;
        }

        if (event.getStage() == EventStage.POST && this.concurrentPlayerMap.containsKey(name)) {
            final EntityPlayer entity = this.concurrentPlayerMap.get(name);
            final BlockPos position = EntityUtil.getFlooredEntityPosition(entity);

            final Waypoint waypoint = new Waypoint(position,  "Loading....");

            this.waypointMap.put(name, waypoint);
        }
    }

    @Listener
    public void onTick(RunTickEvent event) {
        if (NullUtil.isPlayerWorld()) {
            this.waypointMap.clear();
            this.concurrentPlayerMap.clear();

            return;
        }

        for (EntityPlayer entities : mc.world.playerEntities) {
            if (!this.concurrentPlayerMap.containsKey(entities.getName())) {
                this.concurrentPlayerMap.put(entities.getName(), entities);
            }
        }
    }

    public void doRenderNameTag(final Waypoint waypoint) {
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        GlStateManager.disableDepth();
        GlStateManager.glLineWidth(1f);

        if (mc.getRenderManager().options == null) {
            return;
        }

        double x = waypoint.getPosition().x;
        double y = waypoint.getPosition().y;
        double z = waypoint.getPosition().z;

        float playerViewX = ISLClass.mc.getRenderManager().playerViewX;
        float playerViewY = ISLClass.mc.getRenderManager().playerViewY;

        boolean flag = ISLClass.mc.getRenderManager().options.thirdPersonView == 2;

        double height = (mc.player.height + (settingOffsetY.getValue().intValue() / 100d));

        double referencedX = x - ISLClass.mc.getRenderManager().renderPosX;
        double referencedY = (y + height) - ISLClass.mc.getRenderManager().renderPosY;
        double referencedZ = z - ISLClass.mc.getRenderManager().renderPosZ;

        GlStateManager.pushMatrix();
        GlStateManager.translate(referencedX, referencedY, referencedZ);

        // Rotate for name tag.
        GlStateManager.rotate(-playerViewY, 0f, 1f, 0f);
        GlStateManager.rotate((flag ? -1f : 1f) * playerViewX, 1f, 0f, 0f);

        this.scaled = settingScale.getValue().intValue() / 1000f;

        // Scale.
        GlStateManager.scale(this.scaled, this.scaled, this.scaled);
        GlStateManager.scale(-0.025f, -0.025f, 0.025f);

        RenderHelper.enableStandardItemLighting();
        GlStateManager.enablePolygonOffset();
        GlStateManager.doPolygonOffset(1.0f, -1500000.0f);
        GlStateManager.disableLighting();

        GlStateManager.disableDepth();
        GlStateManager.enableBlend();

        int diff = TurokFontManager.getStringWidth(Onepop.getWrapper().fontNameTags, waypoint.getTag());

        GlStateManager.enableTexture2D();

        TurokFontManager.render(Onepop.getWrapper().fontNameTags, settingTag.getValue().replaceAll("<player>", waypoint.getTag()).replaceAll("<pos>", this.getFormattedPosition(waypoint.getPosition())), -(diff / 2), -10, true, settingPlayer.getColor());

        GlStateManager.disableTexture2D();

        TurokGL.pushMatrix();
        TurokRenderGL.color(settingBackground.getColor());
        TurokRenderGL.drawRoundedRect(-((diff + 2f) / 2f) , -1, diff + 2, 10, 2f);
        TurokGL.popMatrix();

        // Release.
        GlStateManager.disableTexture2D();
        GlStateManager.enableDepth();
        GlStateManager.disableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.disablePolygonOffset();
        GlStateManager.doPolygonOffset(1.0f, 1500000.0f);
        GlStateManager.popMatrix();

        GlStateManager.glLineWidth(1f);
        GlStateManager.shadeModel(GL11.GL_FLAT);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
        GlStateManager.enableDepth();
        GlStateManager.enableCull();
        GlStateManager.enableCull();
        GlStateManager.depthMask(true);
        GlStateManager.enableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.enableDepth();
    }

    public void doRenderAABB(final Waypoint waypoint) {

    }

    public String getFormattedPosition(final BlockPos position) {
        return "<" + position.x + ", " + position.y + ", " + position.z + ">";
    }
}
