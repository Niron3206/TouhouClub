package ru.niron3206.cmds.interactions;

public class Pat extends InteractionCommand {

    public Pat() {
        super("pat",
                "Ты должен упомянуть того человека, которого хочешь погладить по голове!",
                "Время пэтта!",
                "гладит по голове",
                "https://cdn.nekos.life/pat/pat_0%02d.gif", 73);
    }

    @Override
    public String getHelp() {
        return "Хотите кого-то погладить по голове? То это команда для вас!";
    }
}
