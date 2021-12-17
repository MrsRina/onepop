package rina.onepop.club.api.util.world;

import rina.onepop.club.api.util.math.PositionUtil;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.CombatRules;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Explosion;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ??
 * @since ??
 **/
public class CrystalUtil {
    final static Minecraft mc = Minecraft.getMinecraft();

    /**
     * Verify self damage.
     *
     * @param position - The position requested.
     * @param suicide - Preserve suicide.
     * @return - True if we get a lot self damage, else False.
     */
    public static boolean isSelfDamage(EntityPlayer target, BlockPos position, boolean suicide, int damage) {
        if (suicide) {
            return false;
        }

        float k = calculateDamage(position.x, position.y, position.z, target);
        float self = calculateDamage(position.x, position.y, position.z, mc.player);

        return ((self > k && !(k < target.getHealth())) || self - 0.5 > damage);
    }

    public static boolean isFacePlace(BlockPos pos, EntityPlayer target) {
        boolean moment = false;

        BlockPos targetPos = new BlockPos(Math.floor(target.posX), Math.floor(target.posY), Math.floor(target.posZ));

        for (BlockPos add : BlockUtil.FULL_BLOCK_ADD) {
            BlockPos added = pos.add(add);

            if (PositionUtil.collideBlockPos(added, targetPos.down()) || PositionUtil.collideBlockPos(added, targetPos.up())) {
                continue;
            }

            if (PositionUtil.collideBlockPos(added, targetPos)) {
                moment = true;

                break;
            }
        }

        return moment;
    }

    public static List<BlockPos> getSphereCrystalPlace(final float range, final boolean thirteen, final boolean specialEntityCheck) {
        final BlockPos selfPos = new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ);
        final List<BlockPos> list = new ArrayList<>();

        for (int x = (int) (selfPos.x - range); x <= selfPos.x + range; x++) {
            for (int z = (int) (selfPos.z - range); z <= selfPos.z + range; z++) {
                for (int y = (int) (selfPos.y - range); y <= selfPos.y + range; y++) {
                    double dist = (selfPos.x - x) * (selfPos.x - x) + (selfPos.z - z) * (selfPos.z - z) + (selfPos.y - y) * (selfPos.y - y);

                    if (dist < range * range) {
                        BlockPos block = new BlockPos(x, y, z);

                        if (isCrystalPlaceable(block, thirteen, specialEntityCheck)) {
                            list.add(block);
                        }
                    }
                }
            }
        }

