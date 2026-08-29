package ru.niron3206.cmds.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import ru.niron3206.audioplayer.MusicManager;
import ru.niron3206.cmds.CommandContext;

import java.util.List;

public class NowPlayingCommand extends MusicCommand {

    @Override
    protected void handleMusic(CommandContext ctx, MusicManager musicManager) {
        AudioTrack track = musicManager.audioPlayer.getPlayingTrack();
        AudioTrackInfo info = track.getInfo();

        ctx.getChannel()
                .sendMessageFormat("🎵 Сейчас играет: `%s`\nАвтор: `%s`\n(Ссылка: <%s>)", info.title, info.author, info.uri)
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
