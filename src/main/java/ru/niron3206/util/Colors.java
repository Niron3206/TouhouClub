package ru.niron3206.util;

import java.awt.Color;
import java.util.concurrent.ThreadLocalRandom;

public final class Colors {

    private Colors() {}

    // случайный цвет полоски эмбеда
    public static int random() {
        ThreadLocalRandom random = ThreadLocalRandom.current();

        return new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256)).getRGB();
    }
}
