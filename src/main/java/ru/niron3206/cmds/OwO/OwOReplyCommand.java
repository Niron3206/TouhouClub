package ru.niron3206.cmds.OwO;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import static ru.niron3206.cmds.OwO.alphabets.en_ENG.en_ENG;
import static ru.niron3206.cmds.OwO.alphabets.ru_RU.ru_RU;

public class OwOReplyCommand  {

    public void OwOReplyCommand(MessageReceivedEvent event) {

        String[] args = event.getMessage().getReferencedMessage().getContentRaw().split("\\s+");

        String[] newArgs = new String[args.length + 1];
        for (int i = 0; i < args.length + 1; i++) {
            if (i == 0)
                newArgs[i] = "owo";
            else
                newArgs[i] = args[i-1];
        }

        OwOAlgorithm msg = new OwOAlgorithm(newArgs);

        for (int i = 1; i < newArgs.length; i++) {
            for (char en : en_ENG) {
                if (newArgs[i].charAt(0) == en) {
                    msg.engOwOAlgorithm();
                }
            }
            for (char ru : ru_RU) {
                if (newArgs[i].charAt(0) == ru) {
                    msg.rusOwOAlgorithm();
                }
            }
        }
        event.getMessage().reply(msg.getFinalMessage()).queue();
    }
    
}
