package rina.onepop.club.client.command;

import rina.onepop.club.api.command.Command;
import rina.onepop.club.api.command.management.CommandManager;

/**
 * @author SrRina
 * @since 16/11/20 at 02:03pm
 */
public class CommandPrefix extends Command {
    public CommandPrefix() {
        super(new String[] {"prefix", "p"}, "Sets a new prefix.");
    }

    @Override
    public String setSyntax() {
        return "prefix/p <char>";
    }

    @Override
    public void onCommand(String[] args) {
        String _char = null;

        if (args.length > 1) {
            _char = args[1];
        }

        if (args.length > 2 || _char == null) {
            splash();

            return;
        }

        CommandManager.getCommandPrefix().setPrefix(_char);

        this.print("Chat prefix has been update to " + "'" + CommandManager.getCommandPrefix().getPrefix() + "'");
    }
}
