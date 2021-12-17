package rina.onepop.club.client.module.player.phasewalk;

import me.rina.turok.util.TurokTick;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.potion.Potion;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import rina.onepop.club.api.module.Module;
import rina.onepop.club.api.module.impl.ModuleCategory;
import rina.onepop.club.api.module.registry.Registry;
import rina.onepop.club.api.setting.value.ValueBoolean;
import rina.onepop.club.api.setting.value.ValueEnum;
import rina.onepop.club.api.setting.value.ValueNumber;
import rina.onepop.club.api.util.client.NullUtil;
import rina.onepop.club.client.event.entity.PlayerMoveEvent;
import rina.onepop.club.client.event.network.PacketEvent;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

/**
 * @author mmmmax
 * @added-by doctor-swag
 * @since 15:28 GMT
 */
@Registry(name = "Phase Walk", tag = "PhaseWalk", description = "Phase whilst walking!", category = ModuleCategory.PLAYER)
public class ModulePhaseWalk extends Module {

    public static ValueBoolean edgeEnable = new ValueBoolean("EdgeEnable", "EdgeEnable", "Clip on the edge of blocks!", false);
    public static ValueEnum mode = new ValueEnum("Mode", "Mode", "The mode of phase!", Mode.CLIP);
    public static ValueNumber delay = new ValueNumber("Delay", "Delay", "The Delay for phase!", 200, 0, 100);
    public static ValueNumber attempts = new ValueNumber("Attempts", "Attempts", "The attempts for phasing.", 5, 0, 10);
    public static ValueBoolean cancelPlayer = new ValueBoolean("Cancel", "Cancel", "Cancels the player!", true);
    public static ValueEnum handleTeleport = new ValueEnum("Handle Teleport", "HandleTeleport", "Handles the teleport of the player!",TeleportMode.ALL );
    public static ValueNumber limitAmount = new ValueNumber("Limit Amount", "LimitAmount", "The limiting amount for phase!", 0.3, 0, 1);
    public static ValueNumber speed = new ValueNumber("Speed", "Speed", "The speed!", 3, 1, 50);
    public static ValueBoolean autoSpeed = new ValueBoolean("Auto-Speed", "AutoSpeed", "Automatically speed the phase!", true);

    boolean cancel = false;

    int teleportID = 0;

    TurokTick timer = new TurokTick();

    @Listener
    public void onPacketSend(PacketEvent.Send event) {
        if (NullUtil.isPlayerWorld()) {
            return;
        }
        if (event.getPacket() instanceof CPacketPlayer){
            if (cancel && cancelPlayer.getValue()){
                event.setCanceled(true);
            }
        }

        if (event.getPacket() instanceof CPacketConfirmTeleport){
            if (handleTeleport.getValue() == TeleportMode.CANCEL){
                event.setCanceled(true);
            }
        }
    }

    @Listener
    public void onPacketRecieve(PacketEvent.Receive event) {
        if (NullUtil.isPlayerWorld()) {
            return;
        }
        if (event.getPacket() instanceof SPacketPlayerPosLook){
            teleportID = ((SPacketPlayerPosLook) event.getPacket()).getTeleportId();
            if (handleTeleport.getValue() == TeleportMode.ALL){
                mc.getConnection().sendPacket(new CPacketConfirmTeleport(teleportID - 1));
                mc.getConnection().sendPacket(new CPacketConfirmTeleport(teleportID));
                mc.getConnection().sendPacket(new CPacketConfirmTeleport(teleportID + 1));
            }

            if (handleTeleport.getValue() == TeleportMode.BELOW){
                mc.getConnection().sendPacket(new CPacketConfirmTeleport(teleportID +- 1));
            }

            if (handleTeleport.getValue() == TeleportMode.ABOVE){
                mc.getConnection().sendPacket(new CPacketConfirmTeleport(teleportID + 1));
            }

            if (handleTeleport.getValue() == TeleportMode.NOBAND){
                mc.getConnection().sendPacket(new CPacketPlayer.Position(0, 1337, 0, mc.player.onGround));
                mc.getConnection().sendPacket(new CPacketConfirmTeleport(teleportID + 1));
            }
        }
    }

