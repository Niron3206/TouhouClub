package ru.niron3206.cmds.music;

import ru.niron3206.Config;
import ru.niron3206.audioplayer.GuildMusic;
import ru.niron3206.cmds.CommandContext;

import java.util.List;

public class PauseCommand extends MusicCommand {

    @Override
    protected void handleMusic(CommandContext ctx, GuildMusic music) {
        if (player(ctx).getPaused()) {
            ctx.getChannel().sendMessage("⏸ Уже на паузе. Продолжить: `" + Config.prefix() + "resume`").queue();
            return;
        }

        updatePlayer(ctx, update -> update.setPaused(true), "⏸ Пауза");
    }

    @Override
    protected boolean requiresPlayingTrack() {
        return true;
    }

    @Override
    public String getName() {
        return "pause";
    }

    @Override
    public String getHelp() {
        return "Ставит трек на паузу";
    }

    @Override
    public List<String> getAliases() {
        return List.of("ps");
    }
}
