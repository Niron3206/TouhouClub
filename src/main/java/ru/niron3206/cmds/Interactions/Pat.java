package ru.niron3206.cmds.Interactions;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import ru.niron3206.cmds.CommandContext;
import ru.niron3206.cmds.ICommand;

import java.awt.Color;
import java.util.Random;

public class Pat implements ICommand {

    @Override
    public void handle(CommandContext ctx) {
        MessageReceivedEvent event = ctx.getEvent();
        Random random = new Random();
        EmbedBuilder pat = new EmbedBuilder();

        Member member;
        try {
            member = event.getMessage().getMentionedMembers().get(0);
        } catch (IndexOutOfBoundsException e) {
            pat.setTitle(":red_circle: Ты должен упомянуть того человека, которого хочешь погладить по голове!");
            pat.setColor(0xd60012);
            event.getChannel().sendMessageEmbeds(pat.build()).queue();
            pat.clear();
            return;
        }

        pat.setTitle("Время пэтта!");
        pat.setDescription(event.getAuthor().getAsMention() + " *гладит по голове* " + member.getAsMention());
        pat.setImage(String.format("https://cdn.nekos.life/pat/pat_0%02d.gif", random.nextInt(73) + 1));
        pat.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)).getRGB());

        event.getChannel().sendMessageEmbeds(pat.build()).queue();
        pat.clear();
    }

    @Override
    public String getHelp() {
        return "Хотите кого-то погладить по голове? То это команда для вас!";
    }

    @Override
    public String getName() {
        return "pat";
    }
}
