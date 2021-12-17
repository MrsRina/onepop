package rina.onepop.club.client.module.render;

import me.rina.turok.render.opengl.TurokGL;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.EnumAction;
import net.minecraft.util.EnumHand;
import rina.onepop.club.api.module.Module;
import rina.onepop.club.api.module.impl.ModuleCategory;
import rina.onepop.club.api.module.registry.Registry;
import rina.onepop.club.api.setting.value.ValueBoolean;
import rina.onepop.club.api.setting.value.ValueNumber;
import rina.onepop.club.client.event.render.EnumHandSideEvent;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

/**
 * @author SrRina
 * @since 13/07/2021 at 01:15
 **/
@Registry(name = "Custom Hand View", tag = "CustomHandView", description = "Customize camera hands.", category = ModuleCategory.RENDER)
public class ModuleCustomHandView extends Module {
    public static ValueBoolean settingUsingItemAnimation = new ValueBoolean("Using Item Animation", "UsingItemAnimation", "Cancel or no animation.", true);

    public static ValueBoolean settingRight = new ValueBoolean("Right", "Right", "Right", false);
    public static ValueNumber settingRightX = new ValueNumber("Right X", "RightX", "Changes the x value.", 0.0, -50.0, 50.0);
    public static ValueNumber settingRightY = new ValueNumber("Right Y", "RightY", "Changes the y value.", 0.0, -50.0, 50.0);
    public static ValueNumber settingRightZ = new ValueNumber("Right Z", "RightZ", "Changes the z value.", 0.0, -50.0, 50.0);

    public static ValueNumber settingRightYaw = new ValueNumber("Right Yaw","RightYaw","Changes the yaw of the item.",0,-100,100);
    public static ValueNumber settingRightPitch = new ValueNumber("Right Pitch","RightPitch","Changes the pitch of the item.",0,-100,100);
    public static ValueNumber settingRightRoll = new ValueNumber("Right Roll","RightRoll","Changes the roll of the item.",0,-100,100);

    public static ValueNumber settingScaleRight = new ValueNumber("Scale Right", "ScaleRight", "Changes the scale.", 10, 0, 50);

    /* Misc left hand. */
    public static ValueBoolean settingLeft = new ValueBoolean("Left", "Left", "Left", false);
    public static ValueNumber settingLeftX = new ValueNumber("Left X", "LeftX", "Changes the x value.", 0.0, -50.0, 50.0);
    public static ValueNumber settingLeftY = new ValueNumber("Left Y", "LeftY", "Changes the y value.", 0.0, -50.0, 50.0);
    public static ValueNumber settingLeftZ = new ValueNumber("Left Z", "LeftZ", "Changes the z value.", 0.0, -50.0, 50.0);

    public static ValueNumber settingLeftYaw = new ValueNumber("Left Yaw","LeftYaw","Changes the yaw of the item.",0,-100,100);
    public static ValueNumber settingLeftPitch = new ValueNumber("Left Pitch","LeftPitch","Changes the pitch of the item.",0,-100,100);
    public static ValueNumber settingLeftRoll = new ValueNumber("Left Roll","LeftRoll","Changes the roll of the item.",0,-100,100);

    public static ValueNumber settingScaleLeft = new ValueNumber("Scale Left", "ScaleLeft", "Changes the scale.", 10, 0, 50);

    private boolean isUsingAnItem;

    @Override
    public void onSetting() {
        settingRightX.setEnabled(settingRight.getValue());
        settingRightY.setEnabled(settingRight.getValue());
        settingRightZ.setEnabled(settingRight.getValue());
        settingRightYaw.setEnabled(settingRight.getValue());
        settingRightPitch.setEnabled(settingRight.getValue());
        settingRightRoll.setEnabled(settingRight.getValue());
        settingScaleRight.setEnabled(settingRight.getValue());

        settingLeftX.setEnabled(settingLeft.getValue());
        settingLeftY.setEnabled(settingLeft.getValue());
        settingLeftZ.setEnabled(settingLeft.getValue());
        settingLeftYaw.setEnabled(settingLeft.getValue());
        settingLeftPitch.setEnabled(settingLeft.getValue());
        settingLeftRoll.setEnabled(settingLeft.getValue());
        settingScaleLeft.setEnabled(settingLeft.getValue());
    }