        return list;
    }

    public static boolean isCrystalPlaceable(final BlockPos blockPos, final boolean thirteen, final boolean specialEntityCheck) {
        final BlockPos boostY = blockPos.add(0, 1, 0);
        final BlockPos boostYY = blockPos.add(0, 2, 0);
        final BlockPos boostYYY = blockPos.add(0, 3, 0);

        try {
            if (mc.world.getBlockState(blockPos).getBlock() != Blocks.BEDROCK && mc.world.getBlockState(blockPos).getBlock() != Blocks.OBSIDIAN) {
                return false;
            }

            if ((mc.world.getBlockState(boostY).getBlock() != Blocks.AIR || (mc.world.getBlockState(boostYY).getBlock() != Blocks.AIR && !thirteen))) {
                return false;
            }

            if (!specialEntityCheck) {
                return mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boostY)).isEmpty() && mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boostYY)).isEmpty();
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

    public static boolean isCrystalPlaceable(final BlockPos pos) {
        final Block block = mc.world.getBlockState(pos).getBlock();

        if (block == Blocks.OBSIDIAN || block == Blocks.BEDROCK) {
            final Block floor = mc.world.getBlockState(pos.add(0, 1, 0)).getBlock();
            final Block ceil = mc.world.getBlockState(pos.add(0, 2, 0)).getBlock();

            if (floor == Blocks.AIR && ceil == Blocks.AIR) {
                return mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos.add(0, 1, 0))).isEmpty() && mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos.add(0, 2, 0))).isEmpty();
            }
        }

        return false;
    }

    private float calculate(final BlockPos pos, final EntityPlayer player) {
        return calculate(pos.getX() + 0.5F, pos.getY() + 1, pos.getZ() + 0.5F, player);
    }

    public static float calculate(final double x, final double y, final double z, final EntityLivingBase base) {
        double distance = base.getDistance(x, y, z) / 12.0;
        if (distance > 1.0) {
            return 0.0F;
        } else {
            final double densityDistance = distance = (1.0 - distance) * mc.world.getBlockDensity(new Vec3d(x, y, z), base.getEntityBoundingBox());

            float damage = getDifficultyMultiplier((float) ((densityDistance * densityDistance + distance) / 2.0D * 7.0D * 12.0D + 1.0D));
            final DamageSource damageSource = DamageSource.causeExplosionDamage(new Explosion(mc.world, mc.player, x, y, z, 6.0F, false, true));

            try {
                damage = CombatRules.getDamageAfterAbsorb(damage, base.getTotalArmorValue(), (float) base.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());
            } catch (Exception ignore) {}

            final int modifierDamage = EnchantmentHelper.getEnchantmentModifierDamage(base.getArmorInventoryList(), damageSource);

            if (modifierDamage > 0) {
                damage = CombatRules.getDamageAfterMagicAbsorb(damage, modifierDamage);
            }

            final PotionEffect resistance = base.getActivePotionEffect(MobEffects.RESISTANCE);
            if (resistance != null) {
                damage = damage * (25 - (resistance.getAmplifier() + 1) * 5) / 25.0F;
            }

            return Math.max(damage, 0.0F);
        }
    }

    public static float getDifficultyMultiplier(float distance) {
        switch (mc.world.getDifficulty()) {
            case PEACEFUL:
                return 0.0F;
            case EASY:
                return Math.min(distance / 2.0F + 1.0F, distance);
            case HARD:
                return distance * 3.0F / 2.0F;
        }

        return distance;
    }

    public static float calculateDamage(BlockPos pos, Entity entity) {
        return calculateDamage(pos.x + 0.5f, pos.y + 1, pos.z + 0.5, entity);
    }

    public static float calculateDamage(double posX, double posY, double posZ, Entity entity) {
        final float doubleExplosionSize = 12.0f;
        final double distancedsize = entity.getDistance(posX, posY, posZ) / doubleExplosionSize;
        final Vec3d vec3d = new Vec3d(posX, posY, posZ);
        double blockDensity = 0.0;

        try {
            blockDensity = entity.world.getBlockDensity(vec3d, entity.getEntityBoundingBox());
        } catch (Exception ignore) {}

        final double v = (1.0 - distancedsize) * blockDensity;
        final float damage = (float)(int)((v * v + v) / 2.0 * 7.0 * doubleExplosionSize + 1.0);

        double finald = 1.0;

        if (entity instanceof EntityLivingBase) {
            finald = getBlastReduction((EntityLivingBase) entity, getDamageMultiplied(damage), new Explosion(mc.world, null, posX, posY, posZ, 6.0f, false, true));
        }

        return (float)finald;
    }

    public static float getBlastReduction(final EntityLivingBase entity, final float damageI, final Explosion explosion) {
        float damage = damageI;

        if (entity instanceof EntityPlayer) {
            final EntityPlayer ep = (EntityPlayer)entity;
            final DamageSource ds = DamageSource.causeExplosionDamage(explosion);

            damage = CombatRules.getDamageAfterAbsorb(damage, (float)ep.getTotalArmorValue(), (float)ep.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());

            int k = 0;

            try {
                k = EnchantmentHelper.getEnchantmentModifierDamage(ep.getArmorInventoryList(), ds);
            } catch (Exception ignored) {}

            final float f = MathHelper.clamp((float)k, 0.0f, 20.0f);

            damage *= 1.0f - f / 25.0f;

            if (entity.isPotionActive(MobEffects.RESISTANCE)) {
                damage -= damage / 4.0f;
            }

            damage = Math.max(damage, 0.0f);

            return damage;
        }

        damage = CombatRules.getDamageAfterAbsorb(damage, (float)entity.getTotalArmorValue(), (float)entity.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());

        return damage;
    }

    public static float getDamageMultiplied(final float damage) {
        final int diff = mc.world.getDifficulty().getId();

        return damage * ((diff == 0) ? 0.0f : ((diff == 2) ? 1.0f : ((diff == 1) ? 0.5f : 1.5f)));
    }

    public static float calculateDamage(EntityEnderCrystal crystal, Entity entity) {
        return calculateDamage(crystal.posX, crystal.posY, crystal.posZ, entity);
    }
}