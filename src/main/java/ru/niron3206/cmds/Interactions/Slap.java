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
        String[] args = ctx.getArgs();
        MessageReceivedEvent event = ctx.getEvent();
        Random random = new Random();
        EmbedBuilder slap = new EmbedBuilder();

        if(args.length == 1){
            slap.setTitle(":red_circle: Ты должен упомянуть того человека, которого хочешь ударить по щеке!");
            slap.setColor(0xd60012);
        } else {
            try {

                Member member;

                switch (args[1].charAt(2)) {
                    case '!' -> member = event.getGuild().getMemberById(args[1].replace("<@!", "").replace(">", ""));
                    case '&' -> member = event.getGuild().getMemberById(args[1].replace("<@&", "").replace(">", ""));
                    default -> member = event.getGuild().getMemberById(args[1].replace("<@", "").replace(">", ""));
                }

                if (member == null) {
                    slap.setTitle(":red_circle: Ты должен упомянуть того человека, которого хочешь обнять!");
                    slap.setColor(0xd60012);
                } else {
                    slap.setTitle("Время ударить кого-то по щеке!");
                    slap.setDescription(event.getAuthor().getAsMention() + " *даёт пощёчину* " +   member.getAsMention());
                    slap.setImage(String.format("https://cdn.nekos.life/slap/slap_0%02d.gif", random.nextInt(15) + 1));
                    slap.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)).getRGB());
                }

            } catch (StringIndexOutOfBoundsException err) {
                slap.setTitle(":red_circle: Ты должен упомянуть того человека, которого хочешь ударить по щеке!");
                slap.setColor(0xd60012);
            }
        }
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
