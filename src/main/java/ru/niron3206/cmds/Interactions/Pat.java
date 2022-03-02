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
        String[] args = ctx.getArgs();
        MessageReceivedEvent event = ctx.getEvent();
        Random random = new Random();
        EmbedBuilder pat = new EmbedBuilder();

        if(args.length == 1){
            pat.setTitle(":red_circle: Ты должен упомянуть того человека, которого хочешь погладить по голове!");
            pat.setColor(0xd60012);
        } else {
            try {

                Member member;

                switch (args[1].charAt(2)) {
                    case '!' -> member = event.getGuild().getMemberById(args[1].replace("<@!", "").replace(">", ""));
                    case '&' -> member = event.getGuild().getMemberById(args[1].replace("<@&", "").replace(">", ""));
                    default -> member = event.getGuild().getMemberById(args[1].replace("<@", "").replace(">", ""));
                }

                assert member != null;
                pat.setTitle("Время пэтта!");
                pat.setDescription(event.getAuthor().getAsMention() + " *гладит по голове* " +   member.getAsMention());
                pat.setImage(String.format("https://cdn.nekos.life/pat/pat_0%02d.gif", random.nextInt(73) + 1));
                pat.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)).getRGB());

            } catch (Exception err) {
                pat.setTitle(":red_circle: Ты должен упомянуть того человека, которого хочешь погладить по голове!");
                pat.setColor(0xd60012);
            }
        }
        event.getChannel().sendMessageEmbeds(pat.build()).queue();
        pat.clear();
    }

    @Override
    public String getName() {
        return "pat";
    }
}
