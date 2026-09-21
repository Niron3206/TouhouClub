package ru.niron3206.cmds.music;

import ru.niron3206.audioplayer.GuildMusic;
import ru.niron3206.audioplayer.LavalinkManager;
import ru.niron3206.cmds.CommandContext;

import java.util.List;

public class SkipCommand extends MusicCommand {

    @Override
    protected void handleMusic(CommandContext ctx, GuildMusic music) {
        LavalinkManager.getInstance().nextTrack(ctx.getGuild().getIdLong());

        ctx.getChannel().sendMessage("⏭ Проигрывание текущего трека было прекращено!").queue();
    }

    @Override
    protected boolean requiresPlayingTrack() {
        return true;
    }

    @Override
    public String getName() {
        return "skip";
    }

    @Override
    public String getHelp() {
        return "Пропускает играющий трек.";
    }

    @Override
    public List<String> getAliases() {
        return List.of("s");
    }
}
