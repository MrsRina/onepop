package rina.onepop.club;

import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import rina.onepop.club.api.command.management.CommandManager;
import rina.onepop.club.api.component.Component;
import rina.onepop.club.api.component.management.ComponentManager;
import rina.onepop.club.api.engine.Engine;
import rina.onepop.club.api.event.management.EventManager;
import rina.onepop.club.api.module.Module;
import rina.onepop.club.api.module.management.ModuleManager;
import rina.onepop.club.api.preset.management.PresetManager;
import rina.onepop.club.api.social.management.SocialManager;
import rina.onepop.club.api.tracker.management.TrackerManager;
import rina.onepop.club.client.Wrapper;
import rina.onepop.club.client.command.*;
import rina.onepop.club.client.component.*;
import rina.onepop.club.client.gui.accountmanager.AccountManagerMotherFrame;
import rina.onepop.club.client.gui.module.ModuleClickGUI;
import rina.onepop.club.client.gui.overlay.ComponentClickGUI;
import rina.onepop.club.client.manager.chat.SpammerManager;
import rina.onepop.club.client.manager.entity.EntityWorldManager;
import rina.onepop.club.client.manager.network.HotBarManager;
import rina.onepop.club.client.manager.network.PlayerServerManager;
import rina.onepop.club.client.manager.network.RotationManager;
import rina.onepop.club.client.manager.network.TPSManager;
import rina.onepop.club.client.manager.overlay.NotificationManager;
import rina.onepop.club.client.manager.world.BlockManager;
import rina.onepop.club.client.manager.world.BreakManager;
import rina.onepop.club.client.manager.world.HoleManager;
import rina.onepop.club.client.module.client.ModuleGeneral;
import rina.onepop.club.client.module.client.ModuleHUD;
import rina.onepop.club.client.module.client.ModuleRPC;
import rina.onepop.club.client.module.client.ModuleTPSSync;
import rina.onepop.club.client.module.client.anticheat.ModuleAntiCheat;
import rina.onepop.club.client.module.client.developer.ModuleDeveloper;
import rina.onepop.club.client.module.combat.*;
import rina.onepop.club.client.module.combat.autocrystalrewrite.ModuleAutoCrystalRewrite;
import rina.onepop.club.client.module.combat.autotrap.ModuleAutoTrap;
import rina.onepop.club.client.module.combat.autoweb.ModuleAutoWeb;
import rina.onepop.club.client.module.combat.bedaura.ModuleBedAura;
import rina.onepop.club.client.module.combat.burrow.ModuleBurrow;
import rina.onepop.club.client.module.combat.critical.ModuleCritical;
import rina.onepop.club.client.module.combat.holefiller.ModuleHoleFiller;
import rina.onepop.club.client.module.combat.offhand.ModuleOffhand;
import rina.onepop.club.client.module.combat.quiver.ModuleQuiver;
import rina.onepop.club.client.module.combat.selftrap.ModuleSelfTrap;
import rina.onepop.club.client.module.combat.selfweb.ModuleSelfWeb;
import rina.onepop.club.client.module.combat.surround.ModuleSurround;
import rina.onepop.club.client.module.exploit.*;
import rina.onepop.club.client.module.misc.*;
import rina.onepop.club.client.module.misc.armoralert.ModuleArmorAlert;
import rina.onepop.club.client.module.misc.autoeat.ModuleAutoEat;
import rina.onepop.club.client.module.misc.autofish.ModuleAutoFish;
import rina.onepop.club.client.module.misc.middleclick.ModuleMiddleClick;
import rina.onepop.club.client.module.player.*;
import rina.onepop.club.client.module.player.autowalk.ModuleAutoWalk;
import rina.onepop.club.client.module.player.elyraflight.ModuleElytraFlight;
import rina.onepop.club.client.module.player.liquidspeed.ModuleLiquidSpeed;
import rina.onepop.club.client.module.player.longjump.ModuleLongJump;
import rina.onepop.club.client.module.player.phasewalk.ModulePhaseWalk;
import rina.onepop.club.client.module.player.reversestep.ModuleReverseStep;
import rina.onepop.club.client.module.player.strafe.ModuleStrafe;
import rina.onepop.club.client.module.player.straferewrite.ModuleStrafeRewrite;
import rina.onepop.club.client.module.render.*;
import rina.onepop.club.client.module.render.esp.*;
import rina.onepop.club.client.module.render.fullbright.ModuleFullBright;
import rina.onepop.club.client.rpc.RPC;
import team.stiff.pomelo.impl.annotated.AnnotatedEventManager;

