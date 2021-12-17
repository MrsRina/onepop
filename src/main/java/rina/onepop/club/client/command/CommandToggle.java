package rina.onepop.club.client.command;

import com.mojang.realmsclient.gui.ChatFormatting;
import rina.onepop.club.api.command.Command;
import rina.onepop.club.api.module.Module;
import rina.onepop.club.api.module.management.ModuleManager;
import rina.onepop.club.api.setting.value.ValueBoolean;

/**
 * @author SrRina
 * @since 16/11/20 at 10:43pm
 */
public class CommandToggle extends Command {
    public CommandToggle() {
        super(new String[] {"t", "toggle"}, "Toggle modules.");
    }

    @Override
    public String setSyntax() {
        return "t/toggle <module>";
    }

    @Override
    public void onCommand(String[] args) {
        String tag = null;

        if (args.length > 1) {
            tag = args[1];
        }

        if (args.length > 2 || tag == null) {
            splash();

            return;
        }

        Module module = ModuleManager.get(tag);

        if (module == null) {
            this.print(ChatFormatting.RED + "Unknown module");

            return;
        }

        module.toggle();

        ValueBoolean toggleMessage = (ValueBoolean) module.get("ToggleMessage");

        if (!toggleMessage.getValue()) {
            this.print("Module has been updated to " + module.isEnabled());
        }
    }
}
