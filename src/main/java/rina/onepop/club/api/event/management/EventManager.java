package rina.onepop.club.api.event.management;

import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraftforge.client.event.*;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import rina.onepop.club.Onepop;
import rina.onepop.club.api.command.Command;
import rina.onepop.club.api.command.management.CommandManager;
import rina.onepop.club.api.component.Component;
import rina.onepop.club.api.module.Module;
import rina.onepop.club.api.setting.value.InputType;
import rina.onepop.club.api.util.chat.ChatUtil;
import rina.onepop.club.api.util.client.NullUtil;
import rina.onepop.club.client.event.client.ClientTickEvent;
import rina.onepop.club.client.module.client.ModuleHUD;

import java.awt.*;

import static rina.onepop.club.Onepop.MODULE_AUTO_CRYSTAL_REWRITE;

/**
 * @author SrRina
 * @since 15/11/20 at 7:45pm
 */
public class EventManager {
    private float currentRender2DPartialTicks;
    private float currentRender3DPartialTicks;

    private int[] currentRGBColor = {0, 0, 0};

    protected void setCurrentRender2DPartialTicks(float currentRender2DPartialTicks) {
        this.currentRender2DPartialTicks = currentRender2DPartialTicks;
    }

    public float getCurrentRender2DPartialTicks() {
        return currentRender2DPartialTicks;
    }

    protected void setCurrentRender3DPartialTicks(float currentRender3DPartialTicks) {
        this.currentRender3DPartialTicks = currentRender3DPartialTicks;
    }

    public float getCurrentRender3DPartialTicks() {
        return currentRender3DPartialTicks;
    }

    private void setCurrentRGBColor(int[] currentRGBColor) {
        this.currentRGBColor = currentRGBColor;
    }

    public int[] getCurrentRGBColor() {
        return currentRGBColor;
    }

    @SubscribeEvent
    public void onLivingUseItem(LivingEntityUseItemEvent event) {
        Onepop.getPomeloEventManager().dispatchEvent(event);
    }