import java.util.Comparator;

/**
 * @author SrRina
 * @since 15/11/20 at 4:51pm
 */
public class Onepop {
    public static final String NAME         = "onepop";
    public static final String VERSION     = "2";
    public static final String PATH_CONFIG = "onepop/";
    public static final String PATH_PRESET = PATH_CONFIG + "preset/";
    public static final String CHAT        = ChatFormatting.GRAY + NAME + " " + ChatFormatting.WHITE;

    /*
     * We create one final Minecraft, there is the function Minecraft or this variable;
     */
    public static final Minecraft MC = Minecraft.getMinecraft();

    /*
     * The event manager of team pomelo!!
     */
    private final team.stiff.pomelo.EventManager pomeloEventManager = new AnnotatedEventManager();

    /* API managers. */
    private TrackerManager trackerManager;
    private ModuleManager moduleManager;
    private EventManager clientEventManager;
    private CommandManager commandManager;
    private SocialManager socialManager;
    private PresetManager presetManager;
    private ComponentManager componentManager;

    /* Not API managers. */
    private SpammerManager spammerManager;
    private PlayerServerManager playerServerManager;
    private EntityWorldManager entityWorldManager;
    private HoleManager holeManager;
    private RotationManager rotationManager;
    private BlockManager blockManager;
    private BreakManager breakManager;
    private HotBarManager hotBarManager;
    private TPSManager tpsManager;
    private NotificationManager notificationManager;

    /* GUI screen stuff. */
    private ModuleClickGUI moduleClickGUI;
    private ComponentClickGUI componentClickGUI;

    private AccountManagerMotherFrame accountManagerMotherFrame;
    private Wrapper wrapper;

    /* Discord RPC */
    private RPC discordRPC;

