package rina.onepop.club.client.module.render;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.rina.turok.render.font.management.TurokFontManager;
import me.rina.turok.render.opengl.TurokRenderGL;
import me.rina.turok.util.TurokMath;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;
import rina.onepop.club.Onepop;
import rina.onepop.club.api.ISLClass;
import rina.onepop.club.api.engine.opengl.Statement;
import rina.onepop.club.api.event.impl.EventStage;
import rina.onepop.club.api.module.Module;
import rina.onepop.club.api.module.impl.ModuleCategory;
import rina.onepop.club.api.module.registry.Registry;
import rina.onepop.club.api.setting.value.ValueBoolean;
import rina.onepop.club.api.setting.value.ValueColor;
import rina.onepop.club.api.setting.value.ValueNumber;
import rina.onepop.club.api.social.Social;
import rina.onepop.club.api.social.management.SocialManager;
import rina.onepop.club.api.social.type.SocialType;
import rina.onepop.club.api.util.client.NullUtil;
import rina.onepop.club.api.util.entity.EntityUtil;
import rina.onepop.club.api.util.network.ServerUtil;
import rina.onepop.club.client.event.client.RunTickEvent;
import rina.onepop.club.client.event.render.RenderNameEvent;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author SrRina
 * @since 07/02/2021 at 15:47
 **/
@Registry(name = "Name Tags", tag = "NameTags", description = "Better name tag.", category = ModuleCategory.RENDER)
public class ModuleNameTags extends Module {
    /* Player stuff. */
    public static ValueBoolean settingFriend = new ValueBoolean("Friend", "Friend", "Allows render friends name tag.", true);
    public static ValueBoolean settingEnemy = new ValueBoolean("Enemy", "Enemy", "Allows render enemies name tag.", false);
    public static ValueBoolean settingPing = new ValueBoolean("Ping", "Ping", "Show ping player.", true);
    public static ValueBoolean settingName = new ValueBoolean("Name", "Name", "Draws name.", true);
    public static ValueBoolean settingHealth = new ValueBoolean("Health", "Health", "Draws health!", true);
    public static ValueBoolean settingMainHand = new ValueBoolean("Main Hand", "MainHand", "Render item main hand.", true);
    public static ValueBoolean settingOffhand = new ValueBoolean("Offhand", "Offhand", "Render item offhand.", true);
    public static ValueBoolean settingArmor = new ValueBoolean("Armor", "Armor", "Render armor!", true);

    /* Background setting. */
    public static ValueColor settingBackgroundTextColor = new ValueColor("Bg. Text Color", "BackgroundTextColor", "Sets color.", new Color(0, 0, 0, 100));
    public static ValueColor settingBackgroundItemColor = new ValueColor("Bg. Item Color", "BackgroundItemColor", "Sets color.", new Color(0, 0, 0, 100));

    /* Fonts setting. */
    public static ValueBoolean settingShadow = new ValueBoolean("Shadow", "Shadow", "String shadow.", true);
    public static ValueBoolean settingCustomFont = new ValueBoolean("Custom Font", "CustomFont", "Set custom font to render.", true);

    /* Misc settings. */
    public static ValueBoolean settingSmartScale = new ValueBoolean("Smart Scale", "SmartScale", "Automatically scale if you are close of entity.", true);
    public static ValueNumber settingScale = new ValueNumber("Scale", "Scale", "The scale of render.", 25, 1, 1000);
    public static ValueNumber settingOffsetY = new ValueNumber("Offset Y", "OffsetY", "Offset y to render.", 10, 0, 100);
    public static ValueNumber settingRange = new ValueNumber("Range", "Range", "Distance to capture players.", 200, 0, 200);

    private HashMap<String, ItemStack> itemStackMap = new HashMap<>();
    private ArrayList<EntityPlayer> entityToDraw = new ArrayList<>();

    private int positionTagX;
    private int diffX;

    private float scaled;

    int CLEAR = 256;
    int MASK = 2929;

    @Override
    public void onSetting() {
        // For this release 2.0, custom fonts are disabled!
        Onepop.getWrapper().fontNameTags.setRenderingCustomFont(false);
    }

    @Override
    public void onRender3D() {
        if (NullUtil.isPlayerWorld()) {
            return;
        }

        mc.profiler.startSection("onepop-nametags");

        Statement.matrix();
        Statement.unset(GL11.GL_TEXTURE_2D);
        Statement.blend();
        Statement.setShaderModel(GL11.GL_SMOOTH);
        Statement.set(GL11.GL_DEPTH_TEST);

        Statement.setDepthMask();

        float partialTicks = Onepop.getClientEventManager().getCurrentRender3DPartialTicks();

        for (EntityPlayer entities : this.entityToDraw) {
            float x = (float) TurokMath.lerp(entities.prevPosX, entities.posX, partialTicks);
            float y = (float) TurokMath.lerp(entities.prevPosY, entities.posY, partialTicks);
            float z = (float) TurokMath.lerp(entities.prevPosZ, entities.posZ, partialTicks);

            this.doNameTags(entities, x, y, z, partialTicks);
        }

        Statement.setShaderModel(GL11.GL_FLAT);
        Statement.set(GL11.GL_DEPTH_TEST);
        Statement.set(GL11.GL_TEXTURE_2D);
        Statement.setDepthMask();
        Statement.refresh();

        mc.profiler.endSection();
    }

