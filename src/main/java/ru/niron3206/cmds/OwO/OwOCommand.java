package ru.niron3206.cmds.OwO;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import ru.niron3206.cmds.CommandContext;
import ru.niron3206.cmds.ICommand;

import static ru.niron3206.cmds.OwO.alphabets.en_ENG.en_ENG;
import static ru.niron3206.cmds.OwO.alphabets.ru_RU.ru_RU;

public class OwOCommand implements ICommand {

    @Override
    public void handle(CommandContext ctx) {

        String[] args = ctx.getArgs();
        MessageReceivedEvent event = ctx.getEvent();
        OwOAlgorithm msg = new OwOAlgorithm(args);
        EmbedBuilder hug = new EmbedBuilder();

        if (args.length < 2) {
            hug.setTitle(":red_circle: Мне нужно сообщения для перевода....бака~");
            hug.setColor(0xd60012);
            event.getChannel().sendMessageEmbeds(hug.build()).queue();
            hug.clear();
        } else {
            for (int i = 1; i < args.length; i++) {
                for (char en : en_ENG) {
                    if (args[i].charAt(0) == en) {
                        msg.engOwOAlgorithm();
                    }
                }
                for (char ru : ru_RU) {
                    if (args[i].charAt(0) == ru) {
                        msg.rusOwOAlgorithm();
                    }
                }
            }

            event.getChannel().sendMessage(msg.finalMessage).queue();
        }
    }

    @Override
    public String getName() {
        return "owo";
    }
}
