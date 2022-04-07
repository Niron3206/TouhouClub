package ru.niron3206.cmds;

import net.dv8tion.jda.api.EmbedBuilder;
import ru.niron3206.Config;

import java.awt.*;
import java.util.Random;

public class HelpCommand implements ICommand{

    private final CommandManager manager;

    public HelpCommand(CommandManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(CommandContext ctx) {
        EmbedBuilder embed = new EmbedBuilder();
        Random random = new Random();
        String[] args = ctx.getArgs();

        if(args.length == 1) {
            StringBuilder builder = new StringBuilder();
            embed.setTitle("Список команд:\n");

            manager.getCommands().stream().map(ICommand::getName).forEach(
                    (it) -> builder.append("`").append(Config.get("PREFIX")).append(it).append("`\n")
            );

            embed.setDescription(builder.toString());
            embed.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)).getRGB());
            ctx.getEvent().getChannel().sendMessageEmbeds(embed.build()).queue();
            embed.clear();
            return;
        }

        String search = args[1];
        ICommand command = manager.getCommand(search);

        if(command == null) {
            embed.setTitle(":red_circle: Команды `" + search + "` не существует!");
            embed.setColor(0xd60012);
            ctx.getEvent().getChannel().sendMessageEmbeds(embed.build()).queue();
            embed.clear();
            return;
        }

        embed.setTitle(command.getHelp());
        embed.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)).getRGB());
        ctx.getEvent().getChannel().sendMessageEmbeds(embed.build()).queue();
        embed.clear();
    }

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getHelp() {
        return "Показывает список доступных команд бота.";
    }
}