    @SubscribeEvent
    public void onUpdate(LivingEvent.LivingUpdateEvent event) {
        if (event.isCanceled()) return;
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (NullUtil.isPlayer()) {
            return;
        }

        if (MODULE_AUTO_CRYSTAL_REWRITE.isEnabled()) {
            MODULE_AUTO_CRYSTAL_REWRITE.onDirectTick();
        }

        Onepop.getPomeloEventManager().dispatchEvent(new ClientTickEvent());
        Onepop.getSpammerManager().onUpdateAll();
        Onepop.getTrackerManager().onUpdateAll();
        Onepop.getPlayerServerManager().onUpdateAll();
        Onepop.getHoleManager().onUpdateAll();
        Onepop.getRotationManager().onUpdateAll();
        Onepop.getBlockManager().onUpdateAll();
        Onepop.getBreakManager().onUpdateAll();
        Onepop.getHotBarManager().onUpdateAll();
        Onepop.getNotificationManager().onUpdateAll();

        for (Module modules : Onepop.getModuleManager().getModuleList()) {
            modules.onSync();
            modules.onSetting();
        }
    }

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent event) {
        if (Onepop.MC.player == null) {
            return;
        }

        this.setCurrentRender2DPartialTicks(event.getPartialTicks());

        RenderGameOverlayEvent.ElementType target = RenderGameOverlayEvent.ElementType.ALL;

        if (!Onepop.getMinecraft().player.isCreative() && Onepop.getMinecraft().player.getRidingEntity() instanceof AbstractHorse) {
            target = RenderGameOverlayEvent.ElementType.HEALTHMOUNT;
        }

        if (event.getType() != target) {
            return;
        }

        for (Module modules : Onepop.getModuleManager().getModuleList()) {
            if (modules.isEnabled()) {
                modules.onRender2D();

                GL11.glPushMatrix();

                GL11.glEnable(GL11.GL_TEXTURE_2D);
                GL11.glEnable(GL11.GL_BLEND);

                GlStateManager.enableBlend();

                GL11.glPopMatrix();

                GlStateManager.enableCull();
                GlStateManager.depthMask(true);
                GlStateManager.enableTexture2D();
                GlStateManager.enableBlend();
                GlStateManager.enableDepth();
            }
        }

        boolean flag = !Onepop.getComponentClickGUI().isClosingGUI();

        for (Component components : Onepop.getComponentManager().getComponentList()) {
            if (flag) {
                if (components.isEnabled() && ModuleHUD.settingRender.getValue()) {
                    components.onRender();

                    GL11.glPushMatrix();

                    GL11.glEnable(GL11.GL_TEXTURE_2D);
                    GL11.glEnable(GL11.GL_BLEND);

                    GlStateManager.enableBlend();

                    GL11.glPopMatrix();

                    GlStateManager.enableCull();
                    GlStateManager.depthMask(true);
                    GlStateManager.enableTexture2D();
                    GlStateManager.enableBlend();
                    GlStateManager.enableDepth();
                }

                components.cornerDetector();
            }
        }

        // We apply the dock push function here to best smooth!
        Onepop.getComponentManager().onCornerDetectorComponentList(this.currentRender2DPartialTicks);
    }

    @SubscribeEvent
    public void onWorldRender(RenderWorldLastEvent event) {
        if (event.isCanceled()) {
            return;
        }

        this.setCurrentRender3DPartialTicks(event.getPartialTicks());

        float[] currentSystemCycle = {
                (System.currentTimeMillis() % (360 * 32)) / (360f * 32f)
        };

        int currentColorCycle = Color.HSBtoRGB(currentSystemCycle[0], 1, 1);

        this.currentRGBColor = new int[] {
                ((currentColorCycle >> 16) & 0xFF),
                ((currentColorCycle >> 8) & 0xFF),
                (currentColorCycle & 0xFF)
        };

        /*
         * Basically the ticks are more smooth in event RenderWorldLastEvent;
         * This make any color update as the color fully smooth.
         * And we update the colors of the GUI too.
         */
        Onepop.getWrapper().onUpdateColor();

        if (MODULE_AUTO_CRYSTAL_REWRITE.isEnabled()) {
            MODULE_AUTO_CRYSTAL_REWRITE.onDirectDraw3D();
        }

        for (Module modules : Onepop.getModuleManager().getModuleList()) {
            if (modules.isEnabled()) {
                modules.onRender3D();
            }
        }
    }

    @SubscribeEvent()
    public void onAttackingDamage(LivingHurtEvent event) {
        Onepop.getPomeloEventManager().dispatchEvent(event);
    }

    @SubscribeEvent()
    public void onInputUpdate(InputUpdateEvent event) {
        Onepop.getPomeloEventManager().dispatchEvent(event);
    }

    @SubscribeEvent
    public void onPlayerSPPushOutOfBlocksEvent(PlayerSPPushOutOfBlocksEvent event) {
        Onepop.getPomeloEventManager().dispatchEvent(event);
    }

    @SubscribeEvent(priority = EventPriority.HIGH, receiveCanceled = true)
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        Onepop.getModuleManager().onInput(Keyboard.getEventKey(), InputType.KEYBOARD);
    }

    @SubscribeEvent(priority = EventPriority.HIGH, receiveCanceled = true)
    public void onMouse(InputEvent.MouseInputEvent event) {
        Onepop.getModuleManager().onInput(Mouse.getEventButton(), InputType.MOUSE);
    }

    @SubscribeEvent(priority = EventPriority.NORMAL)
    public void onChat(ClientChatEvent event) {
        String message = event.getMessage();
        String currentPrefix = CommandManager.getCommandPrefix().getPrefix();

        if (message.startsWith(currentPrefix)) {
            event.setCanceled(true);

            ChatUtil.malloc(message);

            String[] args = Onepop.getCommandManager().split(message);

            boolean isCommand = false;

            for (Command commands : Onepop.getCommandManager().getCommandList()) {
                Command commandRequested = CommandManager.get(args[0]);

                if (commandRequested != null) {
                    commandRequested.onCommand(args);

                    isCommand = true;

                    break;
                }
            }

            if (!isCommand) {
                ChatUtil.print(Onepop.CHAT + ChatFormatting.RED + "Unknown command. Try help for a list commands");
            }
        }
    }
}
