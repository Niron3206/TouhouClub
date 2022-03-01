package ru.niron3206.cmds;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CommandContext {
    private final MessageReceivedEvent event;
    private final String[] args;

    public CommandContext(MessageReceivedEvent event, String[] args) {
        this.event = event;
        this.args = args;
    }

    public MessageReceivedEvent getEvent() {
        return this.event;
    }

    public String[] getArgs() {
        return this.args;
    }
}