package rina.onepop.club.client.module.misc.autofish;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.rina.turok.util.TurokTick;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import rina.onepop.club.Onepop;
import rina.onepop.club.api.ISLClass;
import rina.onepop.club.api.module.Module;
import rina.onepop.club.api.module.impl.ModuleCategory;
import rina.onepop.club.api.module.registry.Registry;
import rina.onepop.club.api.setting.value.ValueNumber;
import rina.onepop.club.api.tracker.impl.RightMouseClickTracker;
import rina.onepop.club.api.util.client.NullUtil;
import rina.onepop.club.client.event.client.ClientTickEvent;
import rina.onepop.club.client.event.network.PacketEvent;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

/**
 * @author SrRina
 * @since 02/02/2021 at 13:28
 **/
@Registry(name = "Auto-Fish", tag = "AutoFish", description = "Automatically fish to you.", category = ModuleCategory.MISC)
public class ModuleAutoFish extends Module {
    public static ValueNumber settingSplashDelay = new ValueNumber("Splash Delay", "SplashDelay", "The MS delay after the sound splash event.", 750, 1, 3000);

    private Flag flag = Flag.NoFishing;
    private final TurokTick tick = new TurokTick();

    @Override
    public void onSetting() {
    }

    @Override
    public void onShutdown() {
        this.setDisabled();
    }

    @Listener
    public void onListen(PacketEvent.Receive event) {
        if (!(event.getPacket() instanceof SPacketSoundEffect)) {
            return;
        }

        // We set the variables to use.
        final SPacketSoundEffect packet = (SPacketSoundEffect) event.getPacket();
        final SoundEvent currentSoundEvent = packet.getSound();

        // Verify the current sound.
        if (currentSoundEvent == SoundEvents.ENTITY_BOBBER_SPLASH) {
            this.flag = Flag.Splash;
        }
    }

    @Listener
    public void onListenClientTick(ClientTickEvent event) {
        if (NullUtil.isPlayerWorld()) {
            return;
        }

        // We need verify if has Fishing Rod at hand!
        if (ISLClass.mc.player.getHeldItemMainhand().getItem() != Items.FISHING_ROD) {
            this.print("No fishing rod at hand!");
            this.setDisabled();

            return;
        }

        if (this.flag == Flag.Splash) {
            // There is the delay to late splash.
            if (this.tick.isPassedMS(settingSplashDelay.getValue().intValue())) {
                this.print("You fish!");

                // We skip queue and send.
                Onepop.getTrackerManager().dispatch(new RightMouseClickTracker(EnumHand.MAIN_HAND));

                // We send for queue.
                Onepop.getTrackerManager().dispatch(new RightMouseClickTracker(EnumHand.MAIN_HAND));

                // End flag for splash, so it back to fishing system.
                this.flag = Flag.Fishing;
            }
        } else {
            // Reset the tick for no delay for the delay..
            this.tick.reset();

            // I don't know.
            if (this.flag == Flag.NoFishing && ISLClass.mc.gameSettings.keyBindUseItem.isKeyDown()) {
                this.flag = Flag.Fishing;
            }

            if (this.flag == Flag.Fishing) {
                // We can't fish out of water!
                if (mc.player.fishEntity != null && mc.player.fishEntity.onGround) {
                    this.print("You can't fish out of " + ChatFormatting.BLUE + "water" + ChatFormatting.WHITE + ".");

                    Onepop.getTrackerManager().dispatch(new RightMouseClickTracker(EnumHand.MAIN_HAND));
                }
            }
        }
    }

    @Override
    public void onEnable() {
        this.flag = Flag.NoFishing;
    }

    @Override
    public void onDisable() {
        // Not sure, but we do it.
        if (this.flag == Flag.Fishing) {
            Onepop.getTrackerManager().dispatch(new RightMouseClickTracker(EnumHand.MAIN_HAND));
        }

        this.flag = Flag.NoFishing;
    }
}