package ru.niron3206.cmds.music;

import dev.arbjerg.lavalink.protocol.v4.TrackInfo;
import ru.niron3206.audioplayer.GuildMusic;
import ru.niron3206.cmds.CommandContext;

import java.util.List;

public class NowPlayingCommand extends MusicCommand {

    @Override
    protected void handleMusic(CommandContext ctx, GuildMusic music) {
        TrackInfo info = playingTrack(ctx).getInfo();

        ctx.getChannel()
                .sendMessageFormat("🎵 Сейчас играет: `%s`\nАвтор: `%s`\n(Ссылка: <%s>)",
                        info.getTitle(), info.getAuthor(), info.getUri())
                .queue();
    }

    @Override
    protected boolean requiresPlayingTrack() {
        return true;
    }

    @Override
    public String getName() {
        return "nowplaying";
    }

    @Override
    public String getHelp() {
        return "Показывает проигрывающийся трек";
    }

    @Override
    public List<String> getAliases() {
        return List.of("np");
    }
}
