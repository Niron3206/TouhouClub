package ru.niron3206.cmds;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import ru.niron3206.Config;
import ru.niron3206.cmds.Interactions.Hug;
import ru.niron3206.cmds.OwO.OwOCommand;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class CommandManager {
    private final List<ICommand> commands = new ArrayList<>();

    public CommandManager() {
        addCommand(new OwOCommand());
        addCommand(new Hug());
    }

    private void addCommand(ICommand cmd) {
        boolean nameFound = this.commands.stream().anyMatch((it) ->it.getName().equalsIgnoreCase(cmd.getName()));

        if(nameFound) {
            try {
                throw new IllegalAccessException("A command with this name is already presents");
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        commands.add(cmd);
    }

    private ICommand getCommand(String search) {
        String searchLower = search.toLowerCase();

        for(ICommand cmd : this.commands) {
            if(cmd.getName().equals(searchLower) || cmd.getAliases().contains(searchLower)) {
                return cmd;
            }
        }
        return null;
    }

    public void handle(MessageReceivedEvent event) {
        String[] args = event.getMessage().getContentRaw()
                .replaceFirst("(?i)" + Pattern.quote(Config.get("PREFIX")), "")
                .split("\\s+");

        String invoke = args[0].toLowerCase();
        ICommand cmd = this.getCommand(invoke);

        if(cmd != null) {
            event.getChannel().sendTyping().queue();

            CommandContext ctx = new CommandContext(event, args);

            cmd.handle(ctx);
        }
    }
}
