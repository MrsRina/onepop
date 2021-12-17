package rina.onepop.club.client.module.combat.autocrystalrewrite;

import me.rina.turok.util.TurokTick;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import rina.onepop.club.api.ISLClass;
import rina.onepop.club.api.module.Module;
import rina.onepop.club.api.module.impl.ModuleCategory;
import rina.onepop.club.api.module.registry.Registry;
import rina.onepop.club.api.setting.value.ValueBoolean;
import rina.onepop.club.api.setting.value.ValueColor;
import rina.onepop.club.api.setting.value.ValueEnum;
import rina.onepop.club.api.setting.value.ValueNumber;
import rina.onepop.club.api.social.management.SocialManager;
import rina.onepop.club.api.social.type.SocialType;
import rina.onepop.club.api.strict.StrictUtilityInjector;
import rina.onepop.club.api.util.client.NullUtil;
import rina.onepop.club.api.util.entity.EntityUtil;
import rina.onepop.club.api.util.item.SlotUtil;
import rina.onepop.club.api.util.math.PositionUtil;
import rina.onepop.club.api.util.math.RotationUtil;
import rina.onepop.club.api.util.render.RenderUtil;
import rina.onepop.club.api.util.world.CrystalUtil;
import rina.onepop.club.client.event.client.ClientTickEvent;
import rina.onepop.club.client.event.client.RunTickEvent;
import rina.onepop.club.client.event.network.PacketEvent;
import rina.onepop.club.client.manager.network.HotBarManager;
import rina.onepop.club.client.module.render.ModuleAutoCrystalRender;
import team.stiff.pomelo.handler.ListenerPriority;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author SrRina
 * @since 04/09/2021 at 00:00
 **/
@Registry(name = "Auto-Crystal Rewrite", tag = "AutoCrystalRewrite", description = "Add crystals on feet of enemy & explode them!", category = ModuleCategory.COMBAT)
public class ModuleAutoCrystalRewrite extends Module {
    public static ModuleAutoCrystalRewrite INSTANCE;
    public static ValueEnum settingTab = new ValueEnum("|", "Category", "Category of auto crystal.", Tab.CRYSTAL);

    /* Misc. */
    public static ValueBoolean settingAntiNaked = new ValueBoolean("Anti-Naked", "AntiNaked", "No target for naked players.", true);
    public static ValueEnum settingAutoSwitch = new ValueEnum("Auto-Switch", "AutoSwitch", "Automatically switch...", Switch.NORMAL);
    public static ValueBoolean settingNoSoundDelay = new ValueBoolean("No Sound Delay", "NoSoundDelay", "Save 1 tick by explosion in MC.", true);
    public static ValueBoolean settingPlace113 = new ValueBoolean("Place 1.13", "Place113", "New Minecraft placement 1x1x1.", false);
    public static ValueNumber settingHealth = new ValueNumber("Health Switch", "HealthSwitch", "Switch for needed time.", 20, 1, 36);
    public static ValueBoolean settingPredict = new ValueBoolean("Predict", "Predict", "Normal predication.", true);
    public static ValueBoolean settingIncreaseTicks = new ValueBoolean("Increase Ticks", "IncreaseTicks", "Increase the ticks on ca.", true);
    public static ValueEnum settingStrictPriority = new ValueEnum("Strict Priority", "StrictPriority", "Priority the strict on server.", Priority.OFF);

    /* Crystal. */
    public static ValueNumber settingEntityRange = new ValueNumber("Entity Range", "EntityRage", "Sets range for target.", 13f, 4f, 14f);
    public static ValueBoolean settingWallCheck = new ValueBoolean("Wall Check", "WallCheck", "Enable wall check.", false);
    public static ValueNumber settingWallRange = new ValueNumber("Wall Range", "WallRange", "Prevents blocks in wall.", 4f, 2f, 5f);
    public static ValueNumber settingBreakRange = new ValueNumber("Break Range", "BreakRange", "Sets break range.", 4f, 2f, 6f);
    public static ValueNumber settingPlaceRange = new ValueNumber("Place Range", "PlaceRange", "Sets place range.", 4f, 2f, 6f);
    public static ValueNumber settingPlaceDelay = new ValueNumber("Place Delay", "PlaceDelay", "Sets place delay.", 33, 0, 50);
    public static ValueNumber settingBreakDelay = new ValueNumber("Break Delay", "BreakDelay", "Sets break delay.", 33, 0, 50);
    public static ValueNumber settingSelfDamage = new ValueNumber("Self Damage", "SelfDamage", "Self damage.", 9, 1, 36);
    public static ValueNumber settingMinimumDamage = new ValueNumber("Min. Damage", "Min. Damage", "Sets minimum damage.", 4, 0, 36);
    public static ValueNumber settingFacingY = new ValueNumber("Facing Y", "FacingY", "Sets the facing Y from place.", 1f, 0f, 1f);

