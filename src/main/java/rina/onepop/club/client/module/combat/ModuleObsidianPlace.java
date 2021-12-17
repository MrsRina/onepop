package rina.onepop.club.client.module.combat;

/**
 * @author Rina
 * @since 02/10/2021 at 10:56am
 **/

import me.rina.turok.util.TurokTick;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import rina.onepop.club.api.module.Module;
import rina.onepop.club.api.module.impl.ModuleCategory;
import rina.onepop.club.api.module.registry.Registry;
import rina.onepop.club.api.setting.value.ValueBoolean;
import rina.onepop.club.api.setting.value.ValueEnum;
import rina.onepop.club.api.setting.value.ValueNumber;
import rina.onepop.club.api.util.item.SlotUtil;
import rina.onepop.club.api.util.math.PositionUtil;
import rina.onepop.club.api.util.math.RotationUtil;
import rina.onepop.club.api.util.world.BlockUtil;
import rina.onepop.club.api.util.world.BlocksUtil;
import rina.onepop.club.api.util.world.CrystalUtil;
import rina.onepop.club.client.event.client.RunTickEvent;
import rina.onepop.club.client.manager.network.Rotation;
import rina.onepop.club.client.manager.network.RotationManager;
import rina.onepop.club.client.module.combat.autocrystalrewrite.ModuleAutoCrystalRewrite;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

/**
 * @author SrRina
 * @since 02/10/2021 at 11:34am
 **/
@Registry(name = "Obsidian Place", tag = "ObsidianPlace", description = "Place obsidian for Auto-Crystal.", category = ModuleCategory.COMBAT)
public class ModuleObsidianPlace extends Module {
    // Misc.
    public static ValueBoolean settingRenderSwing = new ValueBoolean("Render Swing", "RenderSwing", "Render place swing.", true);
    public static ValueBoolean settingAirPlace = new ValueBoolean("Air Place", "AirPlace", "Option for air place.", false);
    public static ValueNumber settingDelay = new ValueNumber("Delay", "Delay", "Delay for place.", 2f, 0f, 10f);
    public static ValueNumber settingPlaceRange = new ValueNumber("Place Range", "PlaceRange", "Sets place range.", 2f, 2f, 6f);
    public static ValueNumber settingSelfDamage = new ValueNumber("Self Damage", "SelfDamage", "Sets self damage.", 8, 0, 36);
    public static ValueEnum settingRotate = new ValueEnum("Rotation", "Rotation", "Rotation.", Rotation.REL);

    private final TurokTick delay = new TurokTick();

    private boolean withOffhand;
    private int obsidianSlot;

    @Listener
    public void onRunTickEvent(RunTickEvent event) {
        if (!ModuleAutoCrystalRewrite.INSTANCE.isEnabled()) {
            return;
        }

        final EntityPlayer entity = ModuleAutoCrystalRewrite.INSTANCE.getEntity();

        if (entity == null) {
            return;
        }

        this.obsidianSlot = SlotUtil.findItemSlotFromHotBar(Item.getItemFromBlock(Blocks.OBSIDIAN));
        this.withOffhand = mc.player.getHeldItemOffhand().getItem() == Item.getItemFromBlock(Blocks.OBSIDIAN);

        if (this.obsidianSlot == -1 && !this.withOffhand) {
            return;
        }

        if (delay.isPassedMS(settingDelay.getValue().floatValue() * 10f)) {
            BlockPos position = this.findObsidianPlace(entity);

            if (position != null) {
                this.doPlace(position);

                delay.reset();
            }
        }
    }

