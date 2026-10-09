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

    // сс, мм:сс или ч:мм:сс в миллисекунды
    // -1, если не разобрать
    public static long parse(String text) {
        String[] parts = text.trim().split(":", -1);

        if (parts.length > 3) {
            return -1;
        }

        long seconds = 0;

        for (int i = 0; i < parts.length; i++) {
            if (!parts[i].matches("\\d{1,6}")) {
                return -1;
            }

            long value = Long.parseLong(parts[i]);

            // ограничены только минуты и секунды после двоеточия
            if (i > 0 && value >= 60) {
                return -1;
            }

            seconds = seconds * 60 + value;
        }

        return TimeUnit.SECONDS.toMillis(seconds);
    }
}