    /* Render. */
    public static ValueColor settingColorPlace = new ValueColor("Place Color", "PlaceColor", "Render the placement.", true, Color.cyan);
    public static ValueNumber settingLineAlpha = new ValueNumber("Line Alpha", "lineAlpha", "Sets line alpha.", 100, 0, 255);
    public static ValueNumber settingLineSize = new ValueNumber("Line Size", "LineSize", "Sets line size.", 1f, 1f, 5f);

    private final TurokTick placeDelayMS = new TurokTick();
    private final TurokTick breakDelayMS = new TurokTick();

    private final TurokTick rotationDelayMS = new TurokTick();
    private final TurokTick cooldownDelayMS = new TurokTick();
    private boolean multiConnection;

    private float yaw;
    private float pitch;

    private final CPacketPlayerTryUseItemOnBlock placePacket = new CPacketPlayerTryUseItemOnBlock();
    private final CPacketUseEntity breakPacket =  new CPacketUseEntity();

    private EntityPlayer entity;
    private BlockPos position;

    private boolean isCrystalInOffhand;
    private boolean isCrystalInMainHand;

    private final List<Integer> hitCount = new ArrayList<>();
    public final List<BlockPos> placeCount = new ArrayList<>();

    private float entityDistance;
    private int entityID;

    public ModuleAutoCrystalRewrite() {
        INSTANCE = this;

        this.placePacket.facingX = 0.5f;
        this.placePacket.facingZ = 0.5f;
    }

    public EntityPlayer getEntity() {
        return entity;
    }

    public BlockPos getPosition() {
        return position;
    }

    @Override
    public void onSetting() {
        settingAutoSwitch.setEnabled(settingTab.getValue() == Tab.MISC);
        settingHealth.setEnabled(settingTab.getValue() == Tab.MISC && settingAutoSwitch.getValue() == Switch.NORMAL);
        settingIncreaseTicks.setEnabled(settingTab.getValue() == Tab.MISC);
        settingPredict.setEnabled(settingTab.getValue() == Tab.MISC);
        settingNoSoundDelay.setEnabled(settingTab.getValue() == Tab.MISC);
        settingAntiNaked.setEnabled(settingTab.getValue() == Tab.MISC);
        settingStrictPriority.setEnabled(settingTab.getValue() == Tab.MISC);
        settingPlace113.setEnabled(settingTab.getValue() == Tab.MISC);

        settingEntityRange.setEnabled(settingTab.getValue() == Tab.CRYSTAL);
        settingBreakRange.setEnabled(settingTab.getValue() == Tab.CRYSTAL);
        settingPlaceRange.setEnabled(settingTab.getValue() == Tab.CRYSTAL);
        settingPlaceDelay.setEnabled(settingTab.getValue() == Tab.CRYSTAL);
        settingBreakDelay.setEnabled(settingTab.getValue() == Tab.CRYSTAL);
        settingWallCheck.setEnabled(settingTab.getValue() == Tab.CRYSTAL);
        settingWallRange.setEnabled(settingTab.getValue() == Tab.CRYSTAL && settingWallCheck.getValue());
        settingSelfDamage.setEnabled(settingTab.getValue() == Tab.CRYSTAL);
        settingFacingY.setEnabled(settingTab.getValue() == Tab.CRYSTAL);
        settingMinimumDamage.setEnabled(settingTab.getValue() == Tab.CRYSTAL);

        settingColorPlace.setEnabled(settingTab.getValue() == Tab.RENDER);
        settingLineAlpha.setEnabled(settingTab.getValue() == Tab.RENDER);
        settingLineSize.setEnabled(settingTab.getValue() == Tab.RENDER);
    }