    /**
     * Registry all components.
     */
    public void onRegistry() {
        // I use the event manager on managers also!
        this.pomeloEventManager.addEventListener(this.rotationManager);
        this.pomeloEventManager.addEventListener(this.presetManager);
        this.pomeloEventManager.addEventListener(this.hotBarManager);
        this.pomeloEventManager.addEventListener(this.breakManager);
        this.pomeloEventManager.addEventListener(this.blockManager);
        this.pomeloEventManager.addEventListener(this.tpsManager);

        // Category Client.
        this.moduleManager.registry(new rina.onepop.club.client.module.client.ModuleClickGUI());
        this.moduleManager.registry(new ModuleHUD());
        this.moduleManager.registry(new ModuleDeveloper());
        this.moduleManager.registry(new ModuleTPSSync());
        this.moduleManager.registry(new ModuleAntiCheat());
        this.moduleManager.registry(new ModuleRPC());
        this.moduleManager.registry(new ModuleGeneral());

        // Category Combat.
        this.moduleManager.registry(new ModuleAutoTrap());
        this.moduleManager.registry(new ModuleBowBomb());
        this.moduleManager.registry(new ModuleAutoCityMine());
        this.moduleManager.registry(new ModuleObsidianPlace());
        this.moduleManager.registry(new ModuleBurrow());
        this.moduleManager.registry(new ModuleAutoCrystalRewrite());
        this.moduleManager.registry(new ModuleOffhand());
        this.moduleManager.registry(new ModuleFastBow());
        this.moduleManager.registry(new ModuleAutoMinecartBomb());
        this.moduleManager.registry(new ModuleAutoArmour());
        this.moduleManager.registry(new ModuleQuiver());
        this.moduleManager.registry(new ModuleSurround());
        this.moduleManager.registry(new ModuleKillAura());
        this.moduleManager.registry(new ModuleCritical());
        this.moduleManager.registry(new ModuleHoleFiller());
        this.moduleManager.registry(new ModuleAutoLog());
        this.moduleManager.registry(new ModuleBedAura());
        this.moduleManager.registry(new ModuleAutoWeb());
        this.moduleManager.registry(new ModuleSelfWeb());
        this.moduleManager.registry(new ModuleSelfTrap());
        this.moduleManager.registry(new ModuleTrapCrystal());
        this.moduleManager.registry(new ModuleStrictTotem());

        // Category Render.
        this.moduleManager.registry(new ModuleAutoCrystalRender());
        this.moduleManager.registry(new ModuleBlockHighlight());
        this.moduleManager.registry(new ModuleHoleESP());
        this.moduleManager.registry(new ModuleFullBright());
        this.moduleManager.registry(new ModuleCustomCamera());
        this.moduleManager.registry(new ModulePortalESP());
        this.moduleManager.registry(new ModuleVoidESP());
        this.moduleManager.registry(new ModuleNameTags());
        this.moduleManager.registry(new ModuleEntityESP());
        this.moduleManager.registry(new ModulePlayerESP());
        //this.moduleManager.registry(new ModuleBacktrack());
        this.moduleManager.registry(new ModuleTracers());
        this.moduleManager.registry(new ModuleItemESP());
        this.moduleManager.registry(new ModuleCustomHandView());
        this.moduleManager.registry(new ModuleNoRender());
        //this.moduleManager.registry(new ModuleWaypoints());
        this.moduleManager.registry(new ModuleFeetHighlight());
        this.moduleManager.registry(new ModuleBurrowESP());
        this.moduleManager.registry(new ModuleStorageESP());

        // Player
        this.moduleManager.registry(new ModuleElytraFlight());
        this.moduleManager.registry(new ModuleBurrowVanilla());
        this.moduleManager.registry(new ModuleCreativeFly());
        this.moduleManager.registry(new ModuleSafeWalk());
        this.moduleManager.registry(new ModuleHolePusher());
        this.moduleManager.registry(new ModuleAutoWalk());
        this.moduleManager.registry(new ModuleInventoryWalk());
        this.moduleManager.registry(new ModuleVelocity());
        this.moduleManager.registry(new ModuleAirJump());
        this.moduleManager.registry(new ModuleFlight());
        this.moduleManager.registry(new ModuleScaffold());
        this.moduleManager.registry(new ModuleAntiVoid());
        //this.moduleManager.registry(new ModuleJesus());
        this.moduleManager.registry(new ModuleStep());
        this.moduleManager.registry(new ModuleLiquidSpeed());
        this.moduleManager.registry(new ModuleReverseStep());
        this.moduleManager.registry(new ModuleStrafe());
        this.moduleManager.registry(new ModuleNoSlowDown());
        this.moduleManager.registry(new ModuleNoFall());
        this.moduleManager.registry(new ModulePortalGodMode());
        this.moduleManager.registry(new ModuleStrafeRewrite());
        this.moduleManager.registry(new ModuleSprint());
        this.moduleManager.registry(new ModuleLongJump());
        this.moduleManager.registry(new ModulePhaseWalk());
        this.moduleManager.registry(new ModuleWeb());

        // Category Misc.
        this.moduleManager.registry(new ModuleBetterMine());
        this.moduleManager.registry(new ModuleNoInteract());
        this.moduleManager.registry(new ModuleBetterExperience());
        this.moduleManager.registry(new ModuleNetherreckNuker());
        this.moduleManager.registry(new ModuleAutoRefill());
        this.moduleManager.registry(new ModuleAutoKillMessage());
        this.moduleManager.registry(new ModuleAutoMine());
        this.moduleManager.registry(new ModuleHotBarAlert());
        this.moduleManager.registry(new ModuleSwitch());
        this.moduleManager.registry(new ModulePortalGUI());
        this.moduleManager.registry(new ModuleNoBreakAnimation());
        this.moduleManager.registry(new ModuleAntiHunger());
        this.moduleManager.registry(new ModuleArmorAlert());
        this.moduleManager.registry(new ModuleNoServerSwing());
        this.moduleManager.registry(new ModuleBreakAlert());
        this.moduleManager.registry(new ModuleMiddleClick());
        this.moduleManager.registry(new ModuleFreecam());
        this.moduleManager.registry(new ModuleMultitask());
        this.moduleManager.registry(new ModuleAutoRespawn());
        this.moduleManager.registry(new ModuleAutoFish());
        this.moduleManager.registry(new ModuleChatSuffix());
        this.moduleManager.registry(new ModuleSpammer());
        this.moduleManager.registry(new ModuleAntiAFK());
        this.moduleManager.registry(new ModuleTimer());
        this.moduleManager.registry(new ModuleAutoEat());
        this.moduleManager.registry(new ModuleNoEntityTrace());
        this.moduleManager.registry(new ModuleNegro());
        this.moduleManager.registry(new ModuleEntityControl());
        this.moduleManager.registry(new ModuleSearch());
        this.moduleManager.registry(new ModuleBuildHeight());
        this.moduleManager.registry(new ModuleBlink());
        this.moduleManager.registry(new ModuleFastUse());

        // Exploit.
        //this.moduleManager.registry(new ModuleNoServerRotate());
        this.moduleManager.registry(new ModuleExtraSlots());
        this.moduleManager.registry(new ModuleSilentEat());
        //this.moduleManager.registry(new ModuleChorusTweaks());
        //this.moduleManager.registry(new ModuleBetterPackets());
        //this.moduleManager.registry(new ModulePhase());
        this.moduleManager.registry(new ModuleBoatFly());
        this.moduleManager.registry(new ModuleNoEatFall());
        //this.moduleManager.registry(new ModuleNoPlaceDelay());
        //this.moduleManager.registry(new ModuleNoEatDelay());
        this.moduleManager.registry(new ModuleCancelPackets());
        // this.moduleManager.registry(new ModuleAutoHat()); || Deprecated module.
        //this.moduleManager.registry(new ModulePingSpoof());

        // Commands.
        this.commandManager.registry(new CommandPrefix());
        this.commandManager.registry(new CommandToggle());
        this.commandManager.registry(new CommandCoords());
        this.commandManager.registry(new CommandSocial());
        this.commandManager.registry(new CommandVanish());
        this.commandManager.registry(new CommandPreset());

        // Components.
        this.componentManager.registry(new ComponentCrystalsPerSecond());
        this.componentManager.registry(new ComponentArmor());
        this.componentManager.registry(new ComponentArrayList());
        this.componentManager.registry(new ComponentCoordinates());
        this.componentManager.registry(new ComponentCrystalCount());
        this.componentManager.registry(new ComponentBottleXPCount());
        this.componentManager.registry(new ComponentSpeedMeter());
        this.componentManager.registry(new ComponentFPS());
        this.componentManager.registry(new ComponentGoldenAppleCount());
        this.componentManager.registry(new ComponentSpectatorList());
        this.componentManager.registry(new ComponentInventory());
        this.componentManager.registry(new ComponentPing());
        this.componentManager.registry(new ComponentTotemCount());
        this.componentManager.registry(new ComponentTPS());
        this.componentManager.registry(new ComponentWatermark());
        this.componentManager.registry(new ComponentWelcome());
        // this.componentManager.registry(new ComponentNotification());

        // We organize module list and component list to alphabetical order.
        this.moduleManager.getModuleList().sort(Comparator.comparing(Module::getName));
        this.componentManager.getComponentList().sort(Comparator.comparing(Component::getName));
    }

