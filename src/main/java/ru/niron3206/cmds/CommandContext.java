package ru.niron3206.cmds;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;

public class CommandContext {
    private final MessageReceivedEvent event;
    private final List<String> args;

    public CommandContext(MessageReceivedEvent event, List<String> args) {
        this.event = event;
        this.args = args;
    }

    public Guild getGuild() {
        return getEvent().getGuild();
    }

    public MessageReceivedEvent getEvent() {
        return event;
    }

    public List<String> getArgs() {
        return args;
    }

}