package ru.niron3206;

import io.github.cdimascio.dotenv.Dotenv;

public class Config {

    private static final String DEFAULT_PREFIX = "~";

    // ignoreIfMissing
    private static final Dotenv DOTENV = Dotenv.configure()
            .ignoreIfMissing()
            .ignoreIfMalformed()
            .load();

    private static final String PREFIX = resolvePrefix();

    private Config() {}

    public static String get(String key) {
        return DOTENV.get(key.toUpperCase());
    }

    public static String prefix() {
        return PREFIX;
    }

    private static String resolvePrefix() {
        String value = get("PREFIX");

        return value == null || value.isBlank() ? DEFAULT_PREFIX : value;
    }

    public static String require(String key) {
        String value = get(key);

        if (value == null || value.isBlank()) {
            throw new IllegalStateException(
                    "Не задана настройка " + key.toUpperCase() + ". "
                            + "Укажите её в файле .env (см. .env-example) или в переменных окружения.");
        }
        return value;
    }
}