    @Listener
    public void onListenRenderNameEvent(RenderNameEvent event) {
        if (event.getStage() == EventStage.PRE) {
            event.setCanceled(true);
        }
    }

    @Listener
    public void onTick(RunTickEvent event) {
        if (NullUtil.isPlayerWorld()) {
            return;
        }

        this.entityToDraw.clear();

        for (EntityPlayer entities : ISLClass.mc.world.playerEntities) {
            if (entities == null || entities == mc.player) {
                continue;
            }

            if (entities.isDead || entities.getHealth() < 0) {
                continue;
            }

            if (ISLClass.mc.player.getDistance(entities) >= settingRange.getValue().intValue()) {
                continue;
            }

            if (!this.doAccept(entities)) {
                continue;
            }

            this.entityToDraw.add(entities);
        }
    }

    public void doNameTags(EntityPlayer entity, double x, double y, double z, float partialTicks) {
        if (mc.getRenderManager().options == null) {
            return;
        }

        this.itemStackMap.clear();

        if (settingMainHand.getValue()) {
            this.itemStackMap.put("a", entity.getHeldItemMainhand());
        }

        if (settingArmor.getValue()) {
            this.itemStackMap.put("b", entity.inventory.armorInventory.get(0));
            this.itemStackMap.put("c", entity.inventory.armorInventory.get(1));
            this.itemStackMap.put("d", entity.inventory.armorInventory.get(2));
            this.itemStackMap.put("e", entity.inventory.armorInventory.get(3));
        }

        if (settingOffhand.getValue()) {
            this.itemStackMap.put("f", entity.getHeldItemOffhand());
        }

        float playerViewX = ISLClass.mc.getRenderManager().playerViewX;
        float playerViewY = ISLClass.mc.getRenderManager().playerViewY;

        boolean flag = ISLClass.mc.getRenderManager().options.thirdPersonView == 2;

        double height = (entity.height + (settingOffsetY.getValue().intValue() / 100d) - (entity.isSneaking() ? 0.25f : 0f));

        double referencedX = x - ISLClass.mc.getRenderManager().renderPosX;
        double referencedY = (y + height) - ISLClass.mc.getRenderManager().renderPosY;
        double referencedZ = z - ISLClass.mc.getRenderManager().renderPosZ;

        /*
         * Current scale of entity.
         */
        this.doScale(entity);

        Statement.matrix();
        Statement.translate(referencedX, referencedY, referencedZ);

        // Rotate for name tag.
        Statement.rotate(-playerViewY, 0f, 1f, 0f);
        Statement.rotate((flag ? -1f : 1f) * playerViewX, 1f, 0f, 0f);

        // Scale.
        Statement.scale(this.scaled, this.scaled, this.scaled);
        Statement.scale(-0.025f, -0.025f, 0.025f);

        RenderHelper.enableStandardItemLighting();
        Statement.unset(GL11.GL_LIGHTING);

        int diff = 0;

        Color color = EntityUtil.getColor(entity, new Color(190, 190, 190));

        this.doDrawText(entity, color);

        Statement.unset(GL11.GL_TEXTURE_2D);

        for (Map.Entry<String, ItemStack> entry : this.itemStackMap.entrySet()) {
            if (entry.getValue() == null || entry.getValue().getItem() == Items.AIR) {
                continue;
            }

            diff += 16;
        }

        int positionItems = -(diff / 2);

        TurokRenderGL.color(settingBackgroundItemColor.getColor());
        TurokRenderGL.drawRoundedRect(-((diff + 2) / 2), -20, diff + 2, 17, 2f);

        Statement.set(GL11.GL_TEXTURE_2D);
        RenderHelper.enableGUIStandardItemLighting();

        for (Map.Entry<String, ItemStack> entry : this.itemStackMap.entrySet()) {
            if (entry.getValue() == null || entry.getValue().getItem() == Items.AIR) {
                continue;
            }

            this.doRenderItem(entry.getValue(), positionItems, -16);

            positionItems += 16;
        }

        RenderHelper.disableStandardItemLighting();

        // Release.
        Statement.refresh();
    }

