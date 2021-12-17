package rina.onepop.club.client.gui.imperador.util;

import java.awt.*;
import java.awt.datatransfer.*;
import java.io.IOException;

/**
 * @author SrRina
 * @since 28/08/2021 at 19:05
 **/
public class ClipboardUtil {
    public static String get() {
        final java.awt.datatransfer.Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        final Transferable content = clipboard.getContents(null);

        if (content != null && content.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            try {
                return content.getTransferData(DataFlavor.stringFlavor).toString();
            } catch (UnsupportedFlavorException | IOException exc) {
                return null;
            }
        }

        return null;
    }

    public static void set(final String string) {
        final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

        clipboard.setContents(new StringSelection(string), null);
    }
}
