package ru.niron3206.cmds.music;

import ru.niron3206.audioplayer.GuildMusic;
import ru.niron3206.cmds.CommandContext;

import java.util.List;

public class ShuffleCommand extends MusicCommand {

    @Override
    protected void handleMusic(CommandContext ctx, GuildMusic music) {
        int count = music.shuffle();

        if (count == 0) {
            ctx.getChannel().sendMessage("Очередь пуста").queue();
            return;
        }

        if (count == 1) {
            ctx.getChannel().sendMessage("В очереди один трек, перемешивать нечего").queue();
            return;
        }

        ctx.getChannel().sendMessage("🔀 Очередь перемешана, треков: `" + count + "`").queue();
    }

    @Override
    public String getName() {
        return "shuffle";
    }

    @Override
    public String getHelp() {
        return "Перемешивает очередь";
    }

    @Override
    public List<String> getAliases() {
        return List.of("sh");
    }
}
