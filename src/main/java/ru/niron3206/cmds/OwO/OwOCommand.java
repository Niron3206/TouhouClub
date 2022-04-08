package ru.niron3206.cmds.OwO;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import ru.niron3206.cmds.CommandContext;
import ru.niron3206.cmds.ICommand;

import java.util.List;

import static ru.niron3206.cmds.OwO.alphabets.en_ENG.en_ENG;
import static ru.niron3206.cmds.OwO.alphabets.ru_RU.ru_RU;

public class OwOCommand implements ICommand {

    @Override
    public void handle(CommandContext ctx) {
        MessageReceivedEvent event = ctx.getEvent();

        if(event.getMessage().getReferencedMessage() != null)
            new OwOReplyCommand().OwOReplyCommand(ctx.getEvent());
        else {
            String[] args = ctx.getArgs();
            OwOAlgorithm msg = new OwOAlgorithm(args);
            EmbedBuilder error = new EmbedBuilder();

            if (args.length < 2) {
                error.setTitle(":red_circle: Мне нужно сообщения для перевода....бака~");
                error.setColor(0xd60012);
                event.getChannel().sendMessageEmbeds(error.build()).queue();
                error.clear();
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
                event.getMessage().reply(msg.getFinalMessage()).queue();
            }
        }
    }

    @Override
    public String getHelp() {
        return "Делает вашу речь как у неко! Вы также можете ответить этой командой на чьё-либо сообщение.";
    }

    @Override
    public String getName() {
        return "owo";
    }

    @Override
    public List<String> getAliases(){
        return List.of("w", "OwO");
    }
}