    /**
     * Method non-static to init the client.
     */
    public void onInitClient() {
        //auth.shutdown();

        // Start here the classes!
        this.moduleClickGUI = new ModuleClickGUI();
        this.componentClickGUI = new ComponentClickGUI();

        startup();

        // For load after!
        this.moduleClickGUI.init();
        this.componentClickGUI.init();

        // Reload shit.
        ModuleManager.refresh();
        ModuleManager.reload();

        // Update the font from HUD.
        ModuleHUD.INSTANCE.onRefreshFont();
    }

    public static void startup() {
        Onepop.getSocialManager().onLoad();

        PresetManager.refresh();
        PresetManager.reload();
        PresetManager.process(PresetManager.LOAD);

        ComponentManager.INSTANCE.onLoadList();
    }

    public static void shutdown() {
        for (Module modules : Onepop.getModuleManager().getModuleList()) {
            modules.onShutdown();
        }

        Onepop.getComponentManager().onSaveList();
        Onepop.getSocialManager().onSave();

        PresetManager.reload();
        PresetManager.process(PresetManager.DATA);
        PresetManager.process(PresetManager.SAVE);
    }

    @Mod.EventHandler
    public void onClientStarted(FMLPreInitializationEvent event) {
        // Init the engine for render.
        Engine.initialize();

        // Init all managers very importants for onepop.
        this.trackerManager = new TrackerManager();
        this.moduleManager = new ModuleManager();
        this.clientEventManager = new EventManager();
        this.commandManager = new CommandManager();
        this.socialManager = new SocialManager();
        this.presetManager = new PresetManager();
        this.componentManager = new ComponentManager();
        this.spammerManager = new SpammerManager();
        this.playerServerManager = new PlayerServerManager();
        this.entityWorldManager = new EntityWorldManager();
        this.holeManager = new HoleManager();
        this.rotationManager = new RotationManager();
        this.blockManager = new BlockManager();
        this.breakManager = new BreakManager();
        this.discordRPC = new RPC();
        this.hotBarManager = new HotBarManager();
        this.tpsManager = new TPSManager();
        this.notificationManager = new NotificationManager();

        this.wrapper = new Wrapper();

        MinecraftForge.EVENT_BUS.register(this.clientEventManager);
        MinecraftForge.EVENT_BUS.register(this.commandManager);

        this.onRegistry();
        this.onInitClient();

        if (ModuleRPC.INSTANCE.isEnabled()) {
            discordRPC.run();
        }

        /*
         * Mixin is bad.
         */
        Runtime.getRuntime().addShutdownHook(new Thread("Onepop Shutdown Hook") {
            @Override
            public void run() {
                Onepop.shutdown();
            }
        });
    }

