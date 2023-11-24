package ru.niron3206.cmds;

import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;
import java.util.List;
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
        List<String> args = ctx.getArgs();

        if(args.size() == 0) {
            StringBuilder builder = new StringBuilder();
            embed.setTitle("Список команд:\n");

            List<ICommand> commands = manager.getCommands();

            commands.forEach(
                    (cmd) -> {

                        if (cmd.getGroup() == Groups.HELP) {
                            if(builder.indexOf("ПОМОЩЬ") == -1) {builder.append("**ПОМОЩЬ**\n");}
                            builder.append("`").append(cmd.getName()).append("`\n");
                        }

                        if (cmd.getGroup() == Groups.INTERACTION) {
                            if(builder.indexOf("ВЗАИМОДЕЙСТВИЯ") == -1) {builder.append("**ВЗАИМОДЕЙСТВИЯ**\n");}
                            builder.append("`").append(cmd.getName()).append("`\n");
                        }

                        if (cmd.getGroup() == Groups.MUSIC) {
                            if(builder.indexOf("МУЗЫКА") == -1) {builder.append("**МУЗЫКА**\n");}
                            builder.append("`").append(cmd.getName()).append("`\n");
                        }

                        if (cmd.getGroup() == Groups.CONVERSION) {
                            if(builder.indexOf("КОНВЕРТАЦИЯ") == -1) {builder.append("**КОНВЕРТАЦИЯ**\n");}
                            builder.append("`").append(cmd.getName()).append("`\n");
                        }
                    }
            );

            embed.setDescription(builder.toString());
            embed.setFooter("*если вы хотите узнать, что делает та или иная команда, то введите ~help <команда из списка>");
            embed.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)).getRGB());
            ctx.getEvent().getChannel().sendMessageEmbeds(embed.build()).queue();
            embed.clear();
            return;
        }

        String search = args.get(0);
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

    @Override
    public Groups getGroup() {
        return Groups.HELP;
    }
}
