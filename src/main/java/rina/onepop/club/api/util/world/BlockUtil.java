package rina.onepop.club.api.util.world;

import me.rina.turok.util.TurokMath;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import rina.onepop.club.Onepop;
import rina.onepop.club.client.manager.world.HoleManager;

import java.util.Arrays;
import java.util.List;

/**
 * @author SrRina
 * @since 08/02/2021 at 14:42
 **/
public class BlockUtil {
    public static float[] ONE_BLOCK_HEIGHT = {
            0.42f, 0.75f
    };

    public static float[] TWO_BLOCKS_HEIGHT = {
            0.4f, 0.75f, 0.5f, 0.41f, 0.83f, 1.16f, 1.41f, 1.57f, 1.58f, 1.42f
    };

    public static float[] THREE_BLOCKS_HEIGHT = {
            0.42f, 0.78f, 0.63f, 0.51f, 0.9f, 1.21f, 1.45f, 1.43f, 1.78f, 1.63f, 1.51f, 1.9f, 2.21f, 2.45f, 2.43f
    };

    public static float[] FOUR_BLOCKS_HEIGHT = {
            0.42f, 0.78f, 0.63f, 0.51f, 0.9f, 1.21f, 1.45f, 1.43f, 1.78f, 1.63f, 1.51f, 1.9f, 2.21f, 2.45f, 2.43f, 2.78f, 2.63f, 2.51f, 2.9f, 3.21f, 3.45f, 3.43f
    };

    public static final BlockPos[] HOLE = new BlockPos[] {
        new BlockPos(1, 0, 0),
        new BlockPos(-1, 0, 0),
        new BlockPos(0, 0, 1),
        new BlockPos(0, 0, -1)
    };

    public static final BlockPos[] FULL_BLOCK_ADD = {
            new BlockPos(0, -1, 0),
            new BlockPos(0, 1, 0),
            new BlockPos(1, 0, 0),
            new BlockPos(-1, 0, 0),
            new BlockPos(0, 0, 1),
            new BlockPos(0, 0, -1)
    };

    public static final List<Block> BLACK_LIST = Arrays.asList(
            Blocks.ENDER_CHEST,
            Blocks.CHEST,
            Blocks.TRAPPED_CHEST,
            Blocks.CRAFTING_TABLE,
            Blocks.ANVIL,
            Blocks.BREWING_STAND,
            Blocks.HOPPER,
            Blocks.DROPPER,
            Blocks.DISPENSER,
            Blocks.TRAPDOOR,
            Blocks.ENCHANTING_TABLE
    );

    public static final List<Block> SHULKER_LIST = Arrays.asList(
            Blocks.WHITE_SHULKER_BOX,
            Blocks.ORANGE_SHULKER_BOX,
            Blocks.MAGENTA_SHULKER_BOX,
            Blocks.LIGHT_BLUE_SHULKER_BOX,
            Blocks.YELLOW_SHULKER_BOX,
            Blocks.LIME_SHULKER_BOX,
            Blocks.PINK_SHULKER_BOX,
            Blocks.GRAY_SHULKER_BOX,
            Blocks.SILVER_SHULKER_BOX,
            Blocks.CYAN_SHULKER_BOX,
            Blocks.PURPLE_SHULKER_BOX,
            Blocks.BLUE_SHULKER_BOX,
            Blocks.BROWN_SHULKER_BOX,
            Blocks.GREEN_SHULKER_BOX,
            Blocks.RED_SHULKER_BOX,
            Blocks.BLACK_SHULKER_BOX
    );

    public static final List<Material> REPLACEABLE_LIST = Arrays.asList(
        Material.WATER,
        Material.LAVA,
        Material.PLANTS,
        Material.FIRE,
        Material.CIRCUITS,
        Material.GRASS,
        Material.PACKED_ICE,
        Material.SNOW
    );

    public static final EnumFacing[] MASK_FACING_HOLE = {
            EnumFacing.WEST, EnumFacing.EAST, EnumFacing.NORTH, EnumFacing.SOUTH
    };

    public static final EnumFacing[] MASK_FACING_NON_UP = {
        EnumFacing.WEST, EnumFacing.EAST, EnumFacing.NORTH, EnumFacing.SOUTH, EnumFacing.DOWN
    };

    /**
     * We create a custom block damage to help at some modules.
     */
    public static class BlockDamage {
        private BlockPos pos;
        private EnumFacing facing;

