package ru.niron3206.util;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class YoutubeLinks {

    // те же домены, что понимает плагин youtube
    private static final Set<String> HOSTS = Set.of(
            "youtube.com", "www.youtube.com", "m.youtube.com", "music.youtube.com",
            "youtu.be", "www.youtu.be");

    // 90, 90s, 1m30s, 1h2m3s
    private static final Pattern TIME = Pattern.compile("(?:(\\d{1,4})h)?(?:(\\d{1,5})m)?(?:(\\d{1,7})s?)?");

    private YoutubeLinks() {}

    // отметка ?t= или ?start= в миллисекундах, 0 если её нет
    public static long startTime(String url) {
        URI uri;

        try {
            uri = new URI(url);
        } catch (URISyntaxException e) {
            return 0;
        }

        String host = uri.getHost();
        String query = uri.getRawQuery();

        if (host == null || query == null || !HOSTS.contains(host.toLowerCase(Locale.ROOT))) {
            return 0;
        }

        for (String param : query.split("&")) {
            int eq = param.indexOf('=');

            if (eq < 0) {
                continue;
            }

            String name = param.substring(0, eq);

            if (name.equals("t") || name.equals("start")) {
                return parse(param.substring(eq + 1));
            }
        }

        return 0;
    }

    private static long parse(String value) {
        Matcher matcher = TIME.matcher(value);

        if (value.isEmpty() || !matcher.matches()) {
            return 0;
        }

        long seconds = number(matcher, 1) * 3600 + number(matcher, 2) * 60 + number(matcher, 3);

        return TimeUnit.SECONDS.toMillis(seconds);
    }

    private static long number(Matcher matcher, int group) {
        String value = matcher.group(group);

        return value == null ? 0 : Long.parseLong(value);
    }
}
