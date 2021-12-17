package rina.onepop.club.client.module.render;

import com.mojang.authlib.GameProfile;
import me.rina.turok.util.TurokMath;
import me.rina.turok.util.TurokTick;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.SPacketEntityStatus;
import org.lwjgl.opengl.GL11;
import rina.onepop.club.Onepop;
import rina.onepop.club.api.engine.opengl.Statement;
import rina.onepop.club.api.module.Module;
import rina.onepop.club.api.module.impl.ModuleCategory;
import rina.onepop.club.api.module.registry.Registry;
import rina.onepop.club.api.setting.value.ValueBoolean;
import rina.onepop.club.api.setting.value.ValueColor;
import rina.onepop.club.api.setting.value.ValueNumber;
import rina.onepop.club.api.util.client.NullUtil;
import rina.onepop.club.client.event.client.RunTickEvent;
import rina.onepop.club.client.event.network.PacketEvent;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author SrRina
 * @since 02/08/2021 at 18:17
 *
 * 0 - Walking.
 * 1 - Pop.
 * 2 - Health.
 **/
@Registry(name = "Backtrack", tag = "Backtrack", description = "Interpolate the player movements & render them.", category = ModuleCategory.RENDER)
public class ModuleBacktrack extends Module {
    public static class Clone {
        private final EntityPlayer player;
        private final TurokTick timer = new TurokTick();

        private final String tag;
        private int stage;
        private int type;

        private float alphaOne = 255;
        private float alphaTwo = 255;

        private float alphaThree = 255f;
        private float alphaFour = 255f;

        private EntityPlayer cloneOne;
        private EntityPlayer cloneTwo;

        private EntityPlayer cloneThree;
        private EntityPlayer cloneFour;

        private boolean finished;

        public Clone(EntityPlayer player) {
            this.tag = player.getName();
            this.player = player;

            this.timer.reset();
            this.stage = 0;
        }

        public void setType(int type) {
            this.type = type;
        }

        public int getType() {
            return type;
        }

        public EntityPlayer getPlayer() {
            return player;
        }

        public String getTag() {
            return tag;
        }

        public boolean isFinished() {
            return finished;
        }

        public boolean isAlphaFinished() {
            if (this.stage == 2 && this.alphaOne <= 10) {
                return true;
            } else if (this.stage == 3 && this.alphaTwo <= 10) {
                return true;
            } else if (this.stage == 4 && this.alphaThree <= 10) {
                return true;
            } else if (this.stage == 5 && this.alphaFour <= 10) {
                return true;
            }

            return false;
        }

        public void updateAll() {
            if (this.stage == 0) {
                this.timer.reset();
                this.stage = 1;
            }

            if (this.timer.isPassedMS(ModuleBacktrack.settingDelay.getValue().intValue())) {
                if (this.stage == 4 && ModuleBacktrack.settingLimit.getValue().intValue() >= 4) {
                    this.cloneFour = this.addClone();
                    this.stage = 5;
                }

                if (this.stage == 3 && ModuleBacktrack.settingLimit.getValue().intValue() >= 3) {
                    this.cloneThree = this.addClone();
                    this.stage = 4;
                }

                if (this.stage == 2 && ModuleBacktrack.settingLimit.getValue().intValue() >= 2) {
                    this.cloneTwo = this.addClone();
                    this.stage = 3;
                }

                if (this.stage == 1 && ModuleBacktrack.settingLimit.getValue().intValue() >= 1) {
                    this.cloneOne = this.addClone();
                    this.stage = 2;
                }

                this.timer.reset();
            }

            this.finished = this.stage > ModuleBacktrack.settingLimit.getValue().intValue() && this.isAlphaFinished();
        }

        public ValueColor getColor() {
            switch (type) {
                case 0: {
                    return settingWalking;
                }

                case 1: {
                    return settingPop;
                }

                case 2: {
                    return settingHealth;
                }
            }

            return null;
        }