        public BlockDamage(BlockPos pos, EnumFacing facing) {
            this.pos = pos;
            this.facing = facing;
        }

        public BlockPos getPos() {
            return pos;
        }

        public EnumFacing getFacing() {
            return facing;
        }
    }

    public static boolean isBreakable(BlockPos pos) {
        return !isAir(pos) && getHardness(pos) != -1;
    }

    public static HoleManager.Hole isHole(final BlockPos position) {
        if (!isSafeOrUnsafeExcludingEntity(position, new BlockPos(0, -1, 0)) || !(isAir(position.add(0, 1, 0)) && isAir(position.add(0, 2, 0)))) {
            return null;
        }

        HoleManager.Hole hole = null;

        if (isSafeOrUnsafeExcludingEntity(position, BlockUtil.HOLE[0]) &&
            isSafeOrUnsafeExcludingEntity(position, BlockUtil.HOLE[1]) &&
            isSafeOrUnsafeExcludingEntity(position, BlockUtil.HOLE[2]) &&
            isSafeOrUnsafeExcludingEntity(position, HOLE[3])) {

            hole = new HoleManager.Hole(position, getHoleType(position),null);
        } else {
            EnumFacing facing = null;
            boolean found = false;

            for (EnumFacing faces : MASK_FACING_HOLE) {
                final BlockPos offset = position.offset(faces);

                if ((!isSafeOrUnsafeExcludingEntity(offset, new BlockPos(0, -1, 0)) || !isAir(offset)) && !found) {
                    continue;
                }

                found = true;

                if (isDoubleHoleExcludingEntityByFacing(position, offset, faces) && isDoubleHoleExcludingEntityByFacing(position, position, faces.getOpposite())) {
                    facing = faces;

                    break;
                }
            }

            if (facing != null) {
                hole = new HoleManager.Hole(position, HoleManager.UNSAFE, facing);
            }
        }

        return hole;
    }

    public static boolean isDoubleHoleExcludingEntityByFacing(BlockPos origin, BlockPos added, EnumFacing facing) {
        return (isSafeOrUnsafeExcludingEntityByFacing(added, MASK_FACING_HOLE[0]) || MASK_FACING_HOLE[0] == facing.getOpposite()) &&
               (isSafeOrUnsafeExcludingEntityByFacing(added, MASK_FACING_HOLE[1]) || MASK_FACING_HOLE[1] == facing.getOpposite()) &&
               (isSafeOrUnsafeExcludingEntityByFacing(added, MASK_FACING_HOLE[2]) || MASK_FACING_HOLE[2] == facing.getOpposite()) &&
               (isSafeOrUnsafeExcludingEntityByFacing(added, MASK_FACING_HOLE[3]) || MASK_FACING_HOLE[3] == facing.getOpposite());
    }

    public static boolean isCrystalPlaceableNonExcludingEntity(BlockPos position) {
        final Block block = getBlock(position);

        return block == Blocks.OBSIDIAN || block == Blocks.BEDROCK;
    }

    public static boolean isSafeOrUnsafeExcludingEntity(BlockPos position, BlockPos add) {
        final Block block = getBlock(position.add(add));

        return block == Blocks.OBSIDIAN || block == Blocks.BEDROCK || block == Blocks.ENDER_CHEST;
    }

    public static boolean isSafeOrUnsafeExcludingEntityByFacing(BlockPos position, EnumFacing add) {
        final Block block = getBlock(position.offset(add));

        return block == Blocks.OBSIDIAN || block == Blocks.BEDROCK || block == Blocks.ENDER_CHEST;
    }

    public static boolean isSafeOrUnsafeExcludingEntitySpecifyBlock(BlockPos position, BlockPos add, Block blockClass) {
        final Block block = getBlock(position.add(add));

        return block == blockClass;
    }

    public static boolean isSafeOrUnsafeExcludingEntitySpecifyBlockByFacing(BlockPos position, EnumFacing face, Block blockClass) {
        final Block block = getBlock(position.offset(face));

        return block == blockClass;
    }

