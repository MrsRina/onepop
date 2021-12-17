package rina.onepop.club.client.command;

import rina.onepop.club.Onepop;
import rina.onepop.club.api.command.Command;
import rina.onepop.club.api.util.chat.ChatUtil;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

/**
 * @author SrRina
 * i made this...
 * @since 16/11/20 at 02:03pm
 */
public class CommandCoords extends Command {
  public CommandCoords() {
    super(new String[] {"coords", "c"}, "Copies your coordinates to the clipboard");
  }

  @Override
  public String setSyntax() {
    return "coords/c || coords/c name";
  }

  @Override
  public void onCommand(String[] args) {
    if (args.length > 2) {
      splash();

      return;
    }

    String coords = "X: " + Onepop.MC.player.getPosition().getX() + " Y: " + Onepop.MC.player.getPosition().getY() + " Z: " + Onepop.MC.player.getPosition().getZ();

    if (args.length > 1) {
      ChatUtil.message("/w " + args[1] + " " + coords);

      return;
    }

    StringSelection stringSelection = new StringSelection(coords);
    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    clipboard.setContents(stringSelection, null);
    this.print("Coordinates copied to clipboard");
  }
}
