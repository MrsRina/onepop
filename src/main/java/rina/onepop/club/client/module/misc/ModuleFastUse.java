package rina.onepop.club.client.module.misc;

import net.minecraft.init.Items;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemEndCrystal;
import net.minecraft.item.ItemEnderPearl;
import net.minecraft.item.ItemSnowball;
import rina.onepop.club.Onepop;
import rina.onepop.club.api.module.Module;
import rina.onepop.club.api.module.impl.ModuleCategory;
import rina.onepop.club.api.module.registry.Registry;
import rina.onepop.club.api.setting.value.ValueBoolean;
import rina.onepop.club.api.util.client.NullUtil;
import rina.onepop.club.client.event.client.RunTickEvent;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

/*
jake module ????????????????????????????? nigga
 */
@Registry(name = "Fast Use", tag = "FastUse", description = "Fast use stuff.", category = ModuleCategory.MISC)
public class ModuleFastUse extends Module {
  public static ValueBoolean all = new ValueBoolean("All", "All", "Makes xp go brrrr", false);
  public static ValueBoolean exp = new ValueBoolean("Experience Bottle", "ExperienceBottle", "Makes xp go brrrr", true);
  public static ValueBoolean place = new ValueBoolean("Block", "Block", "Makes xp go brrrr", false);
  public static ValueBoolean crystal = new ValueBoolean("End Crystal", "EndCrystal", "Makes xp go brrrr", true);
  public static ValueBoolean pearl = new ValueBoolean("Pearl", "Pearl", "Makes xp go brrrr", true);
  public static ValueBoolean snowball = new ValueBoolean("Snowballs", "snowballs", "Makes xp go brrrr", true);

  @Override
  public void onSetting() {
    all.setEnabled(false);
    all.setValue(false);

    exp.setEnabled(!all.getValue());
    place.setEnabled(!all.getValue());
    crystal.setEnabled(!all.getValue());
    pearl.setEnabled(!all.getValue());
    snowball.setEnabled(!all.getValue());
  }

  @Listener
  public void onPacketSend(RunTickEvent event) {
    if (NullUtil.isPlayerWorld()) {
      return;
    }

    if (exp.getValue()) {
      if (Onepop.MC.player != null && (Onepop.MC.player.getHeldItemMainhand().getItem() == Items.EXPERIENCE_BOTTLE || Onepop.MC.player.getHeldItemOffhand().getItem() == Items.EXPERIENCE_BOTTLE)) {
        mc.rightClickDelayTimer = 0;
      }
    }

    if (place.getValue()) {
      if (Onepop.MC.player != null && (Onepop.MC.player.getHeldItemMainhand().getItem() instanceof ItemBlock || Onepop.MC.player.getHeldItemOffhand().getItem() instanceof ItemBlock)) {
        mc.rightClickDelayTimer = 0;
      }
    }
    if (crystal.getValue()) {
      if (Onepop.MC.player != null && (Onepop.MC.player.getHeldItemMainhand().getItem() instanceof ItemEndCrystal || Onepop.MC.player.getHeldItemOffhand().getItem() instanceof ItemEndCrystal)) {
        mc.rightClickDelayTimer = 0;
      }
    }
    if (pearl.getValue()) {
      if (Onepop.MC.player != null && (Onepop.MC.player.getHeldItemMainhand().getItem() instanceof ItemEnderPearl || Onepop.MC.player.getHeldItemOffhand().getItem() instanceof ItemEnderPearl)) {
        mc.rightClickDelayTimer = 0;
      }
    }
    if (snowball.getValue()) {
      if (Onepop.MC.player != null && (Onepop.MC.player.getHeldItemMainhand().getItem() instanceof ItemSnowball || Onepop.MC.player.getHeldItemOffhand().getItem() instanceof ItemSnowball)) {
        mc.rightClickDelayTimer = 0;
      }
    }
    if (all.getValue()) {
      mc.rightClickDelayTimer = 0;
    }
  }
}