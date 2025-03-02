package rina.onepop.club.client.module.player.reversestep;

import rina.onepop.club.api.module.Module;
import rina.onepop.club.api.module.impl.ModuleCategory;
import rina.onepop.club.api.module.registry.Registry;
import rina.onepop.club.api.setting.value.ValueBoolean;
import rina.onepop.club.api.setting.value.ValueEnum;
import rina.onepop.club.api.setting.value.ValueNumber;
import rina.onepop.club.api.util.client.NullUtil;
import rina.onepop.club.api.util.entity.PlayerUtil;
import rina.onepop.club.api.util.world.BlockUtil;
import rina.onepop.club.api.util.world.BlocksUtil;
import rina.onepop.club.client.event.client.ClientTickEvent;
import net.minecraft.util.math.BlockPos;
import rina.onepop.club.api.ISLClass;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

/**
 * @author SrRina
 * @since 14/04/2021 at 20:24
 **/
@Registry(name = "Reverse Step", tag = "ReverseStep", description = "Step down blocks.", category = ModuleCategory.PLAYER)
public class ModuleReverseStep extends Module {
    /* Misc. */
    public static ValueEnum settingMode = new ValueEnum("Mode", "Mode", "The modes for you fall!", Mode.SMOOTH);
    public static ValueNumber settingHeight = new ValueNumber("Height", "Height", "The height of falls to get down.", 0, 0, 10);

    /* Hole. */
    public static ValueBoolean settingOnlyHole = new ValueBoolean("Only Hole", "OnlyHole", "Step down only if you falling at one hole!", true);
    public static ValueNumber settingHoleHeight = new ValueNumber("Hole Height", "HoleHeight", "The height of falls to get down.", 1, 1, 4);

    @Override
    public void onSetting() {
        settingHeight.setEnabled(!settingOnlyHole.getValue());
        settingHoleHeight.setEnabled(settingOnlyHole.getValue());
    }

    @Listener
    public void onListen(ClientTickEvent event) {
        if (NullUtil.isPlayerWorld()) {
            return;
        }

        if (!ISLClass.mc.player.onGround || ISLClass.mc.player.isOnLadder() || ISLClass.mc.player.isInWater() || ISLClass.mc.player.isInLava() || ISLClass.mc.player.movementInput.jump || ISLClass.mc.player.noClip) {
            return;
        }

        if (ISLClass.mc.player.moveForward == 0 && ISLClass.mc.player.moveStrafing == 0 || mc.player.posY <= 1f) {
            return;
        }

        if (settingOnlyHole.getValue()) {
            BlockPos player = PlayerUtil.getBlockPos();

            boolean fall = settingHoleHeight.getValue().intValue() == 1 && isHole(player.add(0, -1, 0));

            if (settingHoleHeight.getValue().intValue() != 1) {
                for (int y = 0; y < settingHoleHeight.getValue().intValue() + 1; y++) {
                    if (isHole(player.add(0, -y, 0)) || isHole(player)) {
                        fall = true;
                    }
                }
            }

            if (fall || isHole(player)) {
                this.doFall();
            }
        } else {
            if (settingHeight.getValue().intValue() > 0) {
                BlockPos player = PlayerUtil.getBlockPos();
                if (!BlockUtil.isAir(player.add(0, -(settingHeight.getValue().intValue() + 1), 0))) {
                    return;
                }
            }

            this.doFall();
        }
    }

    public boolean isHole(BlockPos position) {
        if (!BlockUtil.isAir(position)) {
            return false;
        }

        int count = 0;

        for (BlockPos add : BlocksUtil.SURROUND) {
            final BlockPos added = position.add(add);

            if (BlockUtil.isAir(added)) {
                count++;
            }
        }

        return count == 0;
    }

    public void doFall() {
        switch ((Mode) settingMode.getValue()) {
            case SMOOTH: {
                mc.player.motionY--;

                break;
            }

            case NORMAL: {
                mc.player.motionY -= 1;

                break;
            }

            case BYPASS: {
                //mc.player.motionY = -(Integer.MAX_VALUE - Integer.MAX_VALUE - (Integer.MAX_VALUE / 1.2f));

                break;
            }
        }
    }
}
