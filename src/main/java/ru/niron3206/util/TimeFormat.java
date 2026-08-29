package ru.niron3206.util;

import com.sedmelluq.discord.lavaplayer.tools.Units;

import java.util.concurrent.TimeUnit;

public final class TimeFormat {

    private TimeFormat() {}

    public static String format(long millis) {
        if (millis < 0 || millis == Units.DURATION_MS_UNKNOWN) {
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