    public void doDrawText(EntityPlayer entity, Color color) {
        String ping = "";
        String name = "";
        String health = "";

        int diff = 0;

        if (settingPing.getValue()) {
            final NetworkPlayerInfo playerInfo = mc.player.connection.getPlayerInfo(entity.getUniqueID());

            ping  = "[" + (playerInfo != null ? "" + ServerUtil.getPing(playerInfo) + "ms" : "nNms") + "] ";
        }

        if (settingName.getValue()) {
            name = entity.getName() + " ";
        }

        if (settingHealth.getValue()) {
            health = "" + ((int) entity.getHealth() + (int) entity.getAbsorptionAmount());
        }

        diff = TurokFontManager.getStringWidth(Onepop.getWrapper().fontNameTags, (ping + name + health));

        Statement.unset(GL11.GL_TEXTURE_2D);
        Statement.unset(GL11.GL_DEPTH_TEST);
        GlStateManager.disableAlpha();

        TurokRenderGL.color(settingBackgroundTextColor.getColor());
        TurokRenderGL.drawRoundedRect(-((diff + 2) / 2), -1, diff + 2, 10, 2f);

        Statement.set(GL11.GL_TEXTURE_2D);

        int x = -(diff / 2);

        if (settingPing.getValue()) {
            final NetworkPlayerInfo playerInfo = mc.player.connection.getPlayerInfo(entity.getUniqueID());

            String cache = ChatFormatting.GRAY + "[" + ChatFormatting.RESET + "" + (playerInfo != null ? "" + ServerUtil.getPing(playerInfo) + "ms" : "nNms") + ChatFormatting.GRAY + "] ";

            int ms = playerInfo != null ? ServerUtil.getPing(playerInfo) : 0;

            Color colorPing;

            if (ms >= 100) {
                colorPing = new Color(255, TurokMath.clamp(255 - (int) TurokMath.distancingValues(ms, 150, 255), 0, 255), 0);
            } else {
                colorPing = new Color(TurokMath.clamp((int) TurokMath.distancingValues(ms, 100, 255), 0, 255), 255, 0);
            }

            TurokFontManager.render(Onepop.getWrapper().fontNameTags, cache, x, 0, settingShadow.getValue(), colorPing);

            x += TurokFontManager.getStringWidth(Onepop.getWrapper().fontNameTags, ping);
        }

        if (settingName.getValue()) {
            TurokFontManager.render(Onepop.getWrapper().fontNameTags, name, x, 0, settingShadow.getValue(), color);

            x += TurokFontManager.getStringWidth(Onepop.getWrapper().fontNameTags, name);
        }

        if (settingHealth.getValue()) {
            TurokFontManager.render(Onepop.getWrapper().fontNameTags, health, x, 0, settingShadow.getValue(), new Color(255, TurokMath.clamp((int) TurokMath.distancingValues(entity.getHealth() + entity.getAbsorptionAmount(), 36, 255), 0, 255), 0));

            x += TurokFontManager.getStringWidth(Onepop.getWrapper().fontNameTags, health);
        }
    }

    public void doRenderItem(ItemStack item, int x, int y) {
        Statement.matrix();
        Statement.setDepthMask();

        GlStateManager.clear(CLEAR);

        GlStateManager.disableDepth();
        GlStateManager.enableDepth();

        RenderHelper.enableStandardItemLighting();

        mc.getRenderItem().zLevel = -200.0f;

        Statement.scale(1, 1, 0.01f);

        mc.getRenderItem().renderItemAndEffectIntoGUI(item, x, (y / 2) - 12);
        mc.getRenderItem().renderItemOverlays(mc.fontRenderer, item, x, (y / 2) - 12  + 2);
        mc.getRenderItem().zLevel = 0.0f;

        Statement.scale(1, 1, 1);

        RenderHelper.disableStandardItemLighting();

        GlStateManager.enableAlpha();
        GlStateManager.disableBlend();
        GlStateManager.disableLighting();
        GlStateManager.scale(0.5d, 0.5d, 0.5d);
        GlStateManager.disableDepth();
        GlStateManager.enableDepth();
        GlStateManager.scale(2.0f, 2.0f, 2.0f);
        GL11.glPopMatrix();
    }

    public void doScale(EntityLivingBase entity) {
        float distance = ISLClass.mc.player.getDistance(entity);
        float scaling = (float) ((distance / 8f) * Math.pow(1.2589254f, settingScale.getValue().intValue() / 100f));

        boolean flag = distance <= 8.0f;
        
        if (!settingSmartScale.getValue()) {
            this.scaled = settingScale.getValue().intValue() / 100f;
        } else {
            this.scaled = flag ? (150 / 100f) : scaling;
        }
    }

    public boolean doAccept(EntityPlayer entity) {
        boolean isAccepted = false;

        Social social = SocialManager.get(entity.getName());

        if (social != null) {
            if (social.getType() == SocialType.FRIEND && settingFriend.getValue()) {
                isAccepted = true;
            }

            if (social.getType() == SocialType.ENEMY && settingEnemy.getValue()) {
                isAccepted = true;
            }
        } else {
            isAccepted = true;
        }

        return isAccepted;
    }
}