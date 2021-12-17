package rina.onepop.club.client.module.misc;

import com.mojang.realmsclient.gui.ChatFormatting;
import rina.onepop.club.Onepop;
import rina.onepop.club.api.ISLClass;
import rina.onepop.club.api.module.Module;
import rina.onepop.club.api.module.impl.ModuleCategory;
import rina.onepop.club.api.module.registry.Registry;
import rina.onepop.club.api.util.chat.ChatUtil;
import rina.onepop.club.client.event.client.ClientTickEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

import java.util.ArrayList;
import java.util.List;

@Registry(name = "Visual Range", tag = "VisualRange", description = "Get players on visual range.", category = ModuleCategory.RENDER)
public class ModuleVisualRange extends Module {
    private List<String> people;

    @Override
    public void onEnable() {
        people = new ArrayList<>();
    }

    @Listener
    public void onUpdate(ClientTickEvent event) {
        if (ISLClass.mc.world == null | ISLClass.mc.world == null) {
            return;
        }

        List<String> players = new ArrayList<>();
        List<EntityPlayer> playerEntities = ISLClass.mc.world.playerEntities;

        for (Entity e : playerEntities) {
            if (e.getName().equals(Onepop.MC.player.getName())) continue;
            players.add(e.getName());
        }

        // Jake stop skid from bubby client. (joke (joke (joke (joke (crystalinqq)))))
        //this was not a skid
        if (players.size() > 0) {
            for (String name : players) {
                if (!people.contains(name)) {
                    ChatUtil.print(ChatFormatting.GRAY + name + ChatFormatting.RESET + "Is now your visual range");
                    }
                    people.add(name); // My son.
                }
            }
        }
    }
