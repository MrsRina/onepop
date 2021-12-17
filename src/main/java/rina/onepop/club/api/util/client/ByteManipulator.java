package rina.onepop.club.api.util.client;

/**
 * @author SrRina
 * @since 12/08/2021 at 23:55
 **/
public class ByteManipulator {
    public static byte FALSE = 0;
    public static byte TRUE = 1;

    public static boolean byteToBoolean(byte value) {
        return value == TRUE;
    }

    public static boolean intToBoolean(int i) {
        return i == TRUE;
    }
}
