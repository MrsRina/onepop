package me.rina.turok.util;

/**
 * @author SrRina
 * @since 03/12/20 at 10:49pm
 */
public class TurokClass {
    public static Enum getEnumByName(Enum _enum, String name) {
        for (Enum enums : _enum.getClass().getEnumConstants()) {
            if (enums.name().equalsIgnoreCase(name)) {
                return enums;
            }
        }

        return _enum;
    }

    public static boolean isAnnotationPreset(Class clazz, Class clazz1) {
        return clazz.isAnnotationPresent(clazz1);
    }
}
