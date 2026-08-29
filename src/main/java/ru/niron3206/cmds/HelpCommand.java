package ru.niron3206.cmds;

import net.dv8tion.jda.api.EmbedBuilder;
import ru.niron3206.Config;
import ru.niron3206.util.Colors;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HelpCommand implements ICommand{

    private final CommandManager manager;

    public HelpCommand(CommandManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(CommandContext ctx) {
        EmbedBuilder embed = new EmbedBuilder();
        List<String> args = ctx.getArgs();

        if (args.isEmpty()) {
            embed.setTitle("Список команд:");
            embed.setDescription(commandList());
            embed.setFooter("*если вы хотите узнать, что делает та или иная команда, то введите "
                    + Config.prefix() + "help <команда из списка>");
            embed.setColor(Colors.random());
            ctx.getChannel().sendMessageEmbeds(embed.build()).queue();
            return;
        }

        String search = args.get(0);
        ICommand command = manager.getCommand(search);

        if (command == null) {
            embed.setTitle(":red_circle: Команды `" + search + "` не существует!");
            embed.setColor(0xd60012);
        } else {
            embed.setTitle(command.getHelp());
            embed.setColor(Colors.random());
        }

        ctx.getChannel().sendMessageEmbeds(embed.build()).queue();
    }

    private String commandList() {
        // держит разделы в порядке объявления groups
        Map<Groups, List<ICommand>> byGroup = manager.getCommands().stream()
                .collect(Collectors.groupingBy(ICommand::getGroup,
                        () -> new EnumMap<>(Groups.class),
                        Collectors.toList()));

        StringBuilder builder = new StringBuilder();

        byGroup.forEach((group, commands) -> {
            builder.append("**").append(group.getTitle()).append("**\n");
            commands.forEach(cmd -> builder.append("`").append(cmd.getName()).append("`\n"));
        });

        return builder.toString();
    }

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getHelp() {
        return "Показывает список доступных команд бота.";
    }

    @Override
    public Groups getGroup() {
        return Groups.HELP;
    }
}