    public static int getHoleType(BlockPos position) {
        return (
                isSafeOrUnsafeExcludingEntitySpecifyBlock(position, HOLE[0], Blocks.BEDROCK) &&
                isSafeOrUnsafeExcludingEntitySpecifyBlock(position, HOLE[1], Blocks.BEDROCK) &&
                isSafeOrUnsafeExcludingEntitySpecifyBlock(position, HOLE[2], Blocks.BEDROCK) &&
                isSafeOrUnsafeExcludingEntitySpecifyBlock(position, HOLE[3], Blocks.BEDROCK)) ? HoleManager.SAFE : HoleManager.UNSAFE;
    }

    /**
     * Verify if an position in world is placeable
     *
     * @param position Position to verify
     * @return True if is placeable with no entity, else False
     */
    public static boolean isPlaceableExcludingBlackListAndEntity(BlockPos position) {
        boolean yes = false;

        for (EnumFacing faces : EnumFacing.values()) {
            BlockPos offset = position.offset(faces);

            if (!isAir(offset)) {
                yes = true;

                break;
            }
        }

        return yes;
    }

    public static boolean isReplaceableAndNotLiquid(BlockPos pos) {
        final IBlockState state = getState(pos);

        return state.getMaterial().isReplaceable() && !state.getMaterial().isLiquid();
    }

    public static boolean isReplaceable(BlockPos pos) {
        final IBlockState state = getState(pos);

        return state.getMaterial().isReplaceable();
    }

    public static boolean isPlaceableExcludingEntity(BlockPos position) {
        boolean yes = false;

        for (EnumFacing faces : EnumFacing.values()) {
            BlockPos offset = position.offset(faces);

            if (!isAir(offset) && !BLACK_LIST.contains(getBlock(offset))) {
                yes = true;

                break;
            }
        }

        return yes;
    }

    public static boolean isPlaceableExcludingBlackList(BlockPos position) {
        boolean yes = false;

        for (EnumFacing faces : EnumFacing.values()) {
            BlockPos offset = position.offset(faces);

            if (!isAir(offset)) {
                yes = true;

                break;
            }
        }

        return yes && !isEntityOver(position);
    }

    public static boolean isPlaceable(BlockPos position) {
        boolean yes = false;

        for (EnumFacing faces : EnumFacing.values()) {
            BlockPos offset = position.offset(faces);

            if (!isAir(offset) && !BLACK_LIST.contains(getBlock(offset))) {
                yes = true;

                break;
            }
        }

        return yes && !isEntityOver(position);
    }

    public static boolean isPlaceableExcludingBlackListCustomMask(BlockPos position, EnumFacing[] mask) {
        boolean yes = false;

        for (EnumFacing faces : mask) {
            BlockPos offset = position.offset(faces);

            if (!isAir(offset)) {
                yes = true;

                break;
            }
        }

        return yes && !isEntityOver(position);
    }

    public static boolean isPlaceableExcludingBlackListCustomMask(BlockPos position, BlockPos[] mask) {
        boolean yes = false;

        for (BlockPos add : mask) {
            BlockPos added = position.add(add);

            if (!isAir(added)) {
                yes = true;

                break;
            }
        }

        return yes && !isEntityOver(position);
    }

    public static boolean isPlaceableCustomMask(BlockPos position, EnumFacing[] mask) {
        boolean yes = false;

        for (EnumFacing faces : mask) {
            BlockPos offset = position.offset(faces);

            if (!isAir(offset)) {
                yes = true;

                break;
            }
        }

        return yes;
    }

    public static boolean isPlaceableCustomMask(BlockPos position, BlockPos[] mask) {
        boolean yes = false;

        for (BlockPos add : mask) {
            BlockPos added = position.add(add);

            if (!isAir(added) && !BLACK_LIST.contains(getBlock(added))) {
                yes = true;

                break;
            }
        }

        return yes && !isEntityOver(position);
    }

