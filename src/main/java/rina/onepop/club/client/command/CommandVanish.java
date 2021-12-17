package rina.onepop.club.client.command;

import com.mojang.realmsclient.gui.ChatFormatting;
import rina.onepop.club.Onepop;
import rina.onepop.club.api.command.Command;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;

/**
 * @author SrRina
 * @since 01/03/2021 at 09:39
 **/
public class CommandVanish extends Command {
    public CommandVanish() {
        super(new String[] {"v", "vanish", "riding", "ride"}, "Vanish mount/unmount.");
    }

    @Override
    public String setSyntax() {
        return "v/vanish/riding/ride <mount/on/dismount/off>";
    }

    @Override
    public void onCommand(String[] args) {
        String first = null;

        if (args.length > 1) {
            first = args[1];
        }

        if (args.length > 2 || first == null) {
            splash();

            return;
        }

        final Minecraft mc = Onepop.getMinecraft();
        boolean flag = mc.player.isRiding() || mc.player.getRidingEntity() != null;

        if (verify(first, "mount", "on")) {
            if (flag) {
                splash(ChatFormatting.RED + "Are you riding?");
            } else {
                Entity entity = Onepop.getEntityWorldManager().getEntity(250);

                if (entity == null) {
                    splash(ChatFormatting.RED + "Were you riding?");

                    return;
                }

                mc.player.startRiding(entity);

                splash("Mounted");

                Onepop.getEntityWorldManager().removeEntity(250);
            }
        }

        if (verify(first, "dismount", "off")) {
            if (flag) {
                Onepop.getEntityWorldManager().saveEntity(250, mc.player.getRidingEntity());
            } else {
                splash(ChatFormatting.RED + "Are you riding?");

                return;
            }

            mc.player.dismountRidingEntity();

            splash("Dismounted");
        }
    }
}
