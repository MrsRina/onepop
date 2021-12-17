package rina.onepop.club.api.util.client;

import me.rina.turok.render.font.hal.CFontRenderer;
import rina.onepop.club.api.setting.value.ValueString;

/**
 * @author SrRina
 * @since 18/03/2021 at 11:59
 **/
public class StringUtil {
    public static Number entryBoxNumber(ValueString setting, Number pattern) {
        if (pattern instanceof Float) {
            float lastValue = pattern.floatValue();

            if (!isFloat(setting.getValue())) {
                setting.setValue("" + lastValue);

                return pattern;
            } else {
                float value = Float.parseFloat(setting.getValue());

                if (lastValue == value) {
                    return lastValue;
                }

                return Float.parseFloat(setting.getValue());
            }
        } else if (pattern instanceof Double) {
            double lastValue = pattern.doubleValue();

            if (!isDouble(setting.getValue())) {
                setting.setValue("" + lastValue);

                return pattern;
            } else {
                double value = Double.parseDouble(setting.getValue());

                if (lastValue == value) {
                    return lastValue;
                }

                return Double.parseDouble(setting.getValue());
            }
        } else if (pattern instanceof Integer) {
            float lastValue = pattern.intValue();

            if (!isInteger(setting.getValue())) {
                setting.setValue("" + lastValue);

                return pattern;
            } else {
                float value = Integer.parseInt(setting.getValue());

                if (lastValue == value) {
                    return lastValue;
                }

                return Integer.parseInt(setting.getValue());
            }
        }

        return 0;
    }

    public static boolean isDouble(String string) {
        try {
             Double.parseDouble(string);

            return true;
        } catch (NumberFormatException exc) {
            return false;
        }
    }

    public static boolean isInteger(String string) {
        try {
            Integer.parseInt(string);

            return true;
        } catch (NumberFormatException exc) {
            return false;
        }
    }

    public static boolean isFloat(String string) {
        try {
            Float.parseFloat(string);

            return true;
        } catch (NumberFormatException exc) {
            return false;
        }
    }

    public static boolean contains(String name, String... items) {
        boolean flag = false;

        for (String i : items) {
            if (i.equalsIgnoreCase(name)) {
                flag = true;

                break;
            }
        }

        return flag;
    }

    public static String trimStringToWidth(CFontRenderer font, String text, int width, boolean reverse) {
        StringBuilder stringbuilder = new StringBuilder();
        int i = 0;
        int j = reverse ? text.length() - 1 : 0;
        int k = reverse ? -1 : 1;
        boolean flag = false;
        boolean flag1 = false;

        for(int l = j; l >= 0 && l < text.length() && i < width; l += k) {
            char c0 = text.charAt(l);
            int i1 = font.getStringWidth(Character.toString(c0));
            if (flag) {
                flag = false;
                if (c0 != 'l' && c0 != 'L') {
                    if (c0 == 'r' || c0 == 'R') {
                        flag1 = false;
                    }
                } else {
                    flag1 = true;
                }
            } else if (i1 < 0) {
                flag = true;
            } else {
                i += i1;
                if (flag1) {
                    ++i;
                }
            }

            if (i > width) {
                break;
            }

            if (reverse) {
                stringbuilder.insert(0, c0);
            } else {
                stringbuilder.append(c0);
            }
        }

        return stringbuilder.toString();
    }
}
