package rina.onepop.club.client.module.combat;

import net.minecraft.item.ItemAxe;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketUseEntity;
import rina.onepop.club.api.ISLClass;
import rina.onepop.club.api.module.Module;
import rina.onepop.club.api.module.impl.ModuleCategory;
import rina.onepop.club.api.module.registry.Registry;
import rina.onepop.club.api.setting.value.ValueBoolean;
import rina.onepop.club.api.setting.value.ValueEnum;
import rina.onepop.club.api.setting.value.ValueNumber;
import rina.onepop.club.api.social.Social;
import rina.onepop.club.api.social.management.SocialManager;
import rina.onepop.club.api.social.type.SocialType;
import rina.onepop.club.api.util.client.NullUtil;
import rina.onepop.club.api.util.entity.EntityUtil;
import rina.onepop.club.api.util.math.RotationUtil;
import rina.onepop.club.api.util.network.PacketUtil;
import rina.onepop.club.client.event.client.ClientTickEvent;
import rina.onepop.club.client.manager.network.Rotation;
import rina.onepop.club.client.manager.network.RotationManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityMinecartContainer;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.entity.projectile.EntityShulkerBullet;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

/**
 * @author SrRina
 * @since 17/02/2021 at 13:30
 **/
@Registry(name = "Kill-Aura", tag = "KillAura", description = "Make you hit any entity close of you.", category = ModuleCategory.COMBAT)
public class ModuleKillAura extends Module {
    /* Misc. */
    public static ValueBoolean settingAntiNaked = new ValueBoolean("Anti-Naked", "AntiNaked", "Preserve cringe naked players.", true);
    public static ValueBoolean settingOnlySwordAndAxe = new ValueBoolean("Only Sword/Axe", "OnlySword&Axe", "Held a sword/axe for attack.", false);
    public static ValueBoolean settingRenderSwing = new ValueBoolean("Render Swing", "RenderSwing", "Render attacks swing.", true);
    public static ValueBoolean settingDoubleAttack = new ValueBoolean("Double Attack", "DoubleAttack", "Attacks double times!", true);
    public static ValueEnum settingRotation = new ValueEnum("Rotation", "Rotation", "Rotation for strict servers!", Rotation.NONE);

    /* Player stuff. */
    public static ValueBoolean settingPlayer = new ValueBoolean("Player", "Player", "Hit entity players.", true);

    /* Entities accepted. */
    public static ValueBoolean settingMob = new ValueBoolean("Mob", "Mob", "Hit entity mobs.", true);
    public static ValueBoolean settingAnimal = new ValueBoolean("Animal", "Animal", "Hit entity animal.", true);
    public static ValueBoolean settingVehicles = new ValueBoolean("Vehicle", "Vehicle", "Hit entity vehicles.", true);
    public static ValueBoolean settingProjectiles = new ValueBoolean("Projectile", "Projectile", "Hit entity projectiles.", true);
    public static ValueBoolean settingOffhandItem = new ValueBoolean("Offhand Item", "OffhandItem", "Enable use item while aura is hitting.", true);

    /* Misc. */
    public static ValueNumber settingRange = new ValueNumber("Range", "Range", "Range for target.", 4f, 1f, 6f);

    private Entity target;

    @Listener
    public void onListen(ClientTickEvent event) {
        if (NullUtil.isPlayerWorld()) {
            return;
        }

        final EntityPlayer currentPlayer = settingPlayer.getValue() ? EntityUtil.getTarget(settingRange.getValue().floatValue(), false, false) : null;

        if (currentPlayer == null) {
            this.target = this.doFind();
        } else {
            this.target = currentPlayer;
        }

        this.status(this.target != null ? this.target.getName() : "");

        boolean secondHit = false;

        if (this.target != null && !this.target.isDead) {
            // Only sword.
            boolean flag = !settingOnlySwordAndAxe.getValue() || ((mc.player.getHeldItemMainhand().getItem() instanceof ItemSword || mc.player.getHeldItemMainhand().getItem() instanceof ItemAxe) || (mc.player.getHeldItemOffhand().getItem() instanceof ItemSword || mc.player.getHeldItemOffhand().getItem() instanceof ItemAxe));
            boolean flagOffhand = (mc.player.getHeldItemOffhand().getItem() instanceof ItemSword || mc.player.getHeldItemOffhand().getItem() instanceof ItemAxe);

            if (mc.player.getCooledAttackStrength(0) >= 1 && flag) {
                final ItemStack offhand = mc.player.getHeldItemOffhand();

                if (offhand.getItem() == Items.SHIELD && settingOffhandItem.getValue()) {
                    PacketUtil.send(new CPacketPlayerDigging(CPacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, ISLClass.mc.player.getHorizontalFacing()));
                }

                float[] rotates = RotationUtil.getBreakRotation(new Vec3d(this.target.posX, this.target.posY, this.target.posZ));

                RotationManager.task(settingRotation.getValue(), rotates);

                if (settingDoubleAttack.getValue()) {
                    mc.player.connection.sendPacket(new CPacketUseEntity(this.target));
                }

                mc.playerController.attackEntity(mc.player, this.target);
                this.doSwing(flagOffhand);
            }
        }
    }

    public boolean doVerify(Entity entity) {
        boolean isVerified = false;

        if (entity instanceof IMob && settingMob.getValue()) {
            isVerified = true;
        }

        if (entity instanceof IAnimals && (entity instanceof IMob) == false && settingAnimal.getValue()) {
            isVerified = true;
        }

        if ((entity instanceof EntityBoat || entity instanceof EntityMinecart || entity instanceof EntityMinecartContainer) && settingVehicles.getValue()) {
            isVerified = true;
        }

        if ((entity instanceof EntityShulkerBullet || entity instanceof EntityFireball) && settingProjectiles.getValue()) {
            isVerified = true;
        }

        if (entity instanceof EntityLivingBase) {
            EntityLivingBase entityLivingBase = (EntityLivingBase) entity;

            if (entityLivingBase.isDead || entityLivingBase.getHealth() < 0) {
                isVerified = false;
            }
        }

        return isVerified;
    }

    public Entity doFind() {
        Entity entity = null;

        float range = settingRange.getValue().floatValue();

        for (Entity entities : ISLClass.mc.world.loadedEntityList) {
            if (entities == null || entities == mc.player) {
                continue;
            }

            if (!doVerify(entities)) {
                continue;
            }

            if (entities instanceof EntityPlayer && settingAntiNaked.getValue() && EntityUtil.isEntityPlayerNaked((EntityPlayer) entities)) {
                continue;
            }

            final Social social = SocialManager.get(entities.getName());
            final float distance = mc.player.getDistance(entities);

            if (social != null && social.getType() == SocialType.ENEMY && distance <= range) {
                entity = entities;
                range = distance;
            } else {
                if (distance <= range) {
                    entity = entities;
                    range = distance;
                }
            }
        }

        return entity;
    }

    public void doSwing(boolean offhand) {
        if (settingRenderSwing.getValue()) {
            mc.player.swingArm(offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND);
        } else {
            mc.player.connection.sendPacket(new CPacketAnimation(offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND));
        }
    }
}
