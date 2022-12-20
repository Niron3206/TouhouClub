package ru.niron3206.cmds.Interactions;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import ru.niron3206.cmds.CommandContext;
import ru.niron3206.cmds.ICommand;

import java.awt.Color;
import java.util.Random;

public class Slap implements ICommand {

    @Override
    public void handle(CommandContext ctx) {
        MessageReceivedEvent event = ctx.getEvent();
        Random random = new Random();
        EmbedBuilder slap = new EmbedBuilder();

        Member member;
        try {
            member = event.getMessage().getMentionedMembers().get(0);
        } catch (IndexOutOfBoundsException e) {
            slap.setTitle(":red_circle: Ты должен упомянуть того человека, которого хочешь ударить по щеке!");
            slap.setColor(0xd60012);
            event.getChannel().sendMessageEmbeds(slap.build()).queue();
            slap.clear();
            return;
        }

        slap.setTitle("Время ударить кого-то по щеке!");
        slap.setDescription(event.getAuthor().getAsMention() + " *даёт пощёчину* " + member.getAsMention());
        slap.setImage(String.format("https://cdn.nekos.life/slap/slap_0%02d.gif", random.nextInt(15) + 1));
        slap.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)).getRGB());

        event.getChannel().sendMessageEmbeds(slap.build()).queue();
        slap.clear();
    }

    @Override
    public String getHelp() {
        return "Если вы хотите выразить свои эмоции без мата, то используйте эту команду.";
    }

    @Override
    public String getName() {
        return "slap";
    }
}