    @Override
    public void onDisable() {
        this.placeCount.clear();
        this.hitCount.clear();

        if (settingAutoSwitch.getValue() == Switch.SILENT && HotBarManager.currentItem(HotBarManager.SERVER) != HotBarManager.currentItem(HotBarManager.CLIENT)) {
            mc.player.connection.sendPacket(new CPacketHeldItemChange(HotBarManager.currentItem(HotBarManager.CLIENT)));
            mc.playerController.updateController();
            mc.player.stopActiveHand();
        }
    }

    @Override
    public void onRender3D() {
        if (this.position != null && settingColorPlace.getValue() && !ModuleAutoCrystalRender.INSTANCE.isEnabled()) {
            RenderUtil.drawSolidBlock(camera, this.position, settingColorPlace.getColor());
            RenderUtil.drawOutlineBlock(camera, this.position, settingLineSize.getValue().floatValue(), settingColorPlace.getColor(settingLineAlpha.getValue().intValue()));
        }

        if (settingIncreaseTicks.getValue()) {
            if ((this.isCrystalInMainHand || this.isCrystalInOffhand) && this.isEntityTargeted(this.entity)) {
                this.update();
            }
        }

        if (settingStrictPriority.getValue() != Priority.OFF) {
            this.rotations();
        }
    }

    @Listener
    public void onPacketSend(PacketEvent.Send event) {
        if (event.getPacket() instanceof CPacketPlayerTryUseItemOnBlock && settingStrictPriority.getValue() == Priority.HIGH && this.entity != null) {
            final CPacketPlayerTryUseItemOnBlock packet = (CPacketPlayerTryUseItemOnBlock) event.getPacket();

            event.setCanceled(!CrystalUtil.isCrystalPlaceable(packet.getPos(), settingPlace113.getValue(), true));
        }

        if (settingStrictPriority.getValue() != Priority.OFF && this.entity != null && this.cooldownDelayMS.isPassedMS(500)) {
            StrictUtilityInjector.rotation(event.getPacket(), this.yaw, this.pitch);
        }
    }

