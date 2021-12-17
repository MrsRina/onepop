package rina.onepop.club.client.command;

import com.mojang.realmsclient.gui.ChatFormatting;
import rina.onepop.club.api.command.Command;
import rina.onepop.club.api.preset.Preset;
import rina.onepop.club.api.preset.management.PresetManager;
import rina.onepop.club.api.util.chat.ChatUtil;
import rina.onepop.club.api.util.client.DateTimerUtil;
import rina.onepop.club.api.util.client.StringUtil;

/**
 * @author SrRina
 * @since 30/07/2021 at 18:32
 **/
public class CommandPreset extends Command {
    public CommandPreset() {
        super(new String[] {"preset", "config"}, "Sets preset and manage.");
    }

    @Override
    public String setSyntax() {
        return "preset <save/load> <name> | <add/new/create/remove/rem/del/delete> <name> | <refresh/reload/list>";
    }

    @Override
    public void onCommand(String[] args) {
        String task = null;
        String name = null;

        if (args.length > 1) {
            task = args[1];
        }

        if (args.length > 2) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(args[2]);

            for (int i = 3; i < args.length; ++i) {
                stringBuilder.append(" ");
                stringBuilder.append(args[i]);
            }

            name = stringBuilder.toString();
        }

        if (task == null) {
            this.splash();

            return;
        }

        if (StringUtil.contains(task, "list")) {
            PresetManager.info();

            return;
        }

        if (StringUtil.contains(task, "reload", "refresh")) {
            if (name != null) {
                this.splash();

                return;
            }

            PresetManager.refresh();

            ChatUtil.print("Reloaded/refreshed all presets folder.");

            return;
        }

        if (task.equalsIgnoreCase("save")) {
            if (name == null) {
                PresetManager.reload();

                PresetManager.process(PresetManager.DATA);
                PresetManager.process(PresetManager.SAVE);

                ChatUtil.print("Saved current preset: " + PresetManager.current().getTag() + "; Successfully!");

                return;
            }

            final Preset current = PresetManager.current();
            final Preset preset = PresetManager.get(name);

            if (preset != null) {
                PresetManager.set(preset);
                PresetManager.reload();

                PresetManager.process(PresetManager.DATA);
                PresetManager.process(PresetManager.SAVE);

                if (current != null) {
                    PresetManager.set(current);
                    PresetManager.reload();
                }

                ChatUtil.print("Saved " + preset.getTag() + " successfully!");

                return;
            }

            ChatUtil.print(ChatFormatting.RED + name + " preset doesn't exist!");

            return;
        }

        if (task.equalsIgnoreCase("load")) {
            if (name == null) {
                this.splash();

                return;
            }

            final Preset preset = PresetManager.get(name);

            if (preset != null) {
                PresetManager.set(preset);
                PresetManager.reload();

                PresetManager.process(PresetManager.DATA);
                PresetManager.process(PresetManager.LOAD);

                ChatUtil.print(preset.getTag() + " was loaded successfully!");

                return;
            }

            ChatUtil.print(ChatFormatting.RED + name + " preset doesn't exist!");

            return;
        }

        if (StringUtil.contains(task, "add", "new", "create")) {
            if (name == null) {
                this.splash();

                return;
            }

            boolean contains = PresetManager.contains(name);

            if (!contains) {
                final Preset preset = new Preset(name, DateTimerUtil.time(DateTimerUtil.TIME_AND_DATE));

                PresetManager.implement(preset);
                PresetManager.sync(preset);

                PresetManager.process(PresetManager.DATA);

                ChatUtil.print("Successfully created preset: " + preset.getTag() + " in: " + preset.getData());

                return;
            }

            ChatUtil.print("Duplicate preset!");

            return;
        }

        if (StringUtil.contains(task, "remove", "delete", "del", "rem")) {
            if (name == null) {
                this.splash();

                return;
            }

            final Preset preset = PresetManager.get(name);

            if (preset != null) {
                PresetManager.exclude(preset);
                PresetManager.reload();

                PresetManager.process(PresetManager.DATA);

                ChatUtil.print("Removed " + preset.getTag() + " from preset list.");

                return;
            }

            ChatUtil.print(ChatFormatting.RED + name + " preset doesn't exist!");

            return;
        }

        this.splash();
    }
}
