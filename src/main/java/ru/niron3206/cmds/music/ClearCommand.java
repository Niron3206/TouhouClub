package ru.niron3206.cmds.music;

import ru.niron3206.audioplayer.GuildMusic;
import ru.niron3206.cmds.CommandContext;

import java.util.List;

public class ClearCommand extends MusicCommand {

    @Override
    protected void handleMusic(CommandContext ctx, GuildMusic music) {
        int count = music.clear();

        if (count == 0) {
            ctx.getChannel().sendMessage("Очередь и так пуста").queue();
            return;
        }

        ctx.getChannel().sendMessage("🧹 Очередь очищена, убрано треков: `" + count + "`").queue();
    }

    @Override
    public String getName() {
        return "clear";
    }

    @Override
    public String getHelp() {
        return "Очищает очередь, текущий трек доигрывает";
    }

    @Override
    public List<String> getAliases() {
        return List.of("c");
    }
}
