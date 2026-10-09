package ru.niron3206.cmds.music;

import ru.niron3206.audioplayer.GuildMusic;
import ru.niron3206.cmds.CommandContext;

import java.util.List;

public class ResumeCommand extends MusicCommand {

    @Override
    protected void handleMusic(CommandContext ctx, GuildMusic music) {
        if (!player(ctx).getPaused()) {
            ctx.getChannel().sendMessage("▶ И так играет!").queue();
            return;
        }

        updatePlayer(ctx, update -> update.setPaused(false), "▶ Продолжаю");
    }

    @Override
    protected boolean requiresPlayingTrack() {
        return true;
    }

    @Override
    public String getName() {
        return "resume";
    }

    @Override
    public String getHelp() {
        return "Снимает трек с паузы";
    }

    @Override
    public List<String> getAliases() {
        return List.of("rs");
    }
}
