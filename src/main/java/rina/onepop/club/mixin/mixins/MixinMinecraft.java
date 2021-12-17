package rina.onepop.club.mixin.mixins;

import rina.onepop.club.Onepop;
import rina.onepop.club.client.event.client.RunTickEvent;
import rina.onepop.club.client.event.underground.DisplayGuiScreenEvent;
import rina.onepop.club.client.module.misc.ModuleMultitask;
import rina.onepop.club.mixin.interfaces.IMinecraft;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import org.lwjgl.opengl.Display;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author SrRina
 * @since 16/11/20 at 10:05pm
 */

// TODO: Something here is breaking compatiblity with future
@Mixin(Minecraft.class)
public abstract class MixinMinecraft implements IMinecraft {
    @Shadow
    public EntityPlayerSP player;

    @Shadow public PlayerControllerMP playerController;

    @Redirect(at = @At(
        value = "INVOKE",
        target = "Lorg/lwjgl/opengl/Display;setTitle(Ljava/lang/String;)V"),
        method = "createDisplay")
    public void
    createDisplay(String name) {
        Display.setTitle(Onepop.NAME + " " + Onepop.VERSION);
    }

    @Inject(method = "displayGuiScreen", at = @At("RETURN"))
    public void onDisplayGUIScreen(GuiScreen guiScreenIn, CallbackInfo ci) {
        final DisplayGuiScreenEvent event = new DisplayGuiScreenEvent(guiScreenIn);

        Onepop.getPomeloEventManager().dispatchEvent(event);
    }

    @Accessor
    @Override
    public abstract void setRightClickDelayTimer(int delay);

    @Inject(method = "runTick", at = @At("HEAD"))
    private void onTick(CallbackInfo callbackInfo) {
        Onepop.getPomeloEventManager().dispatchEvent(new RunTickEvent());
    }

    @Inject(method = "shutdown", at = @At("HEAD"))
    private void onShutDown(CallbackInfo ci) {
        Onepop.shutdown();
    }

    @Redirect(method = "sendClickBlockToController", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/EntityPlayerSP;isHandActive()Z"))
    private boolean isHandActive(EntityPlayerSP player){
        return !ModuleMultitask.INSTANCE.isEnabled() && this.player.isHandActive();
    }

    @Redirect(method = "rightClickMouse", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/PlayerControllerMP;getIsHittingBlock()Z"))
    private boolean isHittingBlock(PlayerControllerMP playerControllerMP){
        return !ModuleMultitask.INSTANCE.isEnabled() && this.playerController.getIsHittingBlock();
    }
}
