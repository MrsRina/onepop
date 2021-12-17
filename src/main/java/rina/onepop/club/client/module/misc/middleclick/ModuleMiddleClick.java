package rina.onepop.club.client.module.misc.middleclick;

import rina.onepop.club.Onepop;
import rina.onepop.club.api.module.Module;
import rina.onepop.club.api.module.impl.ModuleCategory;
import rina.onepop.club.api.module.registry.Registry;
import rina.onepop.club.api.setting.value.ValueEnum;
import rina.onepop.club.api.social.Social;
import rina.onepop.club.api.social.management.SocialManager;
import rina.onepop.club.api.social.type.SocialType;
import rina.onepop.club.api.util.client.NullUtil;
import rina.onepop.club.api.util.item.SlotUtil;
import rina.onepop.club.client.event.client.RunTickEvent;
import rina.onepop.club.client.module.combat.burrow.ModuleBurrow;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import org.lwjgl.input.Mouse;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

/**
 * @author SrRina
 * @since 03/05/2021 at 13:25
 **/
@Registry(name = "Middle Click", tag = "MiddleClick", description = "Middle click!", category = ModuleCategory.MISC)
public class ModuleMiddleClick extends Module {
    /* Misc. */
    public static ValueEnum settingMode = new ValueEnum("Mode", "Mode", "Modes for middle click!", Mode.ENDER_PEARL);

    private boolean hasPress;

    @Listener
    public void onTick(RunTickEvent event) {
        if (NullUtil.isPlayerWorld()) {
            return;
        }

        int currentSlot = mc.player.inventory.currentItem;

        if (Mouse.isButtonDown(2)) {
            if (((Mode) settingMode.getValue()).getItem() != null) {
                int slot = SlotUtil.findItemSlotFromHotBar(((Mode) settingMode.getValue()).getItem());

                if (slot != -1) {
                    SlotUtil.setServerCurrentItem(slot);

                    mc.playerController.processRightClick(mc.player, mc.world, EnumHand.MAIN_HAND);

                    this.hasPress = true;
                }
            } else if (settingMode.getValue() != Mode.BURROW) {
                final Entity pointed = mc.getRenderManager().pointedEntity;

                if (pointed instanceof EntityPlayer && !this.hasPress) {
                    Social social = SocialManager.get(pointed.getName());

                    if (settingMode.getValue() == Mode.FRIEND) {
                        if (social == null) {
                            Onepop.getSocialManager().registry(new Social(pointed.getName(), SocialType.FRIEND));

                            this.print("Added " + pointed.getName() + " at friend list.");
                        } else if (social.getType() == SocialType.FRIEND) {
                            Onepop.getSocialManager().unregister(social);

                            this.print("Removed " + pointed.getName() + " from friend list.");
                        }
                    } else {
                        if (social == null) {
                            Onepop.getSocialManager().registry(new Social(pointed.getName(), SocialType.ENEMY));

                            this.print("Added " + pointed.getName() + " at enemy list.");
                        } else if (social.getType() == SocialType.ENEMY) {
                            Onepop.getSocialManager().unregister(social);

                            this.print("Removed " + pointed.getName() + " from enemy list.");
                        }
                    }

                    this.hasPress = true;
                }
            } else {
                if (!this.hasPress) {
                    ModuleBurrow.INSTANCE.setEnabled();

                    this.hasPress = true;
                }
            }
        } else {
            if (this.hasPress) {
                if (((Mode) settingMode.getValue()).getItem() != null) {
                    SlotUtil.setServerCurrentItem(currentSlot);
                }

                this.hasPress = false;
            }
        }
    }
}