    public boolean doPlace(BlockPos place) {
        boolean state = false;

        for (EnumFacing faces : EnumFacing.values()) {
            final BlockPos offset = place.offset(faces);
            final Block block = mc.world.getBlockState(offset).getBlock();

            if (block != Blocks.AIR || settingAirPlace.getValue()) {
                final EnumFacing facing = faces.getOpposite();
                final Vec3d hit = PositionUtil.calculateHitPlace(offset, facing);

                // I removed the facing, maybe it makes the place slow...?
                float facingX = 0.5f;
                float facingY = 0.5f;
                float facingZ = 0.5f;

                boolean flagSneak = BlockUtil.BLACK_LIST.contains(block);

                if (!this.withOffhand) {
                    mc.player.connection.sendPacket(new CPacketHeldItemChange(this.obsidianSlot));
                }

                float[] rotates = RotationUtil.getPlaceRotation(hit);

                // Send task!
                RotationManager.task(settingRotate.getValue(), rotates);

                EnumHand hand = this.withOffhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND;

                if (flagSneak) {
                    mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
                }

                mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(offset, facing, hand, facingX, facingY, facingZ));

                if (settingRenderSwing.getValue()) {
                    mc.player.swingArm(hand);
                } else {
                    mc.player.connection.sendPacket(new CPacketAnimation(hand));
                }

                if (flagSneak) {
                    mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
                }

                if (!this.withOffhand) {
                    mc.player.connection.sendPacket(new CPacketHeldItemChange(mc.player.inventory.currentItem));
                }

                state = true;

                break;
            }
        }

        return state;
    }

    public BlockPos findObsidianPlace(EntityPlayer target) {
        BlockPos position = null;
        float damage = 0.5f;

        for (BlockPos places : BlocksUtil.getSphereList(settingPlaceRange.getValue().floatValue())) {
            if (BlockUtil.getBlock(places) == Blocks.OBSIDIAN && BlockUtil.isAir(places.up()) && (settingAirPlace.getValue() || BlockUtil.isAir(places.up()))) {
                float targetDamage = CrystalUtil.calculateDamage(places, target);

                if (targetDamage > damage) {
                    position = null;

                    break;
                }
            }

            if (BlockUtil.isPlaceableExcludingBlackListCustomMask(places, BlockUtil.MASK_FACING_NON_UP) && BlockUtil.isAir(places.add(0, 1, 0)) && (settingAirPlace.getValue() || BlockUtil.isAir(places.add(0, 2, 0)))) {
                float selfDamage = CrystalUtil.calculateDamage(places.down(), mc.player);
                float targetDamage = CrystalUtil.calculateDamage(places.down(), target);

                if (targetDamage > damage && selfDamage < settingSelfDamage.getValue().floatValue()) {
                    damage = targetDamage;
                    position = places;
                }
            }
        }

        return position;
    }

    public boolean isPlaceable(BlockPos position) {
        return !BlockUtil.isAir(position.add(0, -1, 0)) ||
               !BlockUtil.isAir(position.add(1, 0, 0))  ||
               !BlockUtil.isAir(position.add(-1, 0, 0)) ||
               !BlockUtil.isAir(position.add(0, 0, 1))  ||
               !BlockUtil.isAir(position.add(0, 0, -1));
    }

    public boolean isCrystalPlaceable(BlockPos position, boolean newMinecraftVersion) {
        final BlockPos boostY = position.add(0, 1, 0);
        final BlockPos boostYY = position.add(0, 2, 0);
        final BlockPos boostYYY = position.add(0, 3, 0);

        try {
            if (mc.world.getBlockState(position).getBlock() != Blocks.BEDROCK && mc.world.getBlockState(position).getBlock() != Blocks.OBSIDIAN) {
                return false;
            }

            if ((mc.world.getBlockState(boostY).getBlock() != Blocks.AIR || (mc.world.getBlockState(boostYY).getBlock() != Blocks.AIR && !newMinecraftVersion))) {
                return false;
            }

            for (final Object entity : mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boostY))) {
                if (!(entity instanceof EntityEnderCrystal)) {
                    return false;
                }
            }

            for (final Object entity : mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boostYY))) {
                if (!(entity instanceof EntityEnderCrystal)) {
                    return false;
                }
            }

            for (final Object entity : mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boostYYY))) {
                if (entity instanceof EntityEnderCrystal) {
                    return false;
                }
            }
        } catch (Exception ignored) {
            return false;
        }

        return true;
    }
}
