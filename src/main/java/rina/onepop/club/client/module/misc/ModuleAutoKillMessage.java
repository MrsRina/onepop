package rina.onepop.club.client.module.misc;

import com.mojang.realmsclient.gui.ChatFormatting;
import rina.onepop.club.Onepop;
import rina.onepop.club.api.module.Module;
import rina.onepop.club.api.module.impl.ModuleCategory;
import rina.onepop.club.api.module.registry.Registry;
import rina.onepop.club.api.setting.value.ValueNumber;
import rina.onepop.club.api.social.Social;
import rina.onepop.club.api.social.management.SocialManager;
import rina.onepop.club.api.social.type.SocialType;
import rina.onepop.club.api.util.chat.ChatUtil;
import rina.onepop.club.api.util.client.NullUtil;
import rina.onepop.club.api.util.file.FileUtil;
import rina.onepop.club.client.event.client.RunTickEvent;
import rina.onepop.club.client.event.network.PacketEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.SPacketDestroyEntities;
import rina.onepop.club.api.ISLClass;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;

/**
 * @author SrRina
 * @since 15/05/2021 at 17:15
 **/
@Registry(name = "Auto-Kill Message", tag = "AutoKillMessage", description = "Automatically send one message when you kill some player.", category = ModuleCategory.MISC)
public class ModuleAutoKillMessage extends Module {
    /* Misc. */
    public static ValueNumber settingRangeOut = new ValueNumber("Range Out", "RangeOut", "Removes target from client if is out of range.", 13f, 19f, 1f);

    private EntityPlayer targetPlayer;
    private String[] killMessages;

    private boolean sendMessage;

    @Override
    public void onSetting() {
    }

    @Override
    public void onEnable() {
        this.doLoadMessageKill();
    }

    @Override
    public void onDisable() {
    }

    @Listener
    public void onPacketReceive(PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketDestroyEntities && this.targetPlayer != null) {
            final SPacketDestroyEntities packets = (SPacketDestroyEntities) event.getPacket();

            for (int entityIds : packets.getEntityIDs()) {
                final Entity entity = ISLClass.mc.world.getEntityByID(entityIds);

                if (!(entity instanceof EntityPlayer)) {
                    continue;
                }

                final EntityPlayer player = (EntityPlayer) entity;

                if (player.getHealth() > 0f) {
                    continue;
                }

                if (entityIds == this.targetPlayer.getEntityId()) {
                    this.sendMessage = true;

                    break;
                }
            }
        }
    }

    @Listener
    public void onTick(RunTickEvent event) {
        if (NullUtil.isPlayerWorld()) {
            return;
        }

        /*
         * I don't think good uses target from auto crystal or kill aura, because... the target at range is the all targets!
         */
        EntityPlayer onTarget = null;

        for (EntityPlayer entities : ISLClass.mc.world.playerEntities) {
            if (entities == ISLClass.mc.player) {
                continue;
            }

            // If anyone setting is true, any people on chunk that dies, the module will send message!!
            if (ISLClass.mc.player.getDistance(entities) >= settingRangeOut.getValue().floatValue()) {
                continue;
            }

            final Social social = SocialManager.get(entities.getName());

            if (social != null && social.getType() == SocialType.FRIEND) {
                if (social.getType() == SocialType.FRIEND) {
                    continue;
                }

                if (social.getType() == SocialType.ENEMY) {
                    onTarget = entities;

                    break;
                }
            }

            onTarget = entities;
        }

        /*
         * I need made one delay for it, I can't repeat messages... or no!
         */
        if (this.sendMessage && this.targetPlayer != null) {
            String message = this.chooseMessageKill().replaceAll("<player>", this.targetPlayer.getName());

            if (!message.isEmpty()) {
                ChatUtil.message(message);
            }

            this.targetPlayer = null;
            this.sendMessage = false;
        } else {
            // Just update when the message is not able to send!
            this.targetPlayer = onTarget;
        }
    }

    public void doLoadMessageKill() {
        final String name = Onepop.PATH_CONFIG + "KillMessages" + ".txt";
        final Path path = Paths.get(name);

        try {
            FileUtil.createFolderIfNeeded(Paths.get(Onepop.PATH_CONFIG));
            FileUtil.createFileIfNeeded(path);

            List<String> loadedFileLines = Files.readAllLines(path);

            if (loadedFileLines.isEmpty()) {
                final FileWriter file = new FileWriter(name);

                for (String messagesByDefault : new String[] {"Good game <player>!", "Good game <player>, buy onepop!", "<player> you just got irradiated by 1pop - discord.gg/S3DDhc3qNW"}) {
                    file.write(messagesByDefault + "\r\n");
                }

                file.close();

                this.print(ChatFormatting.YELLOW + "File is empty, enable module again for set default messages.");
                this.setDisabled();

                return;
            }

            this.killMessages = new String[loadedFileLines.size()];

            int index = 0;

            for (String lines : loadedFileLines) {
                if (lines.isEmpty()) {
                    continue;
                }

                this.killMessages[index] = lines;

                index++;
            }
        } catch (IOException exc) {
            this.print(ChatFormatting.RED + "A error occurred in messages kill load file.");

            return;
        }

        this.print(ChatFormatting.GREEN + "Successfully loaded messages kill!");
    }

    public String chooseMessageKill() {
        // Obvious, I wont send "" for the chat!
        if (this.killMessages.length == 0) {
            this.print(ChatFormatting.RED + "The data length of kill messages are 0, or the file is corrupted.");

            return "";
        }

        return this.killMessages[new Random().nextInt(this.killMessages.length)];
    }
}
