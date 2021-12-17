package rina.onepop.club.client.module.combat.bedaura;

import me.rina.turok.util.TurokTick;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBed;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import rina.onepop.club.api.module.Module;
import rina.onepop.club.api.module.impl.ModuleCategory;
import rina.onepop.club.api.module.registry.Registry;
import rina.onepop.club.api.setting.value.ValueBoolean;
import rina.onepop.club.api.setting.value.ValueColor;
import rina.onepop.club.api.setting.value.ValueEnum;
import rina.onepop.club.api.setting.value.ValueNumber;
import rina.onepop.club.api.tool.CounterTool;
import rina.onepop.club.api.util.client.NullUtil;
import rina.onepop.club.api.util.entity.Dimension;
import rina.onepop.club.api.util.entity.EntityUtil;
import rina.onepop.club.api.util.entity.PlayerUtil;
import rina.onepop.club.api.util.item.SlotUtil;
import rina.onepop.club.api.util.math.PositionUtil;
import rina.onepop.club.api.util.math.RotationUtil;
import rina.onepop.club.api.util.render.RenderUtil;
import rina.onepop.club.api.util.world.BlockUtil;
import rina.onepop.club.api.util.world.BlocksUtil;
import rina.onepop.club.api.util.world.CrystalUtil;
import rina.onepop.club.client.event.client.RunTickEvent;
import rina.onepop.club.client.event.network.PacketEvent;
import rina.onepop.club.client.manager.network.Rotation;
import rina.onepop.club.client.manager.network.RotationManager;
import rina.onepop.club.client.module.combat.offhand.ModuleOffhand;
import rina.onepop.club.client.module.combat.offhand.OffhandMode;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

import java.awt.*;

/**
 * @author SrRina
 * @since 17/04/2021 at 20:16
 **/
@Registry(name = "Bed-Aura", tag = "BedAura", description = "Makes you automatically places and click on bed!", category = ModuleCategory.COMBAT)
public class ModuleBedAura extends Module {
    /* Misc. */
    public static ValueBoolean settingAntiNaked = new ValueBoolean("Anti-Naked", "AntiNaked", "Verify if players is with armor to get target.", true);
    public static ValueBoolean settingAutoSwitch = new ValueBoolean("Auto-Switch", "AutoSwitch", "Automatically switch the slot for bed!", true);
    public static ValueBoolean settingAutoOffhand = new ValueBoolean("Auto-Offhand", "AutoOffhand", "Automatically set offhand bed if is needed!", false);
    public static ValueNumber settingRange = new ValueNumber("Range", "Range", "Range for trace!", 6f, 1f, 13f);
    public static ValueEnum settingTargetMode = new ValueEnum("Target Mode", "TargetMode", "Modes to get target.", TargetMode.UNSAFE);
    public static ValueEnum settingLoop = new ValueEnum("Loop", "Loop", "Loop mode.", Loop.POST);

    /* Damage. */
    public static ValueBoolean settingSuicide = new ValueBoolean("Suicide", "Suicide", "Ignore self damage!", false);
    public static ValueNumber settingSelfDamage = new ValueNumber("Self Damage", "SelfDamage", "The self place damage!", 8, 1, 36);
    public static ValueNumber settingMinimumDamageTarget = new ValueNumber("Min. Target Dmg.", "MinimumTargetDamage", "The minimum damage for target!", 2f, 1f, 36f);

    /* Place. */
    public static ValueBoolean settingPlace = new ValueBoolean("Place", "Place", "Makes you place!", true);
    public static ValueBoolean settingAirPlace = new ValueBoolean("Air Place", "Air-Place", "Air place for new Minecraft for 1.13+", false);
    public static ValueNumber settingPlaceRange = new ValueNumber("Place Range", "PlaceRange", "The place ranges!", 4f, 1f, 6f);
    public static ValueNumber settingPlaceDelay = new ValueNumber("Place Delay", "PlaceDelay", "The MS delay for place.", 50, 0, 100);
    public static ValueEnum settingPlaceRotate = new ValueEnum("Place Rotate", "Place Rotate", "The rotations for place!", Rotation.SEND);

    /* Click. */
    public static ValueBoolean settingClick = new ValueBoolean("Click", "Click", "You clicks!", true);
    public static ValueBoolean settingClickPredict = new ValueBoolean("Click Predict", "ClickPredict", "Predict clicks!", false);
    public static ValueBoolean settingClickOnlyWhenEquippedBed = new ValueBoolean("Click With Bed", "ClickWithBed", "Only click if you are equipped with bed in hand!", false);
    public static ValueEnum settingClickHand = new ValueEnum("Click Hand", "ClickHand", "The mode of click!", ClickHand.AUTO);
    public static ValueNumber settingClickRange = new ValueNumber("Click Range", "ClickRange", "The range for click in beds!", 4f, 1f, 6f);
    public static ValueNumber settingClickDelay = new ValueNumber("Click Delay", "ClickDelay", "The MS delay for click.", 50, 0, 100);
    public static ValueEnum settingClickRotate = new ValueEnum("Click Rotate", "ClickRotate", "The click rotates!", Rotation.SEND);

