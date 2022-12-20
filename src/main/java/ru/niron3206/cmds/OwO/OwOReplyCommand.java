package ru.niron3206.cmds.OwO;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.List;

import static ru.niron3206.cmds.OwO.alphabets.en_ENG.en_ENG;
import static ru.niron3206.cmds.OwO.alphabets.ru_RU.ru_RU;

public class OwOReplyCommand  {

    public static void setReplyEvent(MessageReceivedEvent event) {

        List<String> args = new ArrayList<>(List.of(event.getMessage().getReferencedMessage().getContentRaw().split("\\s+")));
        args.add(0, "owo");

        OwOAlgorithm msg = new OwOAlgorithm(args);

        for (int i = 1; i < args.size(); i++) {
            for (char en : en_ENG) {
                if (args.get(i).charAt(0) == en) {
                    msg.engOwOAlgorithm();
                }
            }
            for (char ru : ru_RU) {
                if (args.get(i).charAt(0) == ru) {
                    msg.rusOwOAlgorithm();
                }
            }
        }
        event.getMessage().reply(msg.getFinalMessage()).queue();
    }
}
