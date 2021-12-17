package rina.onepop.club.client.manager.world;

import me.rina.turok.util.TurokTick;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import rina.onepop.club.api.manager.Manager;
import rina.onepop.club.api.util.entity.PlayerUtil;
import rina.onepop.club.api.util.world.BlockUtil;
import rina.onepop.club.api.util.world.BlocksUtil;
import rina.onepop.club.client.event.network.PacketEvent;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * @author SrRina
 * @since 14/04/2021 at 23:17
 **/
public class BlockManager extends Manager {
    public static BlockManager INSTANCE;

    private final ArrayList<BlockPos> playerSurroundBlockList = new ArrayList<>();
    private World world;

    private final TurokTick crystalStamp = new TurokTick();

    private int crystalAmount;
    private int lastCrystalAmount;

    private final Set<Integer> confirmedHitList = new HashSet<>();

    public BlockManager() {
        super("Block Manager", "Manages blocks in world.");

        INSTANCE = this;
    }

    public ArrayList<BlockPos> getPlayerSurroundBlockList() {
        return playerSurroundBlockList;
    }

    public static ArrayList<BlockPos> getAirSurroundPlayer() {
        return INSTANCE.getPlayerSurroundBlockList();
    }

    public static int getCrystalsPerSecond() {
        return INSTANCE.lastCrystalAmount;
    }

    @Listener
    public void onPacketReceive(PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketSoundEffect) {
            final SPacketSoundEffect packet = (SPacketSoundEffect) event.getPacket();

            if (packet.getCategory() == SoundCategory.BLOCKS && packet.getSound() == SoundEvents.ENTITY_GENERIC_EXPLODE) {
                int idEntity = -1;

                for (int ids : this.confirmedHitList) {
                    final EntityEnderCrystal enderCrystal = (EntityEnderCrystal) mc.world.getEntityByID(ids);

                    if (enderCrystal != null && enderCrystal.getPosition().equals(new BlockPos(packet.getX(), packet.getY(), packet.getZ()))) {
                        idEntity = ids;

                        break;
                    }
                }

                if (idEntity != -1) {
                    this.confirmedHitList.remove(idEntity);
                    this.crystalAmount++;
                }
            }
        }
    }

    @Listener
    public void onPacketSend(PacketEvent.Send event) {
        if (event.getPacket() instanceof CPacketUseEntity) {
            final CPacketUseEntity packet = (CPacketUseEntity) event.getPacket();

            if (packet.getEntityFromWorld(mc.world) instanceof EntityEnderCrystal && !this.confirmedHitList.contains(packet.entityId)) {
                this.confirmedHitList.add(packet.entityId);
            }
        }
    }

    @Override
    public void onUpdateAll() {
        this.world = mc.world;

        if (this.world == null || mc.player == null) {
            return;
        }

        this.updateCrystalsPerSecond();
        this.updateSurroundPlayerList();
    }

    public void updateCrystalsPerSecond() {
        if (this.crystalStamp.isPassedMS(1000)) {
            this.lastCrystalAmount = this.crystalAmount;
            this.crystalAmount = 0;

            this.crystalStamp.reset();
        }
    }

    public void updateSurroundPlayerList() {
        this.playerSurroundBlockList.clear();

        BlockPos player = BlockUtil.BLACK_LIST.contains(BlockUtil.getBlock(PlayerUtil.getBlockPos())) ? PlayerUtil.getBlockPos().add(0, 1d, 0) : PlayerUtil.getBlockPos();

        for (BlockPos add : BlocksUtil.FULL_SURROUND) {
            final BlockPos offset = player.add(add);

            int diffY = offset.getY() - player.getY();

            if (diffY == -1 && (!BlockUtil.isAir(offset.up()) || BlockUtil.isPlaceableExcludingBlackList(offset.up()))) {
                continue;
            }

            if (diffY == 1 && BlockUtil.isAir(offset.up())) {
                continue;
            }

            if (player.down() == offset && BlockUtil.isAir(offset) && !BlockUtil.isAir(offset.down())) {
                continue;
            }

            if (BlockUtil.isAir(offset) && BlockUtil.isPlaceableExcludingBlackList(offset)) {
                this.playerSurroundBlockList.add(offset);
            }
        }
    }
}
