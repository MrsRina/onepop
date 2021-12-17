package rina.onepop.club.client.module.player;

import rina.onepop.club.api.ISLClass;
import rina.onepop.club.api.module.Module;
import rina.onepop.club.api.module.impl.ModuleCategory;
import rina.onepop.club.api.module.registry.Registry;
import rina.onepop.club.api.setting.value.ValueBoolean;
import rina.onepop.club.api.setting.value.ValueNumber;
import rina.onepop.club.api.util.client.NullUtil;
import rina.onepop.club.client.event.client.ClientTickEvent;
import me.rina.turok.util.TurokTick;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

/**
 * @author SrRina
 * @since 18/02/2021 at 09:49
 **/
@Registry(name = "Step", tag = "Step", description = "Step up blocks.", category = ModuleCategory.PLAYER)
public class ModuleStep extends Module {
    public static ModuleStep INSTANCE;

    /* Misc. */
    public static ValueNumber settingHeight = new ValueNumber("Height", "Height", "Height for step.", 2, 1, 2);
    public static ValueBoolean settingVanilla = new ValueBoolean("Vanilla", "Vanilla", "Vanilla step.", false);
    public static ValueBoolean settingDisable = new ValueBoolean("Disable", "Disable", "Automatically disables step.", false);
    public static ValueNumber settingDelayDisable = new ValueNumber("Delay Disable", "DelayDisable", "The delay for disable step.", 250, 0, 1000);

    private final TurokTick tick = new TurokTick();
    private boolean counting;

    public ModuleStep() {
        INSTANCE = this;
    }

    @Override
    public void onSetting() {
        settingDisable.setEnabled(!settingVanilla.getValue());
        settingDelayDisable.setEnabled(settingDisable.isEnabled() && settingDisable.getValue());
    }

    @Override
    public void onEnable() {
        this.tick.reset();
        this.counting = false;
    }

    @Override
    public void onDisable() {
        this.tick.reset();
        this.counting = false;

        if (NullUtil.isPlayer()) {
            return;
        }

        mc.player.stepHeight = 0.5f;
    }

