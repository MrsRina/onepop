package me.rina.turok.hardware.keyboard;

import org.lwjgl.input.Keyboard;

/**
 * @author SrRina
 * @since 07/01/2021 at 12:33
 **/
public class TurokKeyboard {
    public static String toString(int key) {
        return (key != -1 ? Keyboard.getKeyName(key) : "NONE");
    }
}
