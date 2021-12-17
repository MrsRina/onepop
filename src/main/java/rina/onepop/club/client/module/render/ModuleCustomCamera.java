package rina.onepop.club.client.module.render;

import rina.onepop.club.api.module.Module;
import rina.onepop.club.api.module.impl.ModuleCategory;
import rina.onepop.club.api.module.registry.Registry;
import rina.onepop.club.api.setting.value.ValueBoolean;
import rina.onepop.club.api.setting.value.ValueNumber;
import rina.onepop.club.api.util.client.NullUtil;
import rina.onepop.club.client.event.client.ClientTickEvent;
import rina.onepop.club.client.event.render.PerspectiveEvent;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

/**
 * @author Jake
 * @author SrRina
 *
 * Sorry jake, I have update the module.
 **/
@Registry(name = "Custom Camera", tag = "CustomCamera", description = "Manage camera stuff and player hands.", category = ModuleCategory.RENDER)
public class ModuleCustomCamera extends Module {
    public static ModuleCustomCamera INSTANCE;

    /* Field of View. */
    public static ValueNumber settingFieldOfView = new ValueNumber("Field of View", "FieldOfView", "Field of view camera.", 130, 0, 180);

    /* Aspect ratio. */
    public static ValueBoolean settingRatioChange = new ValueBoolean("Ratio Changer", "RatioChanger", "Allows you to change the ratio.", false);
    public static ValueNumber settingRatio = new ValueNumber("Ratio", "Ratio", "The ratio for ratio changer.", mc.displayWidth / mc.displayHeight, 0.0,3.0);

    public static ValueBoolean settingNoCameraClip = new ValueBoolean("No Camera Clip", "NoCameraClip", "No camera clip!", false);

    /* Misc right hand. */
    public ModuleCustomCamera() {
        INSTANCE = this;
    }

    @Override
    public void onSetting() {
        settingRatio.setEnabled(settingRatioChange.getValue());
    }

    @Listener
    public void onListenTickEvent(ClientTickEvent event) {
        if (NullUtil.isPlayerWorld()) {
            return;
        }

        mc.gameSettings.fovSetting = settingFieldOfView.getValue().intValue();
    }

    @Listener
    public void onPerspectiveEvent(PerspectiveEvent event) {
        if (settingRatioChange.getValue()) {
            event.setAspect(settingRatio.getValue().floatValue());
        } else {
            event.setAspect((float) ((float) mc.displayWidth / (float) mc.displayHeight));
        }
    }
}