    public static team.stiff.pomelo.EventManager getPomeloEventManager() {
        return OnepopMod.INSTANCE.pomeloEventManager;
    }

    public static TrackerManager getTrackerManager() {
        return OnepopMod.INSTANCE.trackerManager;
    }

    public static ModuleManager getModuleManager() {
        return OnepopMod.INSTANCE.moduleManager;
    }

    public static EventManager getClientEventManager() {
        return OnepopMod.INSTANCE.clientEventManager;
    }

    public static CommandManager getCommandManager() {
        return OnepopMod.INSTANCE.commandManager;
    }

    public static SocialManager getSocialManager() {
        return OnepopMod.INSTANCE.socialManager;
    }

    public static PresetManager getPresetManager() {
        return OnepopMod.INSTANCE.presetManager;
    }

    public static ComponentManager getComponentManager() {
        return OnepopMod.INSTANCE.componentManager;
    }

    public static ModuleClickGUI getModuleClick() {
        return OnepopMod.INSTANCE.moduleClickGUI;
    }

    public static ComponentClickGUI getComponentClickGUI() {
        return OnepopMod.INSTANCE.componentClickGUI;
    }

    public static SpammerManager getSpammerManager() {
        return OnepopMod.INSTANCE.spammerManager;
    }

    public static PlayerServerManager getPlayerServerManager() {
        return OnepopMod.INSTANCE.playerServerManager;
    }

    public static EntityWorldManager getEntityWorldManager() {
        return OnepopMod.INSTANCE.entityWorldManager;
    }

    public static HoleManager getHoleManager() {
        return OnepopMod.INSTANCE.holeManager;
    }

    public static RotationManager getRotationManager() {
        return OnepopMod.INSTANCE.rotationManager;
    }

    public static BlockManager getBlockManager() {
        return OnepopMod.INSTANCE.blockManager;
    }

    public static BreakManager getBreakManager() {
        return OnepopMod.INSTANCE.breakManager;
    }

    public static RPC getRPC() {
        return OnepopMod.INSTANCE.discordRPC;
    }

    public static HotBarManager getHotBarManager() {
        return OnepopMod.INSTANCE.hotBarManager;
    }

    public static TPSManager getTPSManager() {
        return OnepopMod.INSTANCE.tpsManager;
    }

    public static NotificationManager getNotificationManager() {
        return OnepopMod.INSTANCE.notificationManager;
    }

    public static Wrapper getWrapper() {
        return OnepopMod.INSTANCE.wrapper;
    }

    public static Minecraft getMinecraft() {
        return MC;
    }
}