    /* Render. */
    public static ValueBoolean settingRenderSwing = new ValueBoolean("Render Swing", "RenderSwing", "Render swing player.", true);

    /* Render Color. */
    public static ValueColor settingRenderColor = new ValueColor("Render Color", "RenderColor", "Set color.", Color.orange);

    /* Outline misc. */
    public static ValueNumber settingOutlineLineSize = new ValueNumber("Outline Line Size", "OutlineLineSize", "Line size.", 1.0f, 1f, 3.0f);
    public static ValueNumber settingOutlineAlpha = new ValueNumber("Outline Alpha", "OutlineAlpha", "Color line range alpha.", 255, 0, 255);

    private final CounterTool<BlockPos> counterClickPacket = new CounterTool<>();
    private final CounterTool<BlockPos> counterPlacePacket = new CounterTool<>();

    private final TurokTick delayPlace = new TurokTick();
    private final TurokTick delayClick = new TurokTick();
    private final TurokTick delayPredict = new TurokTick();

    private int bedSlot;
    private int currentSlot;

    private EntityPlayer targetPlayer;
    private boolean withOffhand;

    private BlockPos targetPlace;
    private BlockPos targetClick;

    private BlockPos lastPlace;
    private BlockPos lastClick;

    private EnumFacing targetDirection;
    private boolean offhandModuleNotifier;

    /* Color for render. */
    private Color outline = new Color(255, 255, 255, 255);
    private Color solid = new Color(255, 255, 255, 255);

    @Override
    public void onSetting() {
        settingSelfDamage.setEnabled(!settingSuicide.getValue());

        if (mc.world != null && mc.player != null) {
            settingClickRange.setMaximum(mc.playerController.getBlockReachDistance());
        }

        settingPlaceDelay.setEnabled(settingPlace.getValue());
        settingAirPlace.setEnabled(settingPlace.getValue());
        settingPlaceRange.setEnabled(settingPlace.getValue());
        settingPlaceRotate.setEnabled(settingPlace.getValue());

        settingClickRange.setEnabled(settingClick.getValue());
        settingClickPredict.setEnabled(settingClick.getValue());
        settingClickDelay.setEnabled(settingClick.getValue());
        settingClickRotate.setEnabled(settingClick.getValue());
        settingClickHand.setEnabled(settingClick.getValue());
        settingClickOnlyWhenEquippedBed.setEnabled(settingClick.getValue());

        this.solid = settingRenderColor.getColor();
        this.outline = settingRenderColor.getColor(settingOutlineAlpha.getValue().intValue());
    }

    protected void updateOffhand() {
        if (this.isEnabled() && this.withOffhand && ModuleOffhand.settingOffhandMode.getValue() != OffhandMode.BED && !this.offhandModuleNotifier) {
            if (settingAutoOffhand.getValue()) {
                ModuleOffhand.settingOffhandMode.setValue(OffhandMode.BED);

                this.print("Bed offhand active!");
            } else {
                this.print("Check offhand module and set to bed mode!");
            }

            this.offhandModuleNotifier = true;
        }
    }

    @Override
    public void onEnable() {
        this.counterClickPacket.clear();
        this.counterPlacePacket.clear();
        this.delayPredict.reset();
        this.delayPlace.reset();
        this.delayClick.reset();

        this.offhandModuleNotifier = false;
    }

    @Override
    public void onDisable() {
        this.lastPlace = null;
        this.lastClick = null;

        this.targetPlace = null;
        this.targetClick = null;

        this.delayPredict.reset();
        this.delayPlace.reset();
        this.delayClick.reset();

        this.offhandModuleNotifier = false;
    }

    @Override
    public void onRender3D() {
        if (NullUtil.isPlayerWorld()) {
            return;
        }

        if (this.targetPlace != null && this.targetDirection != null) {
            float line = settingOutlineLineSize.getValue().floatValue();

            final BlockPos firstBed = this.targetPlace.up();
            final BlockPos extra = firstBed.offset(this.targetDirection);

            float diffX = extra.x - firstBed.x;
            float diffZ = extra.z - firstBed.z;

            float x = diffX < 0 ? extra.x : firstBed.x;
            float z = diffZ < 0 ? extra.z : firstBed.z;

            float w = 1;

            if (diffX != 0 && diffZ == 0) {
                w = 2;
            }

            float l = 1;

            if (diffX == 0 && diffZ != 0) {
                l = 2;
            }

            RenderUtil.drawSolidBlock(camera, x, firstBed.y, z, w, 0.5f, l, this.solid);
            RenderUtil.drawOutlineBlock(camera, x, firstBed.y, z, w, 0.5f, l, line, this.outline);
        }

        if (settingLoop.getValue() == Loop.FAST) {
            this.doBedAura();
        }
    }

