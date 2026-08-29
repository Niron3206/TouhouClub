package ru.niron3206.cmds.interactions;

public class Slap extends InteractionCommand {

    public Slap() {
        super("slap",
                "Ты должен упомянуть того человека, которого хочешь ударить по щеке!",
                "Время ударить кого-то по щеке!",
                "даёт пощёчину",
                "https://cdn.nekos.life/slap/slap_0%02d.gif", 15);
    }

    @Override
    public String getHelp() {
        return "Если вы хотите выразить свои эмоции без мата, то используйте эту команду.";
    }
}