    public static boolean isEntityOver(BlockPos pos) {
        final List<Entity> sizeOne = Onepop.MC.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos));

        boolean isOver = false;

        if (sizeOne.isEmpty()) {
            return false;
        }

        for (final Object entity : sizeOne) {
            if (!(entity instanceof EntityItem)) {
                isOver = true;

                break;
            }
        }

        final List<Entity> sizeTwo = Onepop.MC.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos.up()));

        if (sizeTwo.isEmpty()) {
            return false;
        }

        for (final Object entity : sizeTwo) {
            if (!(entity instanceof EntityItem)) {
                isOver = true;

                break;
            }
        }

        return isOver;
    }

    public static EnumFacing getBedPlaceableFaces(BlockPos pos, boolean airPlace) {
        final Block blockMain = getBlock(pos);
        final Block blockMainUp = getBlock(pos.up());

        final boolean bed = blockMainUp == Blocks.BED;

        if ((blockMainUp != Blocks.AIR && !bed) || REPLACEABLE_LIST.contains(getState(pos).getMaterial()) || REPLACEABLE_LIST.contains(getState(pos.up()).getMaterial()) || BLACK_LIST.contains(blockMain) || BLACK_LIST.contains(blockMainUp)) {
            return null;
        }

        EnumFacing facing = null;

        for (EnumFacing faces : EnumFacing.values()) {
            if (faces == EnumFacing.UP || faces == EnumFacing.DOWN) {
                continue;
            }

            final BlockPos offset = pos.offset(faces);

            final Block blockOffset = getBlock(offset);
            final Block blockOffsetUp = getBlock(offset.up());

            if (blockOffsetUp == Blocks.BED && bed) {
                return faces;
            }

            if (bed) {
                continue;
            }

            if (BLACK_LIST.contains(blockOffset) || BLACK_LIST.contains(blockOffsetUp)) {
                continue;
            }

            if (((blockOffset == Blocks.AIR || blockOffset == Blocks.BED) && !airPlace) || REPLACEABLE_LIST.contains(getState(offset).getMaterial())) {
                continue;
            }

            if ((blockOffsetUp != Blocks.AIR && !getState(offset.up()).getMaterial().isLiquid()) || getState(offset.up()).getMaterial() == Material.FIRE) {
                continue;
            }

            facing = faces;

            break;
        }

        return facing;
    }
    
    public static Block getBlock(BlockPos pos) {
        return Onepop.MC.world.getBlockState(pos).getBlock();
    }

    /**
     * For all air blocks type, the tall grass and snow is considered air.
     *
     * @param pos
     * @return
     */
    public static boolean isAir(BlockPos pos) {
        IBlockState state = Onepop.MC.world.getBlockState(pos);

        return state.getBlock() == Blocks.AIR || state.getMaterial().isReplaceable();
    }

    public static boolean isUnbreakable(BlockPos pos) {
        IBlockState blockState = Onepop.MC.world.getBlockState(pos);

        return Onepop.MC.world.getBlockState(pos).getBlockHardness(Onepop.MC.world, pos) == -1;
    }

    public static float getHardness(BlockPos pos) {
        IBlockState blockState = Onepop.MC.world.getBlockState(pos);

        return blockState.getBlockHardness(Onepop.MC.world, pos);
    }

    public static IBlockState getState(BlockPos pos) {
        return Onepop.MC.world.getBlockState(pos);
    }

    public static int getDistanceI(BlockPos pos, Entity entity) {
        int x = (int) (pos.x - entity.posX);
        int y = (int) (pos.y - entity.posY);
        int z = (int) (pos.z - entity.posZ);

        return TurokMath.sqrt(x * x + y * y + z * z);
    }

    public static double getDistanceD(BlockPos pos, Entity entity) {
        double x = (pos.x - entity.posX);
        double y = (pos.y - entity.posY);
        double z = (pos.z - entity.posZ);

        return TurokMath.sqrt(x * x + y * y + z * z);
    }

    public static EnumFacing getSideFacing(BlockPos pos, EntityLivingBase entityLivingBase) {
        return EnumFacing.getDirectionFromEntityLiving(pos, entityLivingBase);
    }

    public static EnumFacing getPlaceableFacing(BlockPos position) {
        EnumFacing facing  = null;

        boolean flag = false;

        for (EnumFacing faces : EnumFacing.values()) {
            if (faces == EnumFacing.UP) {
                continue;
            }

            final BlockPos offset = position.offset(faces);

            if (BlockUtil.isAir(offset)) {
                continue;
            }

            facing = faces;

            if (BLACK_LIST.contains(BlockUtil.getBlock(offset))) {
                flag = true;
            } else {
                if (flag) {
                    break;
                }
            }

        }

        return facing;
    }

    public static EnumFacing getFacing(BlockPos pos, EntityLivingBase entityLivingBase) {
        Vec3d eye = entityLivingBase.getPositionEyes(1f);

        float x = (float) eye.x - (pos.x + 0.5f);
        float y = (float) eye.y - (pos.y + 0.5f);
        float z = (float) eye.z - (pos.z + 0.5f);

        return EnumFacing.getFacingFromVector(x, y, z);
    }
}