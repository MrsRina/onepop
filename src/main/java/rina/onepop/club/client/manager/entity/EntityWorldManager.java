package rina.onepop.club.client.manager.entity;

import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import rina.onepop.club.api.manager.Manager;
import rina.onepop.club.api.util.item.SlotUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * @author SrRina
 * @since 01/03/2021 at 09:45
 **/
public class EntityWorldManager extends Manager {
    public static EntityWorldManager INSTANCE;

    private HashMap<Integer, Entity> entitySavedList;
    private EntityOtherPlayerMP entityPlayerSP;

    private boolean inventoryDataSync;

    public EntityWorldManager() {
        super("Entity World Manager", "Save or request entity from space abstract client.");

        this.entitySavedList = new HashMap<>();

        INSTANCE = this;
    }

    public void setEntitySavedList(HashMap<Integer, Entity> entitySavedList) {
        this.entitySavedList = entitySavedList;
    }

    public HashMap<Integer, Entity> getEntitySavedList() {
        return entitySavedList;
    }

    public void saveEntity(int entityId, Entity entity) {
        this.entitySavedList.put(entityId, entity);
    }

    public Entity removeEntity(int entityId) {
        Entity entity = this.getEntity(entityId);

        if (entity != null) {
            this.entitySavedList.remove(entityId);
        }

        return entity;
    }

    public Entity getEntity(int entityId) {
        return (Entity) this.entitySavedList.get(entityId);
    }

    public EntityOtherPlayerMP getEntityPlayerSP() {
        return entityPlayerSP;
    }

    public void setInventoryDataSync(boolean inventoryDataSync) {
        this.inventoryDataSync = inventoryDataSync;
    }

    public boolean getInventoryDataSync() {
        return inventoryDataSync;
    }

    public static void setCurrentItem(int slot) {
        if (slot == -1 || slot > 9) {
            unsetCurrentItem();

            return;
        }

        INSTANCE.setInventoryDataSync(false);

        INSTANCE.getEntityPlayerSP().inventory.currentItem = slot;
        INSTANCE.getEntityPlayerSP().setHeldItem(EnumHand.MAIN_HAND, SlotUtil.getItemStack(slot));
    }

    public static void unsetCurrentItem() {
        INSTANCE.setInventoryDataSync(true);
    }

    @Override
    public void onUpdateAll() {
        this.updatePlayerSP();

        for (Map.Entry<Integer, Entity> entities : new HashMap<>(this.entitySavedList).entrySet()) {
            int id = entities.getKey();
            Entity entity = entities.getValue();

            boolean isManageable = entity.isDead;

            if (entity instanceof EntityLivingBase) {
                EntityLivingBase entityLivingBase = (EntityLivingBase) entity;

                if (entityLivingBase.getHealth() < 0f) {
                    isManageable = true;
                }
            }

            if (entity instanceof EntityPlayer && mc.getConnection() != null) {
                EntityPlayer entityPlayer = (EntityPlayer) entities;
                NetworkPlayerInfo playerInfo = mc.getConnection().getPlayerInfo(entityPlayer.getUniqueID());

                if (playerInfo == null) {
                    isManageable = true;
                }
            }

            if (isManageable) {
                this.entitySavedList.remove(id);
            }
        }
    }

    protected void updatePlayerSP () {
        if (mc.player == null) {
            return;
        }

        if (this.entityPlayerSP == null) {
            this.entityPlayerSP = new EntityOtherPlayerMP(mc.world, mc.player.getGameProfile());
        }

        if (this.getInventoryDataSync()) {
            this.entityPlayerSP.inventory = mc.player.inventory;
        }
    }
}
