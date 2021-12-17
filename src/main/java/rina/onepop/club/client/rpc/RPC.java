package rina.onepop.club.client.rpc;

import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRPC;
import club.minnced.discord.rpc.DiscordRichPresence;
import rina.onepop.club.Onepop;
import rina.onepop.club.client.module.client.ModuleRPC;
import net.minecraft.client.Minecraft;

import java.util.Objects;
public class RPC {
    public final DiscordRPC discordRPC;
    public       DiscordRichPresence discordPresence;

    public final Minecraft mc = Minecraft.getMinecraft();

    public String detailOption1;
    public String detailOption2;
    public String detailOption3;
    public String detailOption4;

    // State.
    public String stateOption1;
    public String stateOption2;
    public String stateOption3;
    public String stateOption4;

    public RPC() {
        this.discordRPC = DiscordRPC.INSTANCE;
        this.discordPresence = new DiscordRichPresence();

        this.detailOption1 = "";
        this.detailOption2 = "";
        this.detailOption3 = "";
        this.detailOption4 = "";

        this.stateOption1 = "";
        this.stateOption2 = "";
        this.stateOption3 = "";
        this.stateOption4 = "";
    }

    public void stop() {
        this.discordRPC.Discord_Shutdown();
    }

    public void run() {
        this.discordPresence = new DiscordRichPresence();

        final DiscordEventHandlers handler_ = new DiscordEventHandlers();

        this.discordRPC.Discord_Initialize("519692561443717130", handler_, true, "");

        this.discordPresence.largeImageText = "1pop" + " " + Onepop.VERSION;
        this.discordPresence.largeImageKey  = "discord";

        new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    if (mc.world == null) {
                        this.detailOption1 = "";
                        this.detailOption2 = "sleeping on da 1pop.club";
                        this.stateOption1 = "zzz";
                    } else {
                        this.detailOption1 = "";

                        if (mc.isIntegratedServerRunning()) {
                            this.detailOption2 = "1popping some nn's";
                            this.stateOption1 = "get 1popped kiddo";
                        } else if (ModuleRPC.showName.getValue()){
                            this.detailOption2 = "1popping at " + Objects.requireNonNull(mc.getCurrentServerData()).serverIP;
                            this.stateOption1 = mc.player.getName() + " 1pop.club hittin' p100";
                        } else {
                            this.detailOption2 = "1popping at " + Objects.requireNonNull(mc.getCurrentServerData()).serverIP;
                            this.stateOption1 ="1pop.club hittin' p100";
                        }
                    }

                    String detail = this.detailOption1 + this.detailOption2 + this.detailOption3 + this.detailOption4;
                    String state  = this.stateOption1 + this.stateOption2 + this.stateOption3 + this.stateOption4;

                    this.discordRPC.Discord_RunCallbacks();

                    this.discordPresence.details = detail;
                    this.discordPresence.state = state;

                    this.discordRPC.Discord_UpdatePresence(this.discordPresence);
                } catch (Exception exc) {
                    exc.printStackTrace();
                }

                try {
                    Thread.sleep(4000L);
                }

                catch (InterruptedException exc_) {
                    exc_.printStackTrace();
                }
            }
        }, "RPC-Callback-Handler").start();
    }

    public String set(String presume) {
        return " " + presume;
    }
}
