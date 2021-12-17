package rina.onepop.club.api.util.client;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author SrRina
 * @since 13/08/2021 at 00:01
 **/
public class DateTimerUtil {
    public static final String TIME_AND_DATE = "HH:mm:ss dd/MM/uuuu";

    public static String time(String pattern) {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        final LocalDateTime time = LocalDateTime.now();

        return formatter.format(time);
    }
}
