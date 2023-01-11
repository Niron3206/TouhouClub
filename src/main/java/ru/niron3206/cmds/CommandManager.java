package ru.niron3206.cmds;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import ru.niron3206.Config;
import ru.niron3206.cmds.Interactions.*;
import ru.niron3206.cmds.music.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class CommandManager {
    private final List<ICommand> commands = new ArrayList<>();

    public CommandManager() {
        addCommand(new HelpCommand(this));

        addCommand(new Hug());
        addCommand(new Pat());
        addCommand(new Slap());

        //addCommand(new OwOCommand());

        //music
        addCommand(new PlayCommand());
        addCommand(new StopCommand());
        addCommand(new SkipCommand());
        addCommand(new NowPlayingCommand());
        addCommand(new QueueCommand());
        addCommand(new LoopCommand());
        addCommand(new LeaveCommand());
        addCommand(new RemoveCommand());
    }

    private void addCommand(ICommand cmd) {
        boolean nameFound = commands.stream().anyMatch((it) -> it.getName().equalsIgnoreCase(cmd.getName()));

        if(nameFound) {
            try {
                throw new IllegalAccessException("A command with this name is already presents");
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        commands.add(cmd);
    }

    public List<ICommand> getCommands() {
        return commands;
    }

    public ICommand getCommand(String search) {
        String searchLower = search.toLowerCase();

        for(ICommand cmd : commands) {
            if(cmd.getName().equals(searchLower) || cmd.getAliases().contains(searchLower)) {
                return cmd;
            }
        }
        return null;
    }

    public void handle(MessageReceivedEvent event) {
        String[] split = event.getMessage().getContentRaw()
                .replaceFirst("(?i)" + Pattern.quote(Config.get("PREFIX")), "")
                .split("\\s+");

        String invoke = split[0].toLowerCase();
        ICommand cmd = getCommand(invoke);

        if(cmd != null) {
            List<String> args = Arrays.asList(split).subList(1, split.length);
            CommandContext ctx = new CommandContext(event, args);
            cmd.handle(ctx);
        }
    }
}