    @Listener
    public void onEnumHandSideEvent(EnumHandSideEvent event) {
        this.isUsingAnItem = settingUsingItemAnimation.getValue() && mc.gameSettings.keyBindUseItem.isKeyDown() && (mc.player.getHeldItemMainhand().getItemUseAction() == EnumAction.EAT || mc.player.getHeldItemMainhand().getItemUseAction() == EnumAction.DRINK);

        float rotateLeftYaw = this.isUsingAnItem && mc.player.getActiveHand() == EnumHand.OFF_HAND ? 0f : settingLeftYaw.getValue().floatValue();
        float rotateLeftPitch = this.isUsingAnItem && mc.player.getActiveHand() == EnumHand.OFF_HAND ? 0f : settingLeftPitch.getValue().floatValue();
        float rotateLeftRoll = this.isUsingAnItem && mc.player.getActiveHand() == EnumHand.OFF_HAND ? 0f : settingLeftRoll.getValue().floatValue();

        float translateLeftX = this.isUsingAnItem && mc.player.getActiveHand() == EnumHand.OFF_HAND ? 0f : settingLeftX.getValue().floatValue() / 100;
        float translateLeftY = this.isUsingAnItem && mc.player.getActiveHand() == EnumHand.OFF_HAND ? 0f : settingLeftY.getValue().floatValue() / 100;
        float translateLeftZ = this.isUsingAnItem && mc.player.getActiveHand() == EnumHand.OFF_HAND ? 0f : settingLeftZ.getValue().floatValue() / 100;

        float rotateRightYaw = this.isUsingAnItem && mc.player.getActiveHand() == EnumHand.MAIN_HAND ? 0f : settingRightYaw.getValue().floatValue();
        float rotateRightPitch = this.isUsingAnItem && mc.player.getActiveHand() == EnumHand.MAIN_HAND ? 0f : settingRightPitch.getValue().floatValue();
        float rotateRightRoll = this.isUsingAnItem && mc.player.getActiveHand() == EnumHand.MAIN_HAND ? 0f : settingRightRoll.getValue().floatValue();

        float translateRightX = this.isUsingAnItem && mc.player.getActiveHand() == EnumHand.MAIN_HAND ? 0f : settingRightX.getValue().floatValue() / 100;
        float translateRightY = this.isUsingAnItem && mc.player.getActiveHand() == EnumHand.MAIN_HAND ? 0f : settingRightY.getValue().floatValue() / 100;
        float translateRightZ = this.isUsingAnItem && mc.player.getActiveHand() == EnumHand.MAIN_HAND ? 0f : settingRightZ.getValue().floatValue() / 100;

        switch (event.getHandSide()) {
            case LEFT: {
                if (settingLeft.getValue()) {
                    TurokGL.translate(translateLeftX, translateLeftY, translateLeftZ);
                    TurokGL.scale(settingScaleLeft.getValue().floatValue() / 10, settingScaleLeft.getValue().floatValue() / 10, settingScaleLeft.getValue().floatValue() / 10);

                    GlStateManager.rotate(rotateLeftYaw,0,1,0);
                    GlStateManager.rotate(rotateLeftPitch,1,0,0);
                    GlStateManager.rotate(rotateLeftRoll,0,0,1);
                }

                break;
            }

            case RIGHT: {
                if (settingRight.getValue()) {
                    TurokGL.translate(translateRightX, translateRightY, translateRightZ);
                    TurokGL.scale(settingScaleRight.getValue().floatValue() / 10, settingScaleRight.getValue().floatValue() / 10, settingScaleRight.getValue().floatValue() / 10);

                    GlStateManager.rotate(rotateRightYaw,0,1,0);
                    GlStateManager.rotate(rotateRightPitch,1,0,0);
                    GlStateManager.rotate(rotateRightRoll,0,0,1);
                }

                break;
            }
        }
    }
}