    @SubscribeEvent
    public void onUpdate(TickEvent.ClientTickEvent event){
        if (NullUtil.isPlayerWorld()) return;
        mc.player.motionX = 0;
        mc.player.motionY = 0;
        mc.player.motionZ = 0;

        if (mode.getValue() == Mode.CLIP){
            if (shouldPacket()){
                if (timer.isPassedMS(delay.getValue().longValue())) {
                    double[] forward = forward(getSpeed());
                    for (int i = 0; i < attempts.getValue().intValue(); i++){
                        sendPackets(mc.player.posX + forward[0], mc.player.posY + getUpMovement(), mc.player.posZ + forward[1]);
                    }

                    timer.reset();
                }
            } else {
                cancel = false;
            }
        }
    }

    @Listener
    public void onMove(PlayerMoveEvent event) {
        if (NullUtil.isPlayerWorld()) {
            return;
        }
        if (shouldPacket()) {
            if (mode.getValue() == Mode.SMOOTH) {
                double[] forward = forward(getSpeed());

                for (int i = 0; i < attempts.getValue().intValue(); i++) {
                    sendPackets(mc.player.posX + forward[0], mc.player.posY + getUpMovement(), mc.player.posZ + forward[1]);
                }
            }

            event.x = 0;
            event.y = 0;
            event.z = 0;
        }
    }



    double getUpMovement(){
        return (mc.gameSettings.keyBindJump.isKeyDown() ? 1 : mc.gameSettings.keyBindSneak.isKeyDown() ? -1 : 0) * getSpeed();
    }

    public void sendPackets(double x, double y, double z){
        cancel = false;
        mc.getConnection().sendPacket(new CPacketPlayer.Position(x, y, z, mc.player.onGround));
        mc.getConnection().sendPacket(new CPacketPlayer.Position(0, 1337, 0, mc.player.onGround));
        //     mc.getConnection().sendPacket(new CPacketPlayer.Position(0, 1447, 0, mc.player.onGround));
        cancel = true;
    }

    double getSpeed(){
        return autoSpeed.getValue() ? getDefaultMoveSpeed() / 10d : speed.getValue().doubleValue() / 100d;
    }

    boolean shouldPacket(){
        return !edgeEnable.getValue() || mc.player.collidedHorizontally;
    }


    public static double getDefaultMoveSpeed() {
        double baseSpeed = 0.2873;
        if (mc.player != null && mc.player.isPotionActive(Potion.getPotionById(1))) {
            final int amplifier = mc.player.getActivePotionEffect(Potion.getPotionById(1)).getAmplifier();
            baseSpeed *= 1.0 + 0.2 * (amplifier + 1);
        }
        return baseSpeed;
    }

    public static boolean isMoving(EntityLivingBase entity) {
        return entity.moveForward != 0 || entity.moveStrafing != 0;
    }

    public static double[] forward(final double speed) {
        float forward = mc.player.movementInput.moveForward;
        float side = mc.player.movementInput.moveStrafe;
        float yaw = mc.player.prevRotationYaw + (mc.player.rotationYaw - mc.player.prevRotationYaw) * mc.getRenderPartialTicks();
        if (forward != 0.0f) {
            if (side > 0.0f) {
                yaw += ((forward > 0.0f) ? -45 : 45);
            } else if (side < 0.0f) {
                yaw += ((forward > 0.0f) ? 45 : -45);
            }
            side = 0.0f;
            if (forward > 0.0f) {
                forward = 1.0f;
            } else if (forward < 0.0f) {
                forward = -1.0f;
            }
        }
        final double sin = Math.sin(Math.toRadians(yaw + 90.0f));
        final double cos = Math.cos(Math.toRadians(yaw + 90.0f));
        final double posX = forward * speed * cos + side * speed * sin;
        final double posZ = forward * speed * sin - side * speed * cos;
        return new double[]{posX, posZ};
    }


}
