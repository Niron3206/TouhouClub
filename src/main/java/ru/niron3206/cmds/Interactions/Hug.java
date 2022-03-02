package ru.niron3206.cmds.Interactions;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import ru.niron3206.cmds.CommandContext;
import ru.niron3206.cmds.ICommand;

import java.util.Random;

public class Hug implements ICommand {

    @Override
    public void handle(CommandContext ctx) {
        String[] args = ctx.getArgs();
        MessageReceivedEvent event = ctx.getEvent();
        Random random = new Random();
        EmbedBuilder hug = new EmbedBuilder();

        if(args.length == 1){
            hug.setTitle(":red_circle: Ты должен упомянуть того человека, которого хочешь обнять!");
            hug.setColor(0xd60012);
        } else {
            try {

                Member member;

                switch (args[1].charAt(2)) {
                    case '!' -> member = event.getGuild().getMemberById(args[1].replace("<@!", "").replace(">", ""));
                    case '&' -> member = event.getGuild().getMemberById(args[1].replace("<@&", "").replace(">", ""));
                    default -> member = event.getGuild().getMemberById(args[1].replace("<@", "").replace(">", ""));
                }

                assert member != null;
                hug.setTitle("Неожиданные обнимашки!");
                hug.setDescription(event.getAuthor().getAsMention() + " *обнимает* " + member.getAsMention());
                hug.setImage(String.format("https://cdn.nekos.life/hug/hug_0%02d.gif", random.nextInt(88) + 1));

            } catch (Exception err) {
                hug.setTitle(":red_circle: Ты должен упомянуть того человека, которого хочешь обнять!");
                hug.setColor(0xd60012);
            }
        }
        event.getChannel().sendMessageEmbeds(hug.build()).queue();
        hug.clear();
    }

    @Override
    public String getName() {
        return "hug";
    }

}
