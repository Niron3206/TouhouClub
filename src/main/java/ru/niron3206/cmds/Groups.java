package ru.niron3206.cmds;

public enum Groups {
    // порядок задаёт порядок разделов в help
    MUSIC("МУЗЫКА"),
    INTERACTION("ВЗАИМОДЕЙСТВИЯ"),
    // CONVERSION("КОНВЕРТАЦИЯ"),
    HELP("ПОМОЩЬ");

    private final String title;

    Groups(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
