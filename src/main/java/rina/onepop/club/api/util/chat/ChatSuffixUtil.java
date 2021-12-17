package rina.onepop.club.api.util.chat;

/**
 * @author SrRina
 * @since 04/02/2021 at 00:26
 **/
public class ChatSuffixUtil {
    public static String hephaestus(String string) {
        String str = string;

        str = str.replace("a", "\u1d00");
        str = str.replace("b", "\u0299");
        str = str.replace("c", "\u1d04");
        str = str.replace("d", "\u1d05");
        str = str.replace("e", "\u1d07");
        str = str.replace("f", "\u0493");
        str = str.replace("g", "\u0262");
        str = str.replace("h", "\u029c");
        str = str.replace("i", "\u026a");
        str = str.replace("j", "\u1d0a");
        str = str.replace("k", "\u1d0b");
        str = str.replace("l", "\u029f");
        str = str.replace("m", "\u1d0d");
        str = str.replace("n", "\u0274");
        str = str.replace("o", "\u1d0f");
        str = str.replace("p", "\u1d18");
        str = str.replace("q", "\u01eb");
        str = str.replace("r", "\u0280");
        str = str.replace("s", "\u0455");
        str = str.replace("t", "\u1d1b");
        str = str.replace("u", "\u1d1c");
        str = str.replace("v", "\u1d20");
        str = str.replace("w", "\u1d21");
        str = str.replace("x", "\u0445");
        str = str.replace("y", "\u028f");
        str = str.replace("z", "\u1d22");

        // The |.
        str = str.replace("|", "\u23D0");

        return str;
    }
}