    @Listener(priority = ListenerPriority.HIGHEST)
    public void onPacketReceive(PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketSpawnObject && settingPredict.getValue()) {
            final SPacketSpawnObject packet = (SPacketSpawnObject) event.getPacket();
            final BlockPos pos = new BlockPos(packet.getX(), packet.getY(), packet.getZ()).down();

            if (packet.getType() == 51 && !this.hitCount.contains(packet.getEntityID()) && this.placeCount.contains(pos)) {
                final CPacketUseEntity attack = new CPacketUseEntity();

                attack.entityId = packet.getEntityID();
                attack.action = CPacketUseEntity.Action.ATTACK;

                mc.player.connection.sendPacket(attack);
                mc.player.connection.sendPacket(new CPacketAnimation(EnumHand.MAIN_HAND));

                this.hitCount.add(packet.getEntityID());

                this.placeCount.remove(pos);
                this.placeDelayMS.reset();
            }
        } else if (event.getPacket() instanceof SPacketSoundEffect && settingNoSoundDelay.getValue()) {
            final SPacketSoundEffect packet = (SPacketSoundEffect) event.getPacket();

            if (packet.getCategory() == SoundCategory.BLOCKS && packet.getSound() == SoundEvents.ENTITY_GENERIC_EXPLODE) {
                boolean hitList = false;

                for (Entity entities : ISLClass.mc.world.loadedEntityList) {
                    if (entities instanceof EntityEnderCrystal) {
                        if (entities.getDistance(packet.getX(), packet.getY(), packet.getZ()) <= 6.0f) {
                            entities.setDead();

                            hitList = true;
                        }
                    }
                }

                if (hitList) {
                    this.hitCount.clear();
                }
            }
        }
    }
    
    @Listener(priority = ListenerPriority.HIGHEST)
    public void onTickEvent(RunTickEvent event) {
        if (NullUtil.isPlayerWorld()) {
            return;
        }

        if (!settingIncreaseTicks.getValue()) {
            if ((this.isCrystalInMainHand || this.isCrystalInOffhand) && this.isEntityTargeted(this.entity)) {
                this.update();
            }
        }
    }

    @Listener(priority = ListenerPriority.LOW)
    public void onClientTickEvent(ClientTickEvent event) {
        if (NullUtil.isPlayerWorld()) {
            return;
        }

        this.entity = this.target();

        if (this.entity != null) {
            this.entityDistance = mc.player.getDistance(this.entity);
        }

        this.isCrystalInOffhand = mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL;
        this.isCrystalInMainHand = mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL;

        int slot = this.findCrystal();

        if (slot != -1 && settingAutoSwitch.getValue() == Switch.SILENT && !this.isCrystalInOffhand && !mc.gameSettings.keyBindUseItem.isKeyDown()) {
            final Item item = SlotUtil.getItem(HotBarManager.currentItem(HotBarManager.SERVER));

            if (item != Items.END_CRYSTAL) {
                mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
            }

            this.isCrystalInMainHand = true;
        }

        if (mc.gameSettings.keyBindUseItem.isKeyDown() && !this.isCrystalInMainHand && slot != -1 && HotBarManager.currentItem(HotBarManager.SERVER) != HotBarManager.currentItem(HotBarManager.CLIENT)) {
            mc.player.connection.sendPacket(new CPacketHeldItemChange(HotBarManager.currentItem(HotBarManager.CLIENT)));
            mc.playerController.updateController();
            mc.player.stopActiveHand();
        }

        final boolean flag = settingAutoSwitch.getValue() == Switch.NORMAL && this.entity != null && (this.entity.getHealth() + this.entity.getAbsorptionAmount()) <= settingHealth.getValue().floatValue();

        if (flag && slot != -1 && !this.isCrystalInOffhand && !this.isCrystalInMainHand) {
            SlotUtil.setCurrentItem(slot);
        }

        if (this.breakDelayMS.isPassedMS(settingEntityRange.getValue().floatValue() + settingBreakDelay.getValue().intValue() * 2)) {
            this.hitCount.clear();
        }

        if (this.entity == null || (slot == -1 && !this.isCrystalInOffhand)) {
            this.position = null;
        }
    }

    protected void update() {
        if (this.breakDelayMS.isPassedMS(settingBreakDelay.getValue().floatValue())) {
            this.entityID = this.findCrystalBreak();

            if (this.entityID != -1) {
                this.breakPacket.entityId = this.entityID;
                this.breakPacket.action = CPacketUseEntity.Action.ATTACK;

                mc.player.connection.sendPacket(this.breakPacket);
                mc.player.connection.sendPacket(new CPacketAnimation(EnumHand.MAIN_HAND));

                this.hitCount.add(this.breakPacket.entityId);
                this.breakDelayMS.reset();
            }
        }

        if (this.placeDelayMS.isPassedMS(settingPlaceDelay.getValue().floatValue())) {
            this.position = this.findCrystalPlace();

            if (this.position != null) {
                this.placePacket.hand = this.isCrystalInOffhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND;

                if (settingStrictPriority.getValue() == Priority.HIGH) {
                    final RayTraceResult raytrace = mc.world.rayTraceBlocks(new Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ), new Vec3d(this.position.x + 0.5f, this.position.y - 0.5f, this.position.z + 0.5f));

                    this.placePacket.placedBlockDirection = EnumFacing.UP;

                    if (raytrace != null && raytrace.sideHit != null) {
                        this.placePacket.placedBlockDirection = raytrace.sideHit;
                    }
                } else {
                    this.placePacket.placedBlockDirection = EnumFacing.UP;

                    if (this.position.getY() > mc.player.getPositionEyes(1f).y) {
                        this.placePacket.placedBlockDirection = EnumFacing.DOWN;
                    }
                }

                this.placePacket.facingY = settingFacingY.getValue().floatValue();
                this.placePacket.position = this.position;

                mc.player.connection.sendPacket(this.placePacket);
                mc.player.connection.sendPacket(new CPacketAnimation(this.placePacket.getHand()));

                this.placeCount.add(this.position);
                this.placeDelayMS.reset();
            }
        }
    }

    protected void rotations() {
        if (!this.rotationDelayMS.isPassedMS(settingPlaceDelay.getValue().intValue())) {
            final BlockPos position = this.findCrystalPlace();

            if (position != null && (this.isCrystalInMainHand || this.isCrystalInOffhand)) {
                final RayTraceResult raytrace = mc.world.rayTraceBlocks(new Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ), new Vec3d(position.x + 0.5f, position.y - 0.5f, position.z + 0.5f));

                if (raytrace != null && raytrace.sideHit != null) {
                    float[] values = RotationUtil.getPlaceRotation(PositionUtil.calculateHitPlace(position, raytrace.sideHit));

                    this.yaw = values[0];
                    this.pitch = values[1];
                }
            }
        } else {
            this.rotationDelayMS.reset();
        }

        if (mc.player.getHeldItemOffhand().getItem() instanceof ItemBlock || mc.player.getHeldItemMainhand().getItem() instanceof ItemBlock) {
            this.cooldownDelayMS.reset();
        }
    }

    protected EntityPlayer target() {
        EntityPlayer target = null;
        float distance = settingEntityRange.getValue().floatValue();

        for (EntityPlayer entities : mc.world.playerEntities) {
            if (entities.isEntityAlive() && mc.player != entities) {
                if (SocialManager.is(entities.getName(), SocialType.FRIEND) || (settingAntiNaked.getValue() && EntityUtil.isEntityPlayerNaked(entities))) {
                    continue;
                }

                float dist = mc.player.getDistance(entities);

                if (dist < distance) {
                    distance = dist;
                    target = entities;
                }
            }
        }

        return target;
    }

    protected int findCrystal() {
        if (!this.isEntityTargeted(this.entity)) {
            return -1;
        }

        return SlotUtil.findItemSlotFromHotBar(Items.END_CRYSTAL);
    }

    protected boolean isEntityTargeted(EntityPlayer entity) {
        return entity != null && entity.getHealth() > 0f && entity.isEntityAlive() && this.entityDistance != -1f && this.entityDistance < settingEntityRange.getValue().floatValue();
    }

    protected BlockPos findCrystalPlace() {
        float damage = 0.5f;

        if (this.entityDistance > settingEntityRange.getValue().floatValue() || this.entityDistance == -1f || this.entity == null) {
            return null;
        }

        BlockPos position = null;

        for (BlockPos places : CrystalUtil.getSphereCrystalPlace(settingPlaceRange.getValue().floatValue(), settingPlace113.getValue(), true)) {
            float entityDamage = CrystalUtil.calculateDamage(places, this.entity);
            float selfDamage = CrystalUtil.calculateDamage(places, mc.player);

            if (settingWallCheck.getValue() && mc.player.getDistance(places.x, places.y, places.z) > settingWallRange.getValue().floatValue() && mc.world.rayTraceBlocks(new Vec3d(mc.player.posX, mc.player.posY + (double) mc.player.getEyeHeight(), mc.player.posZ), new Vec3d(places.getX() + 0.5f, places.getY() - settingFacingY.getValue().floatValue(), places.getZ() + 0.5f), false, true, false) != null) {
                continue;
            }

            if (entityDamage > settingMinimumDamage.getValue().floatValue() && entityDamage > damage && selfDamage < settingSelfDamage.getValue().floatValue()) {
                damage = entityDamage;
                position = places;
            }
        }

        return position;
    }

    protected int findCrystalBreak() {
        int id = -1;
        float damage = 0.5f;

        for (Entity entities : mc.world.loadedEntityList) {
            if (entities instanceof EntityEnderCrystal && entities.isEntityAlive() && mc.player.getDistance(entities) < settingBreakRange.getValue().floatValue() && !this.hitCount.contains(entities.entityId)) {
                float entityDamage = CrystalUtil.calculateDamage(entities.posX, entities.posY, entities.posZ, this.entity);
                float selfDamage = CrystalUtil.calculateDamage(entities.posX, entities.posY, entities.posZ, mc.player);

                if (entityDamage > damage && selfDamage < settingSelfDamage.getValue().floatValue()) {
                    damage = entityDamage;
                    id = entities.entityId;
                }
            }
        }

        return id;
    }

    protected float[] calculateLookAt(double x, double y, double z) {
        return RotationUtil.getPlaceRotation(new Vec3d(x, y, z));
    }
}