package rina.onepop.club.client.module.render.esp;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import rina.onepop.club.api.module.Module;
import rina.onepop.club.api.module.impl.ModuleCategory;
import rina.onepop.club.api.module.registry.Registry;
import rina.onepop.club.api.setting.value.ValueBoolean;
import rina.onepop.club.api.setting.value.ValueColor;
import rina.onepop.club.api.setting.value.ValueEnum;
import rina.onepop.club.api.setting.value.ValueNumber;
import rina.onepop.club.api.social.Social;
import rina.onepop.club.api.social.management.SocialManager;
import rina.onepop.club.api.social.type.SocialType;
import rina.onepop.club.api.util.client.NullUtil;
import rina.onepop.club.client.event.render.RenderModelEvent;
import rina.onepop.club.client.module.render.esp.impl.Mode;
import rina.onepop.club.client.module.render.esp.impl.Type;
import rina.onepop.club.client.module.render.esp.process.ProcessESP;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

import java.awt.*;

/**
 * @author SrRina
 * @since 19/04/2021 at 20:29
 **/
@Registry(name = "Player ESP", tag = "PlayerESP", description = "Chams!", category = ModuleCategory.RENDER)
public class ModulePlayerESP extends Module {
    /* Misc. */
    public static ValueBoolean settingEnemy = new ValueBoolean("Enemy", "Enemy", "Enable for all your enemies!", false);
    public static ValueBoolean settingFriend = new ValueBoolean("Friend", "Friend", "Enable for all yours friends!", true);
    public static ValueBoolean settingEveryone = new ValueBoolean("Everyone", "Everyone", "Everyone!!!!!", true);
    public static ValueBoolean settingFrustumNoRender = new ValueBoolean("Frustum No Render", "FrustumNoRender", "Disable ESP on frustum area!", true);
    public static ValueEnum settingLineType = new ValueEnum("Line Type", "LineType", "Sets line type.", Type.SOFT);
    public static ValueNumber settingScale = new ValueNumber("Scale","Scale", "Scale of entity.", 1000, 0, 2000);
    public static ValueNumber settingOffsetY = new ValueNumber("Offset Y", "OffsetY", "Offset space for Y", 0, -2000, 2000);
    public static ValueEnum settingRenderMode = new ValueEnum("Render Mode", "RenderMode", "Type of render.", Mode.SMOOTH);

    /* Post. */
    public static ValueNumber settingAlpha = new ValueNumber("Alpha", "Alpha", "Sets alpha value.", 100, 0, 255);

    /* Render color. */
    public static ValueColor settingColor = new ValueColor("Color", "Color", "Sets player color.", new Color(190, 190, 190, 255));
    public static ValueNumber settingLineSize = new ValueNumber("Line Size", "LineSize", "Sets line size.", 1f, 1f, 5f);

    @Override
    public void onSetting() {
        settingAlpha.setEnabled(settingRenderMode.getValue() == Mode.SMOOTH || settingRenderMode.getValue() == Mode.OUTLINE);
        settingLineSize.setEnabled(settingRenderMode.getValue() == Mode.SMOOTH || settingRenderMode.getValue() == Mode.OUTLINE || settingRenderMode.getValue() == Mode.LINE);
        settingLineType.setEnabled(settingRenderMode.getValue() == Mode.SMOOTH || settingRenderMode.getValue() == Mode.OUTLINE || settingRenderMode.getValue() == Mode.LINE);
    }

    @Listener
    public void onRenderModel(RenderModelEvent event) {
        if (NullUtil.isPlayerWorld()) {
            return;
        }

        if (this.isAcceptable(event.getEntity())) {
            event.setCanceled(true);

            ProcessESP.playerESP(event, settingScale.getValue().floatValue(), settingOffsetY.getValue().floatValue(), settingAlpha.getValue().intValue(), settingLineSize.getValue().floatValue(), settingLineType.getValue() != Type.WIRE, !settingFrustumNoRender.getValue(), settingColor, (Mode) settingRenderMode.getValue());
        }
    }

    public boolean isAcceptable(Entity entityLivingBase) {
        boolean isAccepted = false;

        if (!(entityLivingBase instanceof EntityPlayer)) {
            return false;
        }

        final EntityPlayer entityPlayer = (EntityPlayer) entityLivingBase;
        final Social social = SocialManager.get(entityPlayer.getName());

        if (social != null && social.getType() == SocialType.FRIEND && settingFriend.getValue()) {
            isAccepted = true;
        }

        if (social != null && social.getType() == SocialType.ENEMY && settingEnemy.getValue()) {
            isAccepted = true;
        }

        if (social == null && settingEveryone.getValue()) {
            isAccepted = true;
        }

        return isAccepted;
    }
}