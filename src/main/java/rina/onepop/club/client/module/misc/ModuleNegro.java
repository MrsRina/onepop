package rina.onepop.club.client.module.misc;

import com.mojang.authlib.GameProfile;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.gui.GuiChat;
import rina.onepop.club.Onepop;
import rina.onepop.club.api.module.Module;
import rina.onepop.club.api.module.impl.ModuleCategory;
import rina.onepop.club.api.module.registry.Registry;
import rina.onepop.club.api.setting.value.ValueString;
import rina.onepop.club.api.util.client.NullUtil;
import rina.onepop.club.client.event.client.RunTickEvent;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

import java.util.UUID;

/**
 * @author SrRina
 * @since 17/04/2021 at 23:49
 **/
@Registry(name = "Negro Module", tag = "FakePlayer", description = "Negro module, muscle!", category = ModuleCategory.MISC)
public class ModuleNegro extends Module {
    /* Misc. */
    public static ValueString settingName = new ValueString("Name", "Name", "Customize name from fake player.", "Pedroperry");

    private EntityOtherPlayerMP pedroperry;
    private String lastVerifiedTag;

    @Override
    public void onShutdown() {
        this.setDisabled();
    }

    @Listener
    public void onTick(RunTickEvent event) {
        if (NullUtil.isPlayerWorld()) {
            return;
        }

        if (mc.currentScreen == null || mc.currentScreen instanceof GuiChat) {
            if (!this.lastVerifiedTag.equals(settingName.getValue())) {
                this.print(ChatFormatting.YELLOW + "Reload module for apply new fake player name.");

                this.lastVerifiedTag = settingName.getValue();
            }
        }

        this.pedroperry.inventory = mc.player.inventory;

        if (mc.player.isDead || mc.player.getHealth() < 0) {
            this.setDisabled();
        }
    }

    @Override
    public void onEnable() {
        if (NullUtil.isPlayerWorld()) {
            return;
        }

        if (this.lastVerifiedTag == null) {
            this.lastVerifiedTag = settingName.getValue();
        }

        this.pedroperry = new EntityOtherPlayerMP(mc.world, new GameProfile(UUID.fromString("498dc6ae-1084-4d7d-ab9f-0cf090fd336a"), settingName.getValue()));
        this.pedroperry.copyLocationAndAnglesFrom(mc.player);
        this.pedroperry.rotationYawHead = mc.player.rotationYawHead;

        Onepop.getEntityWorldManager().saveEntity(-150, this.pedroperry);
        mc.world.addEntityToWorld(-150, this.pedroperry);
    }

    @Override
    public void onDisable() {
        if (NullUtil.isPlayerWorld()) {
            return;
        }

        Onepop.getEntityWorldManager().removeEntity(-150);
        mc.world.removeEntityFromWorld(-150);
    }
}
