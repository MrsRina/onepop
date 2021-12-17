package rina.onepop.club.client.module.combat;

import me.rina.turok.util.TurokTick;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import rina.onepop.club.api.module.Module;
import rina.onepop.club.api.module.impl.ModuleCategory;
import rina.onepop.club.api.module.registry.Registry;
import rina.onepop.club.api.setting.value.ValueBoolean;
import rina.onepop.club.api.setting.value.ValueColor;
import rina.onepop.club.api.setting.value.ValueNumber;
import rina.onepop.club.api.social.management.SocialManager;
import rina.onepop.club.api.social.type.SocialType;
import rina.onepop.club.api.util.client.NullUtil;
import rina.onepop.club.api.util.entity.EntityUtil;
import rina.onepop.club.api.util.item.SlotUtil;
import rina.onepop.club.api.util.math.PositionUtil;
import rina.onepop.club.api.util.render.RenderUtil;
import rina.onepop.club.api.util.world.BlockUtil;
import rina.onepop.club.client.event.client.ClientTickEvent;
import rina.onepop.club.client.module.misc.ModuleBetterMine;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Rina
 * @since 27/09/2021 at 17:33PM
 **/
@Registry(name = "Auto-City Mine", tag = "AutoCityMine", description = "Make you a bot.", category = ModuleCategory.COMBAT)
public class ModuleAutoCityMine extends Module {
    // Misc.
    public static ValueBoolean settingAntiNaked = new ValueBoolean("Anti-Naked", "AntiNaked", "No naked players.", true);
    public static ValueBoolean settingIgnoreFriend = new ValueBoolean("Friend Ignore", "FriendIgnore", "Ignore if a friend is in the hole.", false);
    public static ValueNumber settingRange = new ValueNumber("Range", "Range", "Sets range.", 6f, 2f, 6f);
    public static ValueBoolean settingCompatible = new ValueBoolean("Compatible", "Compatible", "Make it compatible with others client.", true);
    public static ValueBoolean settingPlace113 = new ValueBoolean("Place 1.13", "Place113", "New Minecraft version placement 1x1x1.", false);
    public static ValueBoolean settingAutoPlaceCrystal = new ValueBoolean("Auto-Crystal Place", "AutoCrystalPlace", "Put a crystal after break.", false);
    public static ValueNumber settingTicksRotate = new ValueNumber("Ticks Rotate", "TicksRotate", "Limit of rotating.", 0, 0, 8);
    public static ValueNumber settingCooldown = new ValueNumber("Cooldown", "Cooldown", "Sets cooldown for compatible mode.", 2.1f, 0f, 3f);
    public static ValueColor settingColor = new ValueColor("Color", "Color", "Sets color.", Color.RED);

    private EntityPlayer entity;
    private BlockPos position;

    private boolean isRotating;
    private int rotatingTicks;

    private boolean isBreaking;
    private boolean isDamaging;

    private final List<EntityPlayer> friendList = new ArrayList<>();
    private final TurokTick delayMSCooldown = new TurokTick();

    @Override
    public void onSetting() {
        settingTicksRotate.setEnabled(settingAutoPlaceCrystal.getValue());
        settingCooldown.setEnabled(settingCompatible.getValue());
    }

    @Override
    public void onRender3D() {
        if (NullUtil.isPlayerWorld()) {
            return;
        }

        if (this.position != null && this.isBreaking) {
            RenderUtil.drawOutlineBlock(camera, this.position, 1f, settingColor.getColor());
        }
    }

