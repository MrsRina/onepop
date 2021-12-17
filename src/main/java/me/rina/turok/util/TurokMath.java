package me.rina.turok.util;

import net.minecraft.util.math.Vec3d;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author SrRina
 * @since 21/10/2020
 */
public class TurokMath {
    public static double PI = 3.1416f;

    public static float amount(float value, float maximum) {
        float h = ((maximum - value) / maximum) * 100f;

        return h;
    }

    public static double getDistance(Vec3d one, double theX, double theY, double theZ) {
        final double diffX = one.x - theX;
        final double diffY = one.y - theY;
        final double diffZ = one.z - theZ;

        final double x = diffX * diffX;
        final double y = diffY * diffY;
        final double z = diffZ * diffZ;

        return sqrt(x + y + z);
    }

    public static double getDistance(Vec3d one, Vec3d two) {
        final double diffX = one.x - two.x;
        final double diffY = one.y - two.y;
        final double diffZ = one.z - two.z;

        final double x = diffX * diffX;
        final double y = diffY * diffY;
        final double z = diffZ * diffZ;

        return sqrt(x + y + z);
    }

    public static float distancingValues(float value, float maximum, float distance) {
        float h = (value * 100) / maximum;
        float l = distance / 100f;

        return (h * l);
    }

    public static int clamp(int value, int minimum, int maximum) {
        return value < minimum ? minimum : value > maximum ? maximum : value;
    }

    public static double clamp(double value, double minimum, double maximum) {
        return value < minimum ? minimum : value > maximum ? maximum : value;
    }

    public static float clamp(float value, float minimum, float maximum) {
        return value < minimum ? minimum : value > maximum ? maximum : value;
    }

    public static double round(double vDouble) {
        BigDecimal decimal = new BigDecimal(vDouble);

        decimal = decimal.setScale(2, RoundingMode.HALF_UP);

        return decimal.doubleValue();
    }

    public static Vec3d lerp(Vec3d a, Vec3d b, float ticks) {
        return new Vec3d(
            a.x + (b.x - a.x) * ticks,
            a.y + (b.y - a.y) * ticks,
            a.z + (b.z - a.z) * ticks
        );
    }

    public static float lerp(float a, float b, float ticks) {
        if (ticks >= 1f || ticks < 0f) {
            return b;
        }

        return (a + (b - a) * ticks);
    }

    public static TurokRect lerp(TurokRect a, TurokRect b, float ticks) {
        if (ticks == 1 || ticks == 5) {
            return a.copy(b);
        }

        a.x = TurokMath.serp(a.x, b.x, ticks);
        a.y = TurokMath.serp(a.y, b.y, ticks);

        a.width = TurokMath.serp(a.width, b.width, ticks);
        b.height = TurokMath.serp(a.height, b.height, ticks);

        return a;
    }

    public static double lerp(double a, double b, float ticks) {
        return (a + (b - a) * ticks);
    }

    public static float serp(float a, float b, float ticks) {
        return lerp(a, b, ticks);
    }

    public static double serp(double a, double b, float ticks) {
        return lerp(a, b, ticks);
    }

    public static int normalize(int... value) {
        int normalizedValue = 0;
        int cachedValue = 0;

        for (int values : value) {
            cachedValue = values;

            normalizedValue = values / cachedValue * cachedValue;
        }

        return normalizedValue;
    }

    public static double normalize(double... value) {
        double normalizedValue = 0;
        double cachedValue = 0;

        for (double values : value) {
            cachedValue = values;

            normalizedValue = values / cachedValue * cachedValue;
        }

        return normalizedValue;
    }

    public static float normalize(float... value) {
        float normalizedValue = 0;
        float cachedValue = 0;

        for (float values : value) {
            cachedValue = values;

            normalizedValue = values / cachedValue * cachedValue;
        }

        return normalizedValue;
    }

    public static int ceiling(double value) {
        int valueInt = (int) value;

        return value >= (double) valueInt ? valueInt + 1 : valueInt;
    }

    public static int ceiling(float value) {
        int valueInt = (int) value;

        return value >= (float) valueInt ? valueInt + 1 : valueInt;
    }

    public static double sqrt(double a) {
        return Math.sqrt(a);
    }

    public static float sqrt(float a) {
        return (float) Math.sqrt(a);
    }

    public static int sqrt(int a) {
        return (int) Math.sqrt(a);
    }

    public static int min(int value, int minimum) {
        return Math.max(value, minimum);
    }

    public static float min(float value, float minimum) {
        return value <= minimum ? minimum : value;
    }

    public static double min(double value, double minimum) {
        return value <= minimum ? minimum : value;
    }

    public static int max(int value, int maximum) {
        return Math.min(value, maximum);
    }

    public static double max(double value, double maximum) {
        return value >= maximum ? maximum : value;
    }

    public static float max(float value, float maximum) {
        return value >= maximum ? maximum : value;
    }

    public static int negative(int a) {
        return (a - a) - a;
    }

    public static double negative(double a) {
        return (a - a) - a;
    }

    public static float negative(float a) {
        return (a - a) - a;
    }

    public static int positive(int a) {
        return a > 0 ? (a + a) + a : a;
    }

    public static double positive(double a) {
        return a > 0 ? (a + a) + a : a;
    }

    public static float positive(float a) {
        return a > 0 ? (a + a) + a : a;
    }
}