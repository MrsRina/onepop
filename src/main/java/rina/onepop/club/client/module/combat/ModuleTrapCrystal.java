package rina.onepop.club.client.module.combat;


import rina.onepop.club.api.module.Module;
import rina.onepop.club.api.module.impl.ModuleCategory;
import rina.onepop.club.api.module.registry.Registry;
import rina.onepop.club.api.setting.value.ValueBoolean;
import rina.onepop.club.api.setting.value.ValueEnum;
import rina.onepop.club.api.setting.value.ValueNumber;
import rina.onepop.club.api.util.entity.EntityUtil;
import rina.onepop.club.client.event.client.RunTickEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

@Registry(name = "Trap-Crystal", tag = "TrapCrystal", description = "Does damage in holes", category = ModuleCategory.COMBAT)
public class ModuleTrapCrystal extends Module {

    ValueEnum calcMode = new ValueEnum("Calc Mode", "calcMode", "How it calculates where to place", CalcMode.Calc);
    ValueBoolean ghostSwitch = new ValueBoolean("Ghost Switch", "ghostSwitch", "whether or not it packet switches", false);
    ValueNumber delay = new ValueNumber("Delay", "delay", "place and break delay", 10, 0, 1000);
    ValueNumber range = new ValueNumber("Range", "range", "Range for actions", 4, 1, 8);
    ValueBoolean holeCheck = new ValueBoolean("Hole Check", "holeCheck", "Checks if the target is in a hole", false);
    ValueBoolean antiNaked = new ValueBoolean("Anti Naked", "antiNaked", "Doesnt attack nakeds", true);
    ValueNumber renderRed = new ValueNumber("Render Red", "renderRed", "Red render value", 150, 0, 255);
    ValueNumber renderGreen = new ValueNumber("Render Green", "renderGreen", "Green render value", 150, 0, 255);
    ValueNumber renderBlue = new ValueNumber("Render Blue", "renderBlue", "Blue render value", 150, 0, 255);
    ValueNumber renderAlpha = new ValueNumber("Render Red", "renderAlpha", "Alpha render value", 150, 0, 255);
    ValueNumber lineAlpha = new ValueNumber("Line Alpha", "lineAlpha", "Render outline alpha", 150, 0, 255);
    ValueNumber lineWidth = new ValueNumber("Line Width", "lineWidth", "Render outline width", 1, 0, 3);

    enum CalcMode {
        Head,
        Calc
    }

    EntityPlayer target;
    BlockPos actionPos;
    Action currentAction;

    @Override
    public void onEnable() {
        super.onEnable();
        currentAction = Action.Trap;
    }

    @Override
    public void onDisable() {
        super.onDisable();

    }

    @Override
    public void onSync() {
        super.onSync();
    }

    @Listener
    public void onTick(RunTickEvent event){
        target = EntityUtil.getTarget(range.getValue().floatValue(), false, antiNaked.getValue());
        if (target == null) return;
    }

    @Override
    public void onRender3D() {
        super.onRender3D();
        if (target == null) return;
    }

    BlockPos[] getTrapBlocks(CalcMode calcMode){
        return null;
    }

    enum Action {
        Trap,
        Crystal,
        Mine
    }


}
