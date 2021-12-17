package rina.onepop.club.client.command;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.rina.turok.util.TurokClass;
import net.minecraft.client.network.NetworkPlayerInfo;
import rina.onepop.club.Onepop;
import rina.onepop.club.api.command.Command;
import rina.onepop.club.api.social.Social;
import rina.onepop.club.api.social.management.SocialManager;
import rina.onepop.club.api.social.type.SocialType;
import rina.onepop.club.client.manager.network.PlayerServerManager;

/**
 * @author SrRina
 * @since 19/02/2021 at 11:16
 **/
public class CommandSocial extends Command {
    public CommandSocial() {
        super(new String[] {"friend", "enemy"}, "Command to add or remove friends/enemies.");
    }

    @Override
    public String setSyntax() {
        return "friend/enemy <list/clear> | <add/new/put/set/forced> <name> | <rem/remove/del/delete/unset> <name>";
    }

    @Override
    public void onCommand(String[] args) {
        String first = null;
        String second = null;

        if (args.length > 1) {
            first = args[1];
        }

        if (args.length > 2) {
            second = args[2];
        }

        if (args.length > 3) {
            splash();

            return;
        }

        if (first == null) {
            splash();

            return;
        }

        if (!verify(args[0], "friend", "enemy")) {
            splash();

            return;
        }

        if (verify(first, "add", "put", "set", "new", "forced")) {
            if (second == null) {
                splash();

                return;
            }

            NetworkPlayerInfo player = PlayerServerManager.get(second);

            if (player == null && !first.equalsIgnoreCase("forced")) {
                splash("Is the player online?");

                return;
            }

            if (player != null && SocialManager.get(player.getGameProfile().getName()) != null) {
                splash("Player already at social list");

                return;
            }

            Social user = new Social(first.equalsIgnoreCase("forced") ? second : player.getGameProfile().getName(), (SocialType) TurokClass.getEnumByName(SocialType.UNKNOWN, args[0].toUpperCase()));

            // Add on client.
            Onepop.getSocialManager().registry(user);

            splash("Added " + user.getName() + " " + args[0].toLowerCase());

            return;
        }

        if (verify(first, "rem", "remove", "delete", "del", "unset")) {
            Social social = SocialManager.get(second);

            if (social == null) {
                splash("Unknown friend or enemy");

                return;
            }

            splash("You removed " + social.getName());

            Onepop.getSocialManager().unregister(social);

            return;
        }

        if (verify(first, "list")) {
            StringBuilder stringBuilder = new StringBuilder();

            if (second == null) {
                if (Onepop.getSocialManager().getSocialList().isEmpty()) {
                    splash("Social list is empty!");

                    return;
                }

                splash("Type " + ChatFormatting.GREEN + "friend " + ChatFormatting.RED + "enemy");
            } else {
                if (verify(args[0], "friend", "enemy") == false) {
                    splash(ChatFormatting.RED + "friend/enemy");

                    return;
                }

                if (Onepop.getSocialManager().getSocialList().isEmpty()) {
                    splash("Social list is empty!");

                    return;
                }
            }

            for (Social social : Onepop.getSocialManager().getSocialList()) {
                if (second != null) {
                    if (args[0].equalsIgnoreCase("friend") && social.getType() == SocialType.ENEMY) {
                        continue;
                    }

                    if (args[0].equalsIgnoreCase("enemy") && social.getType() == SocialType.FRIEND) {
                        continue;
                    }
                }

                String name = "" + (social.getType() == SocialType.FRIEND ? ChatFormatting.GREEN + social.getName() : ChatFormatting.RED + social.getName()) + ChatFormatting.WHITE;

                stringBuilder.append(name + "; ");
            }

            if (stringBuilder.length() == 0) {
                splash("Social list is empty!");
            } else {
                splash(stringBuilder.toString());
            }

            return;
        }

        if (verify(first, "clear")) {
            if (Onepop.getSocialManager().getSocialList().isEmpty()) {
                splash("Social list is empty!");

                return;
            }

            this.print("Successfully cleaned social list!");

            return;
        }

        splash();
    }
}
