package rina.onepop.club.api.util.entity;

import me.rina.turok.util.TurokMath;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSkull;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSword;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import rina.onepop.club.Onepop;
import rina.onepop.club.api.social.Social;
import rina.onepop.club.api.social.management.SocialManager;
import rina.onepop.club.api.social.type.SocialType;
import rina.onepop.club.api.util.world.BlockUtil;
import rina.onepop.club.api.util.world.BlocksUtil;
import rina.onepop.club.client.module.client.ModuleGeneral;

import java.awt.*;

/**
 * @author SrRina
 * @since 15/02/2021 at 18:11
 **/
public class EntityUtil {
    public static BlockPos getEntityFlooredPosition(Entity entity, double offset) {
        return new BlockPos(Math.floor(entity.posX) + offset, Math.floor(entity.posY) + offset, Math.floor(entity.posZ) + offset);
    }

    public static boolean isEntityBurrowed(final Entity entity) {
        final BlockPos position = getEntityFlooredPosition(entity, 0.5d);
        final Block block = BlockUtil.getBlock(position);

        return BlockUtil.getHardness(position) != -1 && (block == Blocks.OBSIDIAN || block == Blocks.ENDER_CHEST || block instanceof BlockSkull);
    }

    public static BlockPos getFlooredEntityPosition(final Entity entity) {
        return new BlockPos(Math.floor(entity.posX), entity.posY, Math.floor(entity.posZ));
    }

    public static boolean heldSword(final EntityPlayer player) {
        return player != null && (player.getHeldItemOffhand().getItem() instanceof ItemSword || player.getHeldItemMainhand().getItem() instanceof ItemSword);
    }

    public static double getDistance(final Entity entity) {
        return getDistance(entity, Onepop.MC.player);
    }

    public static double getDistance(final Entity entityOne, final Entity entityTwo) {
        final BlockPos flooredEntityOnePosition = new BlockPos(Math.floor(entityOne.posX), Math.floor(entityOne.posY), Math.floor(entityOne.posZ));
        final BlockPos flooredEntityTwoPosition = new BlockPos(Math.floor(entityTwo.posX), Math.floor(entityTwo.posY), Math.floor(entityTwo.posZ));

        if (flooredEntityTwoPosition.getY() == flooredEntityOnePosition.getY()) {
            final Vec3d realVecPosition = new Vec3d(entityOne.posX, flooredEntityTwoPosition.y, entityOne.posZ);

            return entityTwo.getDistance(realVecPosition.x, realVecPosition.y, realVecPosition.z);
        }

        if (flooredEntityTwoPosition.getY() != flooredEntityOnePosition.getY() && flooredEntityTwoPosition.up().getY() == flooredEntityOnePosition.y) {
            final Vec3d realVecPosition = new Vec3d(entityOne.posX, flooredEntityTwoPosition.y, entityOne.posZ);

            return entityTwo.getDistance(realVecPosition.x, realVecPosition.y, realVecPosition.z);
        }

        return entityTwo.getDistance(entityOne);
    }

    public static double getDistance(final Vec3d one, final Vec3d two) {
        final BlockPos flooredEntityOnePosition = new BlockPos(Math.floor(one.x), Math.floor(one.y), Math.floor(one.z));
        final BlockPos flooredEntityTwoPosition = new BlockPos(Math.floor(two.x), Math.floor(two.y), Math.floor(two.z));

        if (flooredEntityTwoPosition.getY() == flooredEntityOnePosition.getY()) {
            final Vec3d realVecPosition = new Vec3d(one.x, flooredEntityTwoPosition.y, one.z);

            return TurokMath.getDistance(two, realVecPosition.x, realVecPosition.y, realVecPosition.z);
        }

        if (flooredEntityTwoPosition.getY() != flooredEntityOnePosition.getY() && flooredEntityTwoPosition.up().getY() == flooredEntityOnePosition.y) {
            final Vec3d realVecPosition = new Vec3d(one.x, flooredEntityTwoPosition.y, one.z);

            return TurokMath.getDistance(two, realVecPosition.x, realVecPosition.y, realVecPosition.z);
        }

        return TurokMath.getDistance(two, one);
    }

    public static boolean isEntityPlayerSurrounded(EntityPlayer entityPlayer) {
        for (BlockPos add : BlocksUtil.FULL_SURROUND) {
            final BlockPos offset = new BlockPos(Math.floor(entityPlayer.posX), Math.floor(entityPlayer.posY), Math.floor(entityPlayer.posZ)).add(add);

            if (BlockUtil.isAir(offset)) {
                return false;
            }
        }

        return true;
    }

    public static boolean isEntityPlayerNaked(EntityPlayer entityPlayer) {
        boolean isPlayer = true;

        for (int i = 0; i < 4; i++) {
            Item item = entityPlayer.inventory.armorInventory.get(i).getItem();

            if (item != Items.AIR) {
                isPlayer = false;

                break;
            }
        }

        return isPlayer;
    }

    public static Vec3d eye(Entity entity) {
        return new Vec3d(entity.posX, entity.posY + entity.getEyeHeight(), entity.posZ);
    }

    public static EntityPlayer getTarget(float range, boolean unsafe, boolean naked) {
        final Minecraft mc = Onepop.getMinecraft();

        /* The target. */
        EntityPlayer target = null;
        float distance = range;

        for (EntityPlayer entities : mc.world.playerEntities) {
            if (entities.isDead || entities.getHealth() < 0 || entities == mc.player || (SocialManager.get(entities.getName()) != null && SocialManager.get(entities.getName()).getType() == SocialType.FRIEND)) {
                continue;
            }

            if (mc.player.getDistance(entities) > range) {
                continue;
            }

            if (SocialManager.get(entities.getName()) != null && SocialManager.get(entities.getName()).getType() == SocialType.ENEMY) {
                return entities;
            }

            if (naked && isEntityPlayerNaked(entities)) {
                continue;
            }

            boolean flag = isEntityPlayerSurrounded(entities);
            float diff = mc.player.getDistance(entities);

            if (unsafe) {
                if (!flag) {
                    return entities;
                }
            }

            if (diff < distance) {
                target = entities;
                distance = diff;
            }
        }

        return target;
    }

    public static Color getColor(EntityPlayer player, Color color) {
        Social user = SocialManager.get(player.getName());

        if (user != null && user.getType() == SocialType.FRIEND) {
            return ModuleGeneral.settingFriendColor.getColor(255);
        }

        return color;
    }

    public static Vec3d getInterpolatedLinearVec(Entity entity, float ticks) {
        return new Vec3d(
                TurokMath.lerp(entity.lastTickPosX, entity.posX, ticks),
                TurokMath.lerp(entity.lastTickPosY, entity.posY, ticks),
                TurokMath.lerp(entity.lastTickPosZ, entity.posZ, ticks)
        );
    }
}
