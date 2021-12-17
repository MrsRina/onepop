package rina.onepop.club.client.module.misc;

import rina.onepop.club.Onepop;
import rina.onepop.club.api.ISLClass;
import rina.onepop.club.api.module.Module;
import rina.onepop.club.api.module.impl.ModuleCategory;
import rina.onepop.club.api.module.registry.Registry;
import rina.onepop.club.api.setting.value.ValueBoolean;
import rina.onepop.club.api.setting.value.ValueEnum;
import rina.onepop.club.api.setting.value.ValueNumber;
import rina.onepop.club.api.setting.value.ValueString;
import rina.onepop.club.api.util.client.FlagBoolUtil;
import rina.onepop.club.api.util.client.KeyUtil;
import rina.onepop.club.api.util.client.NullUtil;
import rina.onepop.club.api.util.entity.PlayerUtil;
import me.rina.turok.util.TurokMath;
import rina.onepop.club.client.event.client.ClientTickEvent;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

/**
 * @author SrRina
 * @since 04/02/2021 at 20:28
 **/
@Registry(name = "Spammer", tag = "Spammer", description = "Send a lot messages on chat.", category = ModuleCategory.MISC)
public class ModuleSpammer extends Module {
    /* Misc settings. */
    public static ValueNumber settingDelay = new ValueNumber("Delay", "Delay", "Seconds delay to send message.", 0.5f, 0.5f, 10.0f);
    public static ValueNumber settingLimit = new ValueNumber("Limit", "Limit", "The limit of messages in queue.", 3, 1, 6);
    public static ValueBoolean settingAntiSpam = new ValueBoolean("Anti-Spam", "AntiSpam", "Make anti spam server crazy.", true);

    /* Walk. */
    public static ValueEnum settingWalk = new ValueEnum("Walk", "Walk", "Spam blocks walked.", FlagBoolUtil.TRUE);
    public static ValueString settingWalkText = new ValueString("Walk Text", "WalkText", "The custom text for spam.", "I just walked <blocks> blocks, thanks to Onepop!");

    /* Jump. */
    public static ValueEnum settingJump = new ValueEnum("Jump", "Jump", "Spam jump action.", FlagBoolUtil.TRUE);
    public static ValueString settingJumpText = new ValueString("Jump Text", "JumpText", "The custom text for spam.", "I just jumped, thanks to Onepop!");

    private double[] lastWalkingPlayerPos;
    private String current;

    @Override
    public void onSetting() {
        Onepop.getSpammerManager().setDelay(settingDelay.getValue().floatValue());
        Onepop.getSpammerManager().setLimit(settingLimit.getValue().intValue());

        settingWalkText.setEnabled(settingWalk.getValue() == FlagBoolUtil.TRUE);
        settingJumpText.setEnabled(settingJump.getValue() == FlagBoolUtil.TRUE);
    }

    @Override
    public void onShutdown() {
        this.setDisabled();
    }

    @Listener
    public void onListen(ClientTickEvent event) {
        if (NullUtil.isPlayerWorld()) {
            return;
        }
        
        if (settingWalkText.isEnabled()) {
            this.verifyWalking();
        }

        if (settingJumpText.isEnabled()) {
            this.verifyJump();
        }
    }

    public void verifyWalking() {
        // Just to fix null action.
        if (this.lastWalkingPlayerPos == null) {
            this.lastWalkingPlayerPos = PlayerUtil.getLastTickPos();
        }

        if (PlayerUtil.getBPS() >= 4) {
            int x = (int) (this.lastWalkingPlayerPos[0] - PlayerUtil.getPos()[0]);
            int y = (int) (this.lastWalkingPlayerPos[1] - PlayerUtil.getPos()[1]);
            int z = (int) (this.lastWalkingPlayerPos[2] - PlayerUtil.getPos()[2]);

            // x^2 + y^2 + z^2;
            int walkedBlocks = TurokMath.sqrt(x * x + y * y + z * z);

            // I don't want "walked (1, 0) blocks".
            if (walkedBlocks >= 2) {
                Onepop.getSpammerManager().send(settingWalkText.getValue().replaceAll("<blocks>", "" + walkedBlocks) + getRandom());
            }
        } else {
            // Sync the last position.
            this.lastWalkingPlayerPos = PlayerUtil.getLastTickPos();
        }
    }

    public void verifyJump() {
        // We can't send jump spam if we are sprinting or just jumping running, only if you are stop or not fast.
        if (KeyUtil.isJumping() && PlayerUtil.getBPS() <= 4 && ISLClass.mc.player.isInWater() == false) {
            Onepop.getSpammerManager().send(settingJumpText.getValue() + getRandom());
        }
    }

    /*
     * Get a random number to fuck any anti spam system.
     */
    public String getRandom() {
        int min = 0;
        int max = 500;

        String random = "";

        if (settingAntiSpam.getValue()) {
            random = " " + ((int) (Math.random() * (max - min + 1) + min));
        }

        return random;
    }
}