        public void renderAll(float partialTicks) {
            this.alphaOne = TurokMath.lerp(this.alphaOne, this.stage > 1 ? 0 : 255, partialTicks * 0.009f);
            this.alphaTwo = TurokMath.lerp(this.alphaTwo, this.stage > 2 ? 0 : 255, partialTicks * 0.009f);

            this.alphaThree = TurokMath.lerp(this.alphaThree, this.stage > 3 ? 0 : 255, partialTicks * 0.009f);
            this.alphaFour = TurokMath.lerp(this.alphaFour, this.stage > 4 ? 0 : 255, partialTicks * 0.009f);

            if (this.cloneOne != null && this.alphaOne >= 10) {
                this.render(this.cloneOne, this.alphaOne);
                // ProcessESP.renderBacktrack(this, this.cloneOne, settingScale.getValue().floatValue(), settingOffsetY.getValue().floatValue(), TurokMath.clamp(this.alphaOne, 0, settingAlpha.getValue().intValue()), settingLineSize.getValue().floatValue(), settingLineType.getValue() != Type.WIRE, !settingFrustumNoRender.getValue(), this.getColor().getColor((int) TurokMath.clamp((int) this.alphaOne, 0, this.getColor().getA())), (Mode) settingRenderMode.getValue());
            }

            if (this.cloneTwo != null && this.alphaTwo >= 10) {
                this.render(this.cloneTwo, this.alphaTwo);
                // ProcessESP.renderBacktrack(this, this.cloneTwo, settingScale.getValue().floatValue(), settingOffsetY.getValue().floatValue(), TurokMath.clamp(this.alphaTwo, 0, settingAlpha.getValue().intValue()), settingLineSize.getValue().floatValue(), settingLineType.getValue() != Type.WIRE, !settingFrustumNoRender.getValue(), this.getColor().getColor((int) TurokMath.clamp((int) this.alphaTwo, 0, this.getColor().getA())), (Mode) settingRenderMode.getValue());
            }

            if (this.cloneThree != null && this.alphaThree >= 10) {
                this.render(this.cloneThree, this.alphaThree);
                // ProcessESP.renderBacktrack(this, this.cloneThree, settingScale.getValue().floatValue(), settingOffsetY.getValue().floatValue(), TurokMath.clamp(this.alphaThree, 0, settingAlpha.getValue().intValue()), settingLineSize.getValue().floatValue(), settingLineType.getValue() != Type.WIRE, !settingFrustumNoRender.getValue(), this.getColor().getColor((int) TurokMath.clamp((int) this.alphaThree, 0, this.getColor().getA())), (Mode) settingRenderMode.getValue());
            }

            if (this.cloneFour != null && this.alphaFour >= 10) {
                this.render(this.cloneFour, this.alphaFour);
                // ProcessESP.renderBacktrack(this, this.cloneFour, settingScale.getValue().floatValue(), settingOffsetY.getValue().floatValue(), TurokMath.clamp(this.alphaFour, 0, settingAlpha.getValue().intValue()), settingLineSize.getValue().floatValue(), settingLineType.getValue() != Type.WIRE, !settingFrustumNoRender.getValue(), this.getColor().getColor((int) TurokMath.clamp((int) this.alphaFour, 0, this.getColor().getA())), (Mode) settingRenderMode.getValue());
            }
        }

        public EntityPlayer addClone() {
            final EntityOtherPlayerMP clone = new EntityOtherPlayerMP(this.player.world, new GameProfile(this.player.getGameProfile().getId(), ""));
            
            clone.setPositionAndRotation(this.player.posX, this.player.posY, this.player.posZ, this.player.rotationYaw, this.player.rotationPitch);
            clone.isDead = false;

            clone.inventory = this.player.inventory;
            clone.limbSwing = this.player.limbSwing;
            clone.limbSwingAmount = this.player.limbSwingAmount;
        
            return clone;
        }

        public void render(Entity entityIn, float alpha) {
            GL11.glPushMatrix();
            GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);

            if (entityIn.ticksExisted == 0) {
                entityIn.lastTickPosX = entityIn.posX;
                entityIn.lastTickPosY = entityIn.posY;
                entityIn.lastTickPosZ = entityIn.posZ;
            }

            double d0 = entityIn.lastTickPosX + (entityIn.posX - entityIn.lastTickPosX) * (double) 1f;
            double d1 = entityIn.lastTickPosY + (entityIn.posY - entityIn.lastTickPosY) * (double) 1f;
            double d2 = entityIn.lastTickPosZ + (entityIn.posZ - entityIn.lastTickPosZ) * (double) 1f;
            float f = entityIn.prevRotationYaw + (entityIn.rotationYaw - entityIn.prevRotationYaw) * 1f;
            int i = entityIn.getBrightnessForRender();
            if (entityIn.isBurning()) {
                i = 15728880;
            }