    @Listener
    public void onListen(ClientTickEvent event) {
        if (NullUtil.isPlayerWorld()) {
            return;
        }

        if (settingVanilla.getValue()) {
            mc.player.stepHeight = settingHeight.getValue().intValue();

            return;
        } else {
            if (this.counting && settingDisable.getValue() && this.tick.isPassedMS(settingDelayDisable.getValue().intValue())) {
                this.setDisabled();
                return;
            }
        }

        // lol
        if (!mc.player.collidedHorizontally) return;
        if (!mc.player.onGround || mc.player.isOnLadder() || mc.player.isInWater() || mc.player.isInLava() || mc.player.movementInput.jump || mc.player.noClip) return;
        if (mc.player.moveForward == 0 && mc.player.moveStrafing == 0) return;
        // lol

        mc.player.stepHeight = 0.5f;

        double step = this.getStepHeight();

        if (step < 0 || step > 2) {
            return;
        }

        if (step == 2.0d && settingHeight.getValue().intValue() == 2) {
            ISLClass.mc.player.connection.sendPacket(new CPacketPlayer.Position(ISLClass.mc.player.posX, ISLClass.mc.player.posY + 0.42, ISLClass.mc.player.posZ, ISLClass.mc.player.onGround));
            ISLClass.mc.player.connection.sendPacket(new CPacketPlayer.Position(ISLClass.mc.player.posX, ISLClass.mc.player.posY + 0.78, ISLClass.mc.player.posZ, ISLClass.mc.player.onGround));
            ISLClass.mc.player.connection.sendPacket(new CPacketPlayer.Position(ISLClass.mc.player.posX, ISLClass.mc.player.posY + 0.63, ISLClass.mc.player.posZ, ISLClass.mc.player.onGround));
            ISLClass.mc.player.connection.sendPacket(new CPacketPlayer.Position(ISLClass.mc.player.posX, ISLClass.mc.player.posY + 0.51, ISLClass.mc.player.posZ, ISLClass.mc.player.onGround));
            ISLClass.mc.player.connection.sendPacket(new CPacketPlayer.Position(ISLClass.mc.player.posX, ISLClass.mc.player.posY + 0.9, ISLClass.mc.player.posZ, ISLClass.mc.player.onGround));
            ISLClass.mc.player.connection.sendPacket(new CPacketPlayer.Position(ISLClass.mc.player.posX, ISLClass.mc.player.posY + 1.21, ISLClass.mc.player.posZ, ISLClass.mc.player.onGround));
            ISLClass.mc.player.connection.sendPacket(new CPacketPlayer.Position(ISLClass.mc.player.posX, ISLClass.mc.player.posY + 1.45, ISLClass.mc.player.posZ, ISLClass.mc.player.onGround));
            ISLClass.mc.player.connection.sendPacket(new CPacketPlayer.Position(ISLClass.mc.player.posX, ISLClass.mc.player.posY + 1.43, ISLClass.mc.player.posZ, ISLClass.mc.player.onGround));
            ISLClass.mc.player.setPosition(ISLClass.mc.player.posX, ISLClass.mc.player.posY + 2.0, ISLClass.mc.player.posZ);

            this.doRefreshCounter();
        }

        if (step == 1.5d && (settingHeight.getValue().intValue() == 1 || settingHeight.getValue().intValue() == 2)) {
            ISLClass.mc.player.connection.sendPacket(new CPacketPlayer.Position(ISLClass.mc.player.posX, ISLClass.mc.player.posY + 0.41999998688698, ISLClass.mc.player.posZ, ISLClass.mc.player.onGround));
            ISLClass.mc.player.connection.sendPacket(new CPacketPlayer.Position(ISLClass.mc.player.posX, ISLClass.mc.player.posY + 0.7531999805212, ISLClass.mc.player.posZ, ISLClass.mc.player.onGround));
            ISLClass.mc.player.connection.sendPacket(new CPacketPlayer.Position(ISLClass.mc.player.posX, ISLClass.mc.player.posY + 1.00133597911214, ISLClass.mc.player.posZ, ISLClass.mc.player.onGround));
            ISLClass.mc.player.connection.sendPacket(new CPacketPlayer.Position(ISLClass.mc.player.posX, ISLClass.mc.player.posY + 1.16610926093821, ISLClass.mc.player.posZ, ISLClass.mc.player.onGround));
            ISLClass.mc.player.connection.sendPacket(new CPacketPlayer.Position(ISLClass.mc.player.posX, ISLClass.mc.player.posY + 1.24918707874468, ISLClass.mc.player.posZ, ISLClass.mc.player.onGround));
            ISLClass.mc.player.connection.sendPacket(new CPacketPlayer.Position(ISLClass.mc.player.posX, ISLClass.mc.player.posY + 1.1707870772188, ISLClass.mc.player.posZ, ISLClass.mc.player.onGround));
            ISLClass.mc.player.setPosition(ISLClass.mc.player.posX, ISLClass.mc.player.posY + 1.0, ISLClass.mc.player.posZ);

            this.doRefreshCounter();
        }

        if (step == 1.0 && (settingHeight.getValue().intValue() == 1 || settingHeight.getValue().intValue() == 2)) {
            ISLClass.mc.player.connection.sendPacket(new CPacketPlayer.Position(ISLClass.mc.player.posX, ISLClass.mc.player.posY + 0.41999998688698, ISLClass.mc.player.posZ, ISLClass.mc.player.onGround));
            ISLClass.mc.player.connection.sendPacket(new CPacketPlayer.Position(ISLClass.mc.player.posX, ISLClass.mc.player.posY + 0.7531999805212, ISLClass.mc.player.posZ, ISLClass.mc.player.onGround));
            ISLClass.mc.player.setPosition(ISLClass.mc.player.posX, ISLClass.mc.player.posY + 1.0, ISLClass.mc.player.posZ);

            this.doRefreshCounter();
        }
    }

    public void doRefreshCounter() {
        if (!this.counting && settingDisable.getValue() && settingDisable.isEnabled()) {
            this.counting = true;
        }
    }

    public double getStepHeight() {
        double h = -1d;

        final AxisAlignedBB bb = ISLClass.mc.player.getEntityBoundingBox().offset(0, 0.05, 0).grow(0.05);

        if (!mc.world.getCollisionBoxes(ISLClass.mc.player, bb.offset(0, 2, 0)).isEmpty()) {
            return 100;
        }

        for (final AxisAlignedBB aabbs : ISLClass.mc.world.getCollisionBoxes(mc.player, bb)) {
            if (aabbs.maxY > h) {
                h = aabbs.maxY;
            }
        }

        return h - mc.player.posY;
    }
}