    @Listener
    public void onReceivePacket(PacketEvent.Receive event) {
        if (settingLoop.getValue() == Loop.RECEIVE) {
            this.doBedAura();
        }
    }

    @Listener
    public void onTick(RunTickEvent event) {
        if (NullUtil.isPlayerWorld()) {
            return;
        }

        this.updateOffhand();

        if (PlayerUtil.getCurrentDimension() == Dimension.WORLD) {
            this.print("You're not in nether/end dimension!");
            this.setDisabled();

            return;
        }

        this.withOffhand = mc.player.getHeldItemOffhand().getItem() == Items.BED;
        this.bedSlot = SlotUtil.findItemSlotFromHotBar(Items.BED);

        this.doFindTarget();

        if (this.targetPlayer == null) {
            this.targetPlace = null;
            this.targetClick = null;

            return;
        }

        if (!(settingClick.getValue() && this.isClickable() && this.isTargetAliveAndNotNull())) {
            this.targetClick = null;
        }

        if (settingPlace.getValue() && this.isBedFound() && this.isTargetAliveAndNotNull()) {
            if (!this.withOffhand) {
                if (mc.player.inventory.currentItem != this.currentSlot && mc.player.inventory.currentItem != this.bedSlot) {
                    this.currentSlot = mc.player.inventory.currentItem;

                    SlotUtil.setCurrentItem(this.currentSlot);
                }
            }
        } else {
            this.targetDirection = null;
            this.targetPlace = null;
        }

        if (settingLoop.getValue() == Loop.POST) {
            this.doBedAura();
        }
    }

    public void doPlace() {
        if (!(settingPlace.getValue() && this.isBedFound() && this.isTargetAliveAndNotNull())) {
            return;
        }

        float bestDamage = 1.0f;
        float range = settingPlaceRange.getValue().floatValue();

        BlockPos bestPlace = null;
        EnumFacing direction = null;

        for (BlockPos positions : BlocksUtil.getSphereList(range)) {
            if (BlockUtil.getBlock(positions) == Blocks.AIR || BlockUtil.getBlock(positions) == Blocks.BED) {
                continue;
            }

            final EnumFacing bedDirection = BlockUtil.getBedPlaceableFaces(positions, settingAirPlace.getValue());

            if (bedDirection == null) {
                continue;
            }

            double trace = this.targetPlayer.getDistanceSq(positions);

            if (trace >= (settingRange.getValue().floatValue() * settingRange.getValue().floatValue())) {
                continue;
            }

            float targetDamage = Math.max(CrystalUtil.calculateDamage(positions.offset(bedDirection).x, positions.offset(bedDirection).up().y, positions.offset(bedDirection).z, this.targetPlayer), CrystalUtil.calculateDamage(positions.x, positions.up().y, positions.z, this.targetPlayer));

            if (targetDamage > bestDamage) {
                float selfDamage = Math.max(CrystalUtil.calculateDamage(positions.offset(bedDirection), mc.player), CrystalUtil.calculateDamage(positions, mc.player));

                if ((selfDamage > settingSelfDamage.getValue().intValue() && !settingSuicide.getValue()) || targetDamage < settingMinimumDamageTarget.getValue().floatValue()) {
                    continue;
                }

                if (this.counterPlacePacket.getCount(positions) != null && this.counterPlacePacket.getCount(positions) > 4) {
                    this.counterPlacePacket.remove(positions);
                }

                bestDamage = targetDamage;
                direction = bedDirection;
                bestPlace = positions;
            }
        }

        this.targetPlace = this.isBedAtHand() ? bestPlace : null;
        this.targetDirection = this.targetPlace != null ? direction : null;
    }

    public void doClick() {
        if (!(settingClick.getValue() && this.isClickable() && this.isTargetAliveAndNotNull())) {
            return;
        }

        BlockPos targetBed = null;

        float clickRange = settingClickRange.getValue().floatValue();
        float bestDamage = 1.0f;

        for (TileEntity tiles : mc.world.loadedTileEntityList) {
            if (!(tiles instanceof TileEntityBed)) {
                continue;
            }

            if (mc.player.getDistance(tiles.getPos().x, tiles.getPos().y, tiles.getPos().z) > settingClickRange.getValue().intValue()) {
                continue;
            }

            float targetDamage = CrystalUtil.calculateDamage(tiles.getPos().x, tiles.getPos().up().y, tiles.getPos().y, this.targetPlayer);

            if (targetDamage > bestDamage) {
                bestDamage = targetDamage;
                targetBed = tiles.getPos();
            }
        }

        this.targetClick = targetBed;
    }