            int j = i % 65536;
            int k = i / 65536;

            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)j, (float)k);

            Statement.blend();
            Statement.color(255, 255, 255, (int) alpha);

            Statement.unset(GL11.GL_LIGHTING);

            mc.renderManager.renderEntity(entityIn, d0 - mc.renderManager.renderPosX, d1 - mc.renderManager.renderPosY, d2 - mc.renderManager.renderPosZ, f, 1f, false);

            Statement.unset(GL11.GL_BLEND);
            Statement.color(255, 255, 255, 255);

            GL11.glPopAttrib();
            GL11.glPopMatrix();
        }
    }

    /* Misc. */
    public static ValueNumber settingRange = new ValueNumber("Range", "Range", "Range of this shit", 16, 10, 24);
    public static ValueBoolean settingNoSwing = new ValueBoolean("No Swing", "NoSwing", "No swing!", false);

    public static ValueColor settingPop = new ValueColor("Pop", "Pop", "Pop color!", false, Color.ORANGE);
    public static ValueColor settingWalking = new ValueColor("Walking", "Walking", "Walking color.", true, Color.WHITE);
    public static ValueColor settingHealth = new ValueColor("Health", "Health", "Color for health.", false, Color.RED);

    public static ValueNumber settingMaximumHealth = new ValueNumber("Maximum Health", "MaximumHealth", "Maximum health for render health.", 8, 1, 36);
    public static ValueBoolean settingAir = new ValueBoolean("Air Health", "AirHealth", "Only render if player is on air like walking mode.", true);

    public static ValueNumber settingDelay = new ValueNumber("Delay", "Delay", "Sets backtrack delay.", 200, 0, 500);
    public static ValueNumber settingLimit = new ValueNumber("Limit", "Limit", "Limit clones!", 2, 1, 4);

    /* Misc. */
    // public static ValueBoolean settingFrustumNoRender = new ValueBoolean("Frustum No Render", "FrustumNoRender", "Disable ESP on frustum area!", true);
    // public static ValueEnum settingLineType = new ValueEnum("Line Type", "LineType", "Sets line type.", Type.SOFT);
    // public static ValueNumber settingScale = new ValueNumber("Scale","Scale", "Scale of entity.", 1000, 0, 2000);
    // public static ValueNumber settingOffsetY = new ValueNumber("Offset Y", "OffsetY", "Offset space for Y", 0, -2000, 2000);
    // public static ValueEnum settingRenderMode = new ValueEnum("Render Mode", "RenderMode", "Type of render.", Mode.SMOOTH);

    /* Post. */
    // public static ValueNumber settingAlpha = new ValueNumber("Alpha", "Alpha", "Sets alpha value.", 100, 0, 255);

    /* Render color. */
    // public static ValueNumber settingLineSize = new ValueNumber("Line Size", "LineSize", "Sets line size.", 1f, 1f, 5f);

    private final List<Clone> renderList = new ArrayList<>();

    @Override
    public void onSetting() {
        settingMaximumHealth.setEnabled(settingHealth.getValue());
        settingAir.setEnabled(settingHealth.getValue());

        // settingAlpha.setEnabled(settingRenderMode.getValue() == Mode.SMOOTH || settingRenderMode.getValue() == Mode.OUTLINE);
        // settingLineSize.setEnabled(settingRenderMode.getValue() == Mode.SMOOTH || settingRenderMode.getValue() == Mode.OUTLINE || settingRenderMode.getValue() == Mode.LINE);
        // settingLineType.setEnabled(settingRenderMode.getValue() == Mode.SMOOTH || settingRenderMode.getValue() == Mode.OUTLINE || settingRenderMode.getValue() == Mode.LINE);
    }

    @Override
    public void onRender3D() {
        if (NullUtil.isPlayerWorld() || mc.getCurrentServerData() == null) {
            return;
        }

        float partialTicks = Onepop.getClientEventManager().getCurrentRender2DPartialTicks();

        for (Clone clones : this.renderList) {
            final EntityPlayer entityIn = clones.getPlayer();

            if (entityIn == null) {
                continue;
            }

            clones.renderAll(partialTicks);
        }
    }

    @Listener
    public void onPacketReceive(PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketEntityStatus) {
            final SPacketEntityStatus packet = (SPacketEntityStatus) event.getPacket();
            final EntityPlayer player = (EntityPlayer) packet.getEntity(mc.world);

            if (packet.getOpCode() == 35 && mc.player.getDistance(player) < settingRange.getValue().floatValue() && !this.contains(player, 1) && player != mc.player) {
                this.add(player, 1);
            }
        }
    }

    @Listener
    public void onTick(RunTickEvent event) {
        if (NullUtil.isPlayerWorld()) {
            this.renderList.clear();

            return;
        }

        for (Entity entities : mc.world.loadedEntityList) {
            if (entities instanceof EntityPlayer && entities != mc.player && mc.player.getDistance(entities) < settingRange.getValue().intValue()) {
                final EntityPlayer player = (EntityPlayer) entities;

                if (settingNoSwing.getValue()) {
                    player.swingProgress = 0f;
                }

                if (!player.onGround && !this.contains(player, 2) && settingHealth.getValue() && settingMaximumHealth.getValue().intValue() < (player.getHealth() + player.getAbsorptionAmount())) {
                    this.add(player, 2);

                    continue;
                }

                if (!player.onGround && !this.contains(player, 0) && settingWalking.getValue()) {
                    this.add(player, 0);
                }
            }
        }

        for (Clone clones : new ArrayList<>(this.renderList)) {
            clones.updateAll();

            final EntityPlayer player = clones.getPlayer();

            if (mc.world.getEntityByID(player.entityId) == null || clones.isFinished()) {
                this.renderList.remove(clones);
            }
        }
    }

    public EntityPlayer add(final EntityPlayer player, int type) {
        final Clone clone = new Clone(player);

        clone.setType(type);

        this.renderList.add(clone);

        return player;
    }

    public EntityPlayer remove(final EntityPlayer player, int type) {
        Clone found = null;

        for (Clone clones : this.renderList) {
            if (clones.getTag().equals(player.getName()) && clones.getType() == type) {
                found = clones;

                break;
            }
        }

        if (found != null) {
            this.renderList.remove(found);
        }

        return player;
    }

    public boolean contains(final EntityPlayer player, int type) {
        boolean contains = false;

        for (Clone clones : this.renderList) {
            if (clones.getTag().equals(player.getName()) && clones.getType() == type) {
                contains = true;

                break;
            }
        }

        return contains;
    }
}