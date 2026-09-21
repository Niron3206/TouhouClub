package ru.niron3206.util;

import java.util.concurrent.TimeUnit;

public final class TimeFormat {

    private TimeFormat() {}

    // мм:сс
    // для треков длиннее часа ч:мм:сс
    // для стримов --:--
    public static String format(long millis) {
        if (millis <= 0 || millis == Long.MAX_VALUE) {
            return "--:--";
        }

        long totalSeconds = TimeUnit.MILLISECONDS.toSeconds(millis);
        long hours = totalSeconds / 3600;
        long minutes = totalSeconds % 3600 / 60;
        long seconds = totalSeconds % 60;

        return hours > 0
                ? String.format("%d:%02d:%02d", hours, minutes, seconds)
                : String.format("%02d:%02d", minutes, seconds);
    }
}
