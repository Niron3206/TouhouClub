package ru.niron3206.cmds.interactions;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import ru.niron3206.cmds.CommandContext;
import ru.niron3206.cmds.Groups;
import ru.niron3206.cmds.ICommand;
import ru.niron3206.util.Colors;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

// абстрактный класс для взаимодействий
// минус прошлый бойлерплейт
public abstract class InteractionCommand implements ICommand {

    private final String name;
    private final String mentionHint;
    private final String title;
    private final String action;
    private final String imageFormat;
    private final int imageCount;

    protected InteractionCommand(String name, String mentionHint, String title,
                                 String action, String imageFormat, int imageCount) {
        this.name = name;
        this.mentionHint = mentionHint;
        this.title = title;
        this.action = action;
        this.imageFormat = imageFormat;
        this.imageCount = imageCount;
    }

    @Override
    public final void handle(CommandContext ctx) {
        List<Member> mentioned = ctx.getEvent().getMessage().getMentions().getMembers();
        EmbedBuilder embed = new EmbedBuilder();

        if (mentioned.isEmpty()) {
            embed.setTitle(":red_circle: " + mentionHint);
            embed.setColor(0xd60012);
            ctx.getChannel().sendMessageEmbeds(embed.build()).queue();
            return;
        }

        int image = ThreadLocalRandom.current().nextInt(imageCount) + 1;

        embed.setTitle(title);
        embed.setDescription(ctx.getEvent().getAuthor().getAsMention() + " *" + action + "* " + mentioned.get(0).getAsMention());
        embed.setImage(String.format(imageFormat, image));
        embed.setColor(Colors.random());

        ctx.getChannel().sendMessageEmbeds(embed.build()).queue();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Groups getGroup() {
        return Groups.INTERACTION;
    }
}
