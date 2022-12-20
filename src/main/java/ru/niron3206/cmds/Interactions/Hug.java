package ru.niron3206.cmds.Interactions;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import ru.niron3206.cmds.CommandContext;
import ru.niron3206.cmds.ICommand;

import java.awt.Color;
import java.util.Random;

public class Hug implements ICommand {

    @Override
    public void handle(CommandContext ctx) {
        MessageReceivedEvent event = ctx.getEvent();
        Random random = new Random();
        EmbedBuilder hug = new EmbedBuilder();

        Member member;
        try {
            member = event.getMessage().getMentionedMembers().get(0);
        } catch (IndexOutOfBoundsException e) {
            hug.setTitle(":red_circle: Ты должен упомянуть того человека, которого хочешь обнять!");
            hug.setColor(0xd60012);
            event.getChannel().sendMessageEmbeds(hug.build()).queue();
            hug.clear();
            return;
        }

        hug.setTitle("Неожиданные обнимашки!");
        hug.setDescription(event.getAuthor().getAsMention() + " *обнимает* " + member.getAsMention());
        hug.setImage(String.format("https://cdn.nekos.life/hug/hug_0%02d.gif", random.nextInt(88) + 1));
        hug.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)).getRGB());

        event.getChannel().sendMessageEmbeds(hug.build()).queue();
        hug.clear();
    }

    @Override
    public String getHelp() {
        return "Обнимашки всегда выглядят мило! Если вы хотите кого-то обнять, то используйте эту команду.";
    }

    @Override
    public String getName() {
        return "hug";
    }

}