    public void doBedAura() {
        try {
            if (this.delayPlace.isPassedMS(settingPlaceDelay.getValue().intValue())) {
                this.doPlace();

                if (this.targetPlace == null || this.targetDirection == null) {
                    return;
                }

                final EnumFacing facing = EnumFacing.UP;

                Vec3d hitLook = PositionUtil.calculateHitPlace(PlayerUtil.getBlockPos().offset(this.targetDirection).up(), this.targetDirection);
                float[] rotates = RotationUtil.getPlaceRotation(hitLook);

                if (settingPlaceRotate.getValue() == Rotation.REL || settingPlaceRotate.getValue() == Rotation.LEGIT || (this.counterPlacePacket.getCount(this.targetPlace) == null && settingPlaceRotate.getValue() == Rotation.SEND)) {
                    RotationManager.task(settingPlaceRotate.getValue(), rotates);
                }

                boolean flag = false;

                if (settingAutoSwitch.getValue() && !this.withOffhand) {
                    SlotUtil.setServerCurrentItem(this.bedSlot);

                    flag = true;
                }

                final EnumHand hand = this.withOffhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND;

                mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(this.targetPlace, facing, hand, 0f, 0f, 0f));

                if (settingClick.getValue() && settingClickPredict.getValue() && this.delayPredict.isPassedMS(settingClickDelay.getValue().intValue()) && this.lastPlace != null) {
                    final EnumHand handBreak = settingClickHand.getValue() == ClickHand.AUTO ? (this.withOffhand ? EnumHand.OFF_HAND : EnumHand.OFF_HAND) : ((ClickHand) settingClickHand.getValue()).getHand();

                    mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(this.targetPlace.up(), EnumFacing.UP, handBreak, 0f, 0f, 0f));

                    this.delayPredict.reset();
                }

                if (settingRenderSwing.getValue()) {
                    mc.player.swingArm(hand);
                } else {
                    mc.player.connection.sendPacket(new CPacketAnimation(hand));
                }

                if (flag) {
                    SlotUtil.setServerCurrentItem(mc.player.inventory.currentItem);
                }

                this.counterPlacePacket.dispatch(this.targetPlace);

                this.delayPlace.reset();
                this.lastPlace = this.targetPlace;
            }

            if (this.delayClick.isPassedMS(settingClickDelay.getValue().intValue())) {
                this.doClick();

                if (this.targetClick == null) {
                    return;
                }

                EnumFacing facing = EnumFacing.getDirectionFromEntityLiving(this.targetClick, mc.player);
                Vec3d hit = PositionUtil.calculateHitPlace(this.targetClick, facing);

                float[] rotates = RotationUtil.getPlaceRotation(hit);

                if ((settingClickRotate.getValue() == Rotation.REL || settingClickRotate.getValue() == Rotation.LEGIT) || (this.counterPlacePacket.getCount(this.targetClick) == null && settingClickRotate.getValue() == Rotation.SEND)) {
                    RotationManager.task(settingClickRotate.getValue(), rotates);
                }

                final EnumHand hand = settingClickHand.getValue() == ClickHand.AUTO ? (this.withOffhand ? EnumHand.OFF_HAND : EnumHand.OFF_HAND) : ((ClickHand) settingClickHand.getValue()).getHand();

                mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(this.targetClick, facing, hand, 0.5f, 0.5f, 0.5f));

                if (settingRenderSwing.getValue()) {
                    mc.player.swingArm(hand);
                } else {
                    mc.player.connection.sendPacket(new CPacketAnimation(hand));
                }

                this.counterClickPacket.dispatch(this.targetClick);
                this.delayClick.reset();
            }
        } catch (Exception exc) {}
    }

    public void doFindTarget() {
        this.targetPlayer = EntityUtil.getTarget(settingRange.getValue().floatValue(), settingTargetMode.getValue() == TargetMode.UNSAFE, settingAntiNaked.getValue());
    }

    public boolean isClickable() {
        return (this.isBedFound() && this.isBedAtHand()) || !settingClickOnlyWhenEquippedBed.getValue();
    }

    public boolean isBedFound() {
        return this.bedSlot != -1 || this.withOffhand;
    }

    public boolean isBedAtHand() {
        return this.withOffhand || (mc.player.inventory.currentItem == this.bedSlot || mc.player.getHeldItemMainhand().getItem() == Items.BED);
    }

    public boolean isTargetAliveAndNotNull() {
        return this.targetPlayer != null && this.targetPlayer.getHealth() > 0 && this.targetPlayer.isEntityAlive();
    }
}