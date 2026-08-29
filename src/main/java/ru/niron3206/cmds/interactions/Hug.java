package ru.niron3206.cmds.interactions;

public class Hug extends InteractionCommand {

    public Hug() {
        super("hug",
                "Ты должен упомянуть того человека, которого хочешь обнять!",
                "Неожиданные обнимашки!",
                "обнимает",
                "https://cdn.nekos.life/hug/hug_0%02d.gif", 88);
    }

    @Override
    public String getHelp() {
        return "Обнимашки всегда выглядят мило! Если вы хотите кого-то обнять, то используйте эту команду.";
    }
}
