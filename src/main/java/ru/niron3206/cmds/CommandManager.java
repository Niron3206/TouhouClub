package ru.niron3206.cmds;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.niron3206.Config;
import ru.niron3206.cmds.interactions.*;
import ru.niron3206.cmds.music.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CommandManager {

    private static final Logger LOG = LoggerFactory.getLogger(CommandManager.class);

    private final Map<String, ICommand> commands = new LinkedHashMap<>();
    // имена и алиасы
    private final Map<String, ICommand> byInvoke = new HashMap<>();

    public CommandManager() {
        addCommand(new HelpCommand(this));

        //interactions
        addCommand(new Hug());
        addCommand(new Pat());
        addCommand(new Slap());

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
        String name = cmd.getName().toLowerCase(Locale.ROOT);
        ICommand sameName = byInvoke.get(name);

        if (sameName != null) {
            LOG.warn("Команда {} уже занята классом {}, пропускаю", name, sameName.getClass().getSimpleName());
            return;
        }

        commands.put(name, cmd);
        byInvoke.put(name, cmd);

        for (String alias : cmd.getAliases()) {
            ICommand owner = byInvoke.putIfAbsent(alias.toLowerCase(Locale.ROOT), cmd);

            if (owner != null) {
                LOG.warn("Алиас {} команды {} уже занят командой {}", alias, name, owner.getName());
            }
        }
    }

    public Collection<ICommand> getCommands() {
        return Collections.unmodifiableCollection(commands.values());
    }

    public ICommand getCommand(String search) {
        return byInvoke.get(search.toLowerCase(Locale.ROOT));
    }

    public void handle(MessageReceivedEvent event) {
        String content = event.getMessage().getContentRaw().substring(Config.prefix().length()).trim();

        if (content.isEmpty()) {
            return;
        }

        String[] split = content.split("\\s+");
        String invoke = split[0].toLowerCase(Locale.ROOT);
        ICommand cmd = getCommand(invoke);

        if (cmd == null) {
            return;
        }

        List<String> args = Arrays.asList(split).subList(1, split.length);

        LOG.debug("Команда {} от {} на сервере {}", invoke, event.getAuthor().getId(), event.getGuild().getId());

        try {
            cmd.handle(new CommandContext(event, args));
        } catch (Exception e) {
            LOG.error("Команда {} сорвалась", invoke, e);
            event.getGuildChannel().sendMessage("🔴 Что-то пошло не так, попробуй ещё раз!").queue();
        }
    }
}