    @Listener
    public void onClientTickEvent(ClientTickEvent event) {
        if (NullUtil.isPlayerWorld()) {
            return;
        }

        this.entity = this.target();

        if (this.entity != null) {
            if (!this.isBreaking && (!this.containsFriendOnEntityPosition() || settingIgnoreFriend.getValue())) {
                final BlockPos position = EntityUtil.getFlooredEntityPosition(this.entity);

                int free = 0;
                BlockPos buffer = null;

                boolean skip = false;

                if (EntityUtil.isEntityBurrowed(this.entity)) {
                    skip = true;
                    buffer = EntityUtil.getEntityFlooredPosition(this.entity, 0.5d);
                }

                for (BlockPos adds : BlockUtil.HOLE) {
                    if (skip) {
                        break;
                    }

                    final BlockPos added = position.add(adds);

                    if ((BlockUtil.getBlock(added.add(0, -1, 0)) == Blocks.BEDROCK || BlockUtil.getBlock(added.add(0, -1, 0)) == Blocks.OBSIDIAN) && BlockUtil.isAir(added) && (BlockUtil.isAir(added.add(0, 1, 0)) || settingPlace113.getValue())) {
                        break;
                    }

                    if (BlockUtil.isBreakable(added) && !BlockUtil.isAir(added) && (BlockUtil.getBlock(added.add(0, -1, 0)) == Blocks.BEDROCK || BlockUtil.getBlock(added.add(0, -1, 0)) == Blocks.OBSIDIAN) && (BlockUtil.isAir(added.add(0, 1, 0)) || settingPlace113.getValue())) {
                        buffer = added;
                    }
                }

                this.position = buffer;

                if (buffer != null) {
                    this.isBreaking = true;

                    this.delayMSCooldown.reset();
                }
            }

            if (this.isBreaking && this.position != null) {
                if (settingCompatible.getValue()) {
                    if (!this.isDamaging) {
                        mc.playerController.onPlayerDamageBlock(this.position, EnumFacing.getDirectionFromEntityLiving(this.position, mc.player));

                        this.isDamaging = true;
                    }

                    if (this.delayMSCooldown.isPassedMS(settingCooldown.getValue().floatValue() * 1000)) {
                        if (BlockUtil.isAir(this.position)) {
                            this.updatePlace();
                        }

                        this.isDamaging = false;
                        this.isBreaking = false;

                        this.delayMSCooldown.reset();
                    }
                } else {
                    if (ModuleBetterMine.INSTANCE.isEnabled()) {
                        if (!this.isDamaging) {
                            ModuleBetterMine.INSTANCE.queue(this.position, EnumFacing.getDirectionFromEntityLiving(this.position, mc.player));

                            this.isDamaging = true;
                        }

                        if (!ModuleBetterMine.INSTANCE.containsBlockDamage(this.position) && this.isDamaging) {
                            if (BlockUtil.isAir(this.position)) {
                                this.updatePlace();
                            }

                            this.isDamaging = false;
                            this.isBreaking = false;
                        }
                    } else {
                        final EnumFacing facing = EnumFacing.getDirectionFromEntityLiving(this.position, mc.player);

                        mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, this.position, facing));
                        mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, this.position, facing));

                        if (mc.player.getDistance(this.position.x, this.position.y, this.position.z) >= 7f || BlockUtil.isAir(this.position)) {
                            if (BlockUtil.isAir(this.position)) {
                                this.updatePlace();
                            }

                            this.isBreaking = false;
                        }
                    }
                }
            }
        } else {
            this.position = null;
            this.isBreaking = false;
            this.isDamaging = false;
        }
    }

    protected void updatePlace() {
        if (settingAutoPlaceCrystal.getValue() && this.position != null) {
            int slot = SlotUtil.findItemSlotFromHotBar(Items.END_CRYSTAL);
            int side = mc.player.inventory.currentItem;

            EnumHand hand = mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL ? EnumHand.OFF_HAND : null;

            if (slot != -1 || hand != null) {
                boolean heldItemPacket = false;

                if (hand == null) {
                    hand = EnumHand.MAIN_HAND;
                    heldItemPacket = true;

                    mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
                }

                mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(this.position.down(), EnumFacing.UP, hand, 0.5f, 1f, 0.5f));
                mc.player.connection.sendPacket(new CPacketAnimation(hand));

                if (heldItemPacket) {
                    mc.player.connection.sendPacket(new CPacketHeldItemChange(side));
                }
            }
        }
    }

    protected EntityPlayer target() {
        EntityPlayer target = null;

        float range = settingRange.getValue().floatValue();

        this.friendList.clear();

        for (EntityPlayer entities : mc.world.playerEntities) {
            if (entities != mc.player) {
                float dist = mc.player.getDistance(entities);

                if (SocialManager.is(entities.getName(), SocialType.FRIEND) && dist <= settingRange.getValue().floatValue()) {
                    this.friendList.add(entities);

                    continue;
                }

                if (dist <= range && (!EntityUtil.isEntityPlayerNaked(entities) || !settingAntiNaked.getValue())) {
                    target = entities;
                    range = dist;
                }
            }
        }

        return target;
    }

    public boolean containsFriendOnEntityPosition() {
        boolean contains = false;

        final BlockPos targetPosition = EntityUtil.getFlooredEntityPosition(this.entity);

        for (EntityPlayer friends : this.friendList) {
            final BlockPos friendPosition = EntityUtil.getFlooredEntityPosition(friends);

            if (PositionUtil.collideBlockPos(friendPosition, targetPosition)) {
                contains = true;

                break;
            }
        }

        return contains;
    }
}